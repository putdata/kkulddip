package com.kkulddip.order.application.facade;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kkulddip.common.lock.DistributedLock;
import com.kkulddip.order.application.mapper.OrderMapper;
import com.kkulddip.order.application.service.NotificationService;
import com.kkulddip.order.application.service.OrderService;
import com.kkulddip.order.application.service.EventPublisher;
import com.kkulddip.order.application.service.PriceValidationService;
import com.kkulddip.order.application.service.StoreAuthService;
import com.kkulddip.order.application.service.StoreService;
import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.command.AddOrderItemCommand;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.common.event.OrderCreatedEvent;
import com.kkulddip.order.application.dto.request.HandleOrderConfirmedRequest;
import com.kkulddip.order.application.dto.request.HandlePaymentResultRequest;
import com.kkulddip.order.application.exception.OrderException;
import com.kkulddip.order.presentation.rest.dto.request.ConfirmationAction;
import com.kkulddip.order.presentation.rest.dto.request.CreateOrderRequest;
import com.kkulddip.order.presentation.rest.dto.request.OrderConfirmationRequest;
import com.kkulddip.order.presentation.rest.dto.response.CreateOrderResponse;
import com.kkulddip.order.presentation.rest.dto.response.CustomerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.OrderConfirmationResponse;
import com.kkulddip.order.presentation.rest.dto.response.PendingOrderResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderFacade {
    
    private final OrderService orderService;
    private final EventPublisher eventPublisher;
    private final StoreService storeService;
    private final StoreAuthService storeAuthService;
    private final NotificationService notificationService;
    private final OrderMapper orderMapper;
    private final PriceValidationService priceValidationService;
    private final DistributedLock distributedLock;
    
    /**
     * 1. 사용자로부터 주문 요청 처리
     * - 주문 생성
     * - 할인된 가격 검증
     * - 주문 생성 이벤트 발행 (결제 서버로 알림)
     * - 주문 상태 변경 (결제 Pending)
     * - 사용자에게 응답 반환
     */
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        log.info("주문 생성 요청 시작 - customerId: {}, storeId: {}", request.customerId(), request.storeId());
        
        String lockKey = "order:create:" + request.customerId();
        
        if (!distributedLock.tryLock(lockKey)) {
            log.warn("⚠️ 주문 생성 중복 요청 감지: customerId={}", request.customerId());
            throw OrderException.orderAlreadyProcessing(request.customerId());
        }
        
        try {
            // 1. 요청 데이터를 도메인 객체로 변환
            CustomerId customerId = orderMapper.toCustomerId(request.customerId());
            StoreId storeId = orderMapper.toStoreId(request.storeId());
            List<AddOrderItemCommand> orderItemCommands = orderMapper.toAddOrderItemCommands(request.orderItems());
            
            // 2. 가격 검증 (Store 도메인과 대조)
            priceValidationService.validatePrices(request.storeId(), request.orderItems());
            log.info("가격 검증 완료 - storeId: {}", request.storeId());

            // 3. 주문 생성 (주문 아이템 추가)
            Order order = orderService.createOrder(customerId, storeId, orderItemCommands);
            log.info("주문 생성 완료 - orderId: {}", order.getOrderId().value());
            
            // 4. 주문 저장
            Order savedOrder = orderService.save(order);

            // 5. 주문 상태 변경 (결제 Pending)
            orderService.changeOrderStatus(savedOrder, OrderStatus.PAYMENT_PENDING);
            log.info("주문 상태 변경 완료 - orderId: {}, status: PAYMENT_PENDING", 
                savedOrder.getOrderId().value());
            
            // 6. 주문 생성 이벤트 발행 (결제 서버로 알림)
            OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(savedOrder.getOrderId().value())
                .amount(savedOrder.getFinalPrice().amount())
                .customerId(savedOrder.getCustomerId().value())
                .build();
            eventPublisher.publishOrderCreated(orderCreatedEvent);
            log.info("주문 생성 이벤트 발행 완료 - orderId: {}, amount: {}, customerId: {}", 
                savedOrder.getOrderId().value(), savedOrder.getFinalPrice().amount(), savedOrder.getCustomerId().value());
            
            // 7. 응답 반환
            return orderMapper.toCreateOrderResponse(savedOrder);
            
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생 - customerId: {}, storeId: {}", 
                request.customerId(), request.storeId(), e);
            throw OrderException.orderCreationFailed(e);
        } finally {
            distributedLock.unlock(lockKey);
        }
    }
    
    /**
     * 2. 결제 결과 이벤트 수신 처리
     * - 이벤트의 주문 번호로 Order Entity 찾기
     * - 주문 상태 변경 (결제 완료 — 주문 확인 중_사장님)
     * - Store 쪽으로 해당 주문 확정 요청
     * - 푸시 알림 요청 (system → owner)
     * - 푸시 알림 요청 (system → user)
     */
    @Transactional
    public void handlePaymentResult(HandlePaymentResultRequest request) {
        log.info("결제 결과 이벤트 처리 시작 - orderId: {}, status: {}", request.orderId(), request.status());
        
        try {
            // 1. 주문 조회
            OrderId orderId = OrderId.of(request.orderId());
            Order order = orderService.findByOrderId(orderId);
            
            // 2. 결제 상태에 따른 분기 처리
            if ("ABORTED".equals(request.status())) {
                // 결제 실패 처리
                orderService.changeOrderStatus(order, OrderStatus.FAILED);
                log.info("주문 상태 변경 완료 - orderId: {}, status: FAILED", 
                    order.getOrderId().value());
                
                // 고객에게 결제 실패 알림
                notificationService.sendNotificationToCustomer(
                    order.getCustomerId().value(),
                    "결제가 실패하였습니다. 주문이 취소되었습니다."
                );
                
                log.info("결제 실패 처리 완료 - orderId: {}", request.orderId());
                return;
            }
            
            // 3. 주문 상태 변경 (결제 완료 - PAID)
            orderService.changeOrderStatus(order, OrderStatus.PAID);
            log.info("주문 상태 변경 완료 - orderId: {}, status: PAID", 
                order.getOrderId().value());
            
            // 4. Store 쪽으로 주문 확정 요청
            storeService.requestOrderConfirmation(
                order.getOrderId().value(),
                order.getStoreId().value()
            );
            log.info("가게 주문 확정 요청 완료 - orderId: {}, storeId: {}", 
                order.getOrderId().value(), order.getStoreId().value());

            // 5. 주문 상태 변경 (주문 확정 요청 중 - AWAITING_CONFIRMATION)
            orderService.changeOrderStatus(order, OrderStatus.AWAITING_CONFIRMATION);
            log.info("주문 상태 변경 완료 - orderId: {}, status: AWAITING_CONFIRMATION", 
                order.getOrderId().value());
            
            // 6. 푸시 알림 요청 (system → owner)
            notificationService.sendNotificationToOwner(
                order.getStoreId().value(),
                "새로운 주문이 들어왔습니다. 주문번호: " + order.getOrderId().value()
            );
            
            // 7. 푸시 알림 요청 (system → user)
            notificationService.sendNotificationToCustomer(
                order.getCustomerId().value(),
                "결제가 완료되었습니다. 가게에서 주문을 확인 중입니다."
            );
            
            log.info("결제 결과 이벤트 처리 완료 - orderId: {}", request.orderId());
            
        } catch (Exception e) {
            log.error("결제 결과 처리 중 오류 발생 - orderId: {}, status: {}", 
                request.orderId(), request.status(), e);
            throw OrderException.orderUpdateFailed(String.valueOf(request.orderId()), e);
        }
    }
    
    /**
     * 3. 주문 확정 이벤트 수신 처리
     * - 이벤트의 주문 번호로 Order Entity 찾기
     * - 주문 상태 변경 (주문 완료)
     * - 푸시 알림 요청 (system → user)
     */
    @Transactional
    public void handleOrderConfirmed(HandleOrderConfirmedRequest request) {
        log.info("주문 확정 이벤트 처리 시작 - orderId: {}, storeId: {}", 
            request.orderId(), request.storeId());
        
        try {
            // 1. 주문 조회
            OrderId orderId = OrderId.of(request.orderId());
            Order order = orderService.findByOrderId(orderId);
            
            // 2. 주문 상태 변경 (주문 완료)
            orderService.changeOrderStatus(order, OrderStatus.CONFIRMED);
            log.info("주문 상태 변경 완료 - orderId: {}, status: CONFIRMED", 
                order.getOrderId().value());
            
            // 3. 푸시 알림 요청 (system → user)
            notificationService.sendNotificationToCustomer(
                order.getCustomerId().value(),
                "주문이 확정되었습니다. 음식을 준비 중입니다."
            );
            
            log.info("주문 확정 이벤트 처리 완료 - orderId: {}", request.orderId());
            
        } catch (Exception e) {
            log.error("주문 확정 처리 중 오류 발생 - orderId: {}, storeId: {}", 
                request.orderId(), request.storeId(), e);
            throw OrderException.orderUpdateFailed(String.valueOf(request.orderId()), e);
        }
    }
    
    /**
     * 4. 가게의 대기 중인 주문 목록 조회 (사장님용)
     * - 권한 검증 후 특정 가게의 AWAITING_CONFIRMATION 상태 주문들을 조회
     */
    public List<PendingOrderResponse> getPendingOrdersByStore(Long ownerId, StoreId storeId) {
        log.info("가게 대기 주문 조회 시작 - ownerId: {}, storeId: {}", ownerId, storeId.value());
        
        try {
            // 권한 검증: 사장님이 해당 가게를 소유하고 있는지 확인
            storeAuthService.validateOwnerPermission(ownerId, storeId);
            
            List<Order> pendingOrders = orderService.findPendingOrdersByStore(storeId);
            log.info("가게 대기 주문 조회 완료 - ownerId: {}, storeId: {}, count: {}", 
                ownerId, storeId.value(), pendingOrders.size());
            
            return orderMapper.toPendingOrderResponses(pendingOrders);
            
        } catch (Exception e) {
            if (e instanceof OrderException) {
                throw e;
            }
            log.error("가게 대기 주문 조회 중 오류 발생 - ownerId: {}, storeId: {}", ownerId, storeId.value(), e);
            throw OrderException.orderDatabaseError(e);
        }
    }
    
    /**
     * 5. 주문 확정/거절 처리 (사장님용)
     * - 권한 검증: 해당 주문의 가게를 사장님이 소유하고 있는지 확인
     * - 주문 확정 시 픽업 시간 설정
     * - 주문 거절 시 취소 처리
     * - 고객에게 알림 발송
     */
    @Transactional
    public OrderConfirmationResponse processOrderConfirmation(Long ownerId, OrderId orderId, OrderConfirmationRequest request) {
        log.info("주문 확정/거절 처리 시작 - ownerId: {}, orderId: {}, action: {}", 
            ownerId, orderId.value(), request.action());
        
        try {
            // 1. 주문 조회
            Order order = orderService.findByOrderId(orderId);
            
            // 2. 권한 검증: 사장님이 해당 가게를 소유하고 있는지 확인
            storeAuthService.validateOwnerPermission(ownerId, order.getStoreId());
            
            // 3. 주문 상태 확인
            if (!order.isAwaitingConfirmation()) {
                log.warn("주문 확정 불가 상태 - orderId: {}, status: {}", 
                    orderId.value(), order.getOrderStatus());
                throw OrderException.orderInvalidStatus(String.valueOf(orderId.value()), order.getOrderStatus().name());
            }
            
            // 4. 확정/거절 처리
            if (request.action() == ConfirmationAction.CONFIRM) {
                orderService.confirmOrder(order, request.pickupTime());
                log.info("주문 확정 완료 - orderId: {}, pickupTime: {}", 
                    orderId.value(), request.pickupTime());
                
                // 고객에게 확정 알림
                String notificationMessage = "주문이 확정되었습니다.";
                if (request.pickupTime() != null) {
                    java.time.ZonedDateTime seoulTime = request.pickupTime().atZone(java.time.ZoneId.of("Asia/Seoul"));
                    int hour = seoulTime.getHour();
                    int minute = seoulTime.getMinute();
                    notificationMessage += " " + hour + "시 " + minute + "분까지 픽업 준비될 예정이에요.";
                }

                notificationService.sendNotificationToCustomer(
                    order.getCustomerId().value(),
                    notificationMessage
                );
                
            } else {
                orderService.rejectOrder(order);
                log.info("주문 거절 완료 - orderId: {}, reason: {}", 
                    orderId.value(), request.rejectionReason());
                
                // 고객에게 거절 알림
                String message = "주문이 거절되었습니다.";
                if (request.rejectionReason() != null && !request.rejectionReason().trim().isEmpty()) {
                    message += " (" + request.rejectionReason() + ")";
                }
                notificationService.sendNotificationToCustomer(
                    order.getCustomerId().value(),
                    message
                );
            }
            
            // 5. 응답 반환
            return orderMapper.toOrderConfirmationResponse(order);
            
        } catch (Exception e) {
            if (e instanceof OrderException) {
                throw e;
            }
            log.error("주문 확정/거절 처리 중 오류 발생 - ownerId: {}, orderId: {}", 
                ownerId, orderId.value(), e);
            throw OrderException.orderUpdateFailed(String.valueOf(orderId.value()), e);
        }
    }
    
    /**
     * 6. 고객 주문 내역 조회
     * - 권한 검증: 본인의 주문만 조회 가능
     * - 고객 ID로 주문 목록 조회
     */
    public List<CustomerOrderHistoryResponse> getCustomerOrderHistory(Long customerId) {
        log.info("고객 주문 내역 조회 시작 - customerId: {}", customerId);
        
        try {
            CustomerId customerIdVO = CustomerId.of(customerId);
            List<Order> orders = orderService.findByCustomerId(customerIdVO);
            
            log.info("고객 주문 내역 조회 완료 - customerId: {}, count: {}", customerId, orders.size());
            
            return orderMapper.toCustomerOrderHistoryResponses(orders);
            
        } catch (Exception e) {
            log.error("고객 주문 내역 조회 중 오류 발생 - customerId: {}", customerId, e);
            throw OrderException.orderDatabaseError(e);
        }
    }
}
