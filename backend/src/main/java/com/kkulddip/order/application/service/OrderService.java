package com.kkulddip.order.application.service;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.command.AddOrderItemCommand;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.domain.repository.OrderRepository;
import com.kkulddip.order.domain.service.OrderIdGenerator;
import com.kkulddip.order.application.exception.OrderException;

@RequiredArgsConstructor
@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderIdGenerator orderIdGenerator;
    
    /**
     * 주문 생성
     * 
     * @param customerId 고객 ID
     * @param storeId 가게 ID
     * @param orderItemCommands 주문 아이템 커맨드 리스트
     * @return 생성된 주문
     */
    @Transactional
    public Order createOrder(CustomerId customerId, StoreId storeId, List<AddOrderItemCommand> orderItemCommands) {
        try {
            if (orderItemCommands == null || orderItemCommands.isEmpty()) {
                throw OrderException.orderEmptyItems("N/A");
            }
            
            OrderId orderId = orderIdGenerator.generate();
            LocalDateTime orderDate = LocalDateTime.now();
            
            Order order = Order.create(orderId, customerId, storeId, orderDate);
            
            for (AddOrderItemCommand command : orderItemCommands) {
                order.addOrderItem(command);
            }
            
            return order;
        } catch (Exception e) {
            if (e instanceof OrderException) {
                throw e;
            }
            throw OrderException.orderCreationFailed(e);
        }
    }
    
    /**
     * 주문 조회
     * 
     * @param orderId 주문 ID
     * @return 주문
     */
    public Order findByOrderId(OrderId orderId) {
        return orderRepository.findByOrderId(orderId)
            .orElseThrow(() -> OrderException.orderNotFound(String.valueOf(orderId.value())));
    }
    
    /**
     * 주문 저장
     * 
     * @param order 주문
     * @return 저장된 주문
     */
    public Order save(Order order) {
        try {
            return orderRepository.save(order);
        } catch (Exception e) {
            throw OrderException.orderPersistenceError(String.valueOf(order.getOrderId().value()), e);
        }
    }
    
    /**
     * 주문 상태 변경
     * 
     * @param order 주문
     * @param newStatus 새로운 상태
     */
    public void changeOrderStatus(Order order, OrderStatus newStatus) {
        try {
            order.changeStatus(newStatus);
            orderRepository.save(order);
        } catch (Exception e) {
            throw OrderException.orderUpdateFailed(String.valueOf(order.getOrderId().value()), e);
        }
    }
    
    /**
     * 고객 ID로 주문 목록 조회
     * 
     * @param customerId 고객 ID
     * @return 주문 목록
     */
    public List<Order> findByCustomerId(CustomerId customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    /**
     * 가게 ID로 주문 목록 조회
     * 
     * @param storeId 가게 ID
     * @return 주문 목록
     */
    public List<Order> findByStoreId(StoreId storeId) {
        return orderRepository.findByStoreId(storeId);
    }
    
    /**
     * 가게 ID와 주문 상태로 주문 목록 조회
     * 
     * @param storeId 가게 ID
     * @param orderStatus 주문 상태
     * @return 주문 목록
     */
    public List<Order> findByStoreIdAndOrderStatus(StoreId storeId, OrderStatus orderStatus) {
        return orderRepository.findByStoreIdAndOrderStatus(storeId, orderStatus);
    }
    
    /**
     * 주문 존재 여부 확인
     * 
     * @param orderId 주문 ID
     * @return 존재 여부
     */
    public boolean existsByOrderId(OrderId orderId) {
        return orderRepository.existsByOrderId(orderId);
    }
    
    /**
     * 가게의 대기 중인 주문 목록 조회
     * 
     * @param storeId 가게 ID
     * @return AWAITING_CONFIRMATION 상태의 주문 목록
     */
    public List<Order> findPendingOrdersByStore(StoreId storeId) {
        return orderRepository.findByStoreIdAndOrderStatus(storeId, OrderStatus.AWAITING_CONFIRMATION);
    }
    
    /**
     * 주문 확정 처리 (픽업 시간 설정 포함)
     * 
     * @param order 주문
     * @param pickupTime 픽업 시간
     */
    public void confirmOrder(Order order, LocalDateTime pickupTime) {
        try {
            if (pickupTime != null) {
                order.setPickupTime(pickupTime);
            }
            order.changeStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        } catch (Exception e) {
            throw OrderException.orderUpdateFailed(String.valueOf(order.getOrderId().value()), e);
        }
    }
    
    /**
     * 주문 거절 처리
     * 
     * @param order 주문
     */
    public void rejectOrder(Order order) {
        try {
            order.changeStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        } catch (Exception e) {
            throw OrderException.orderUpdateFailed(String.valueOf(order.getOrderId().value()), e);
        }
    }
    
    /**
     * 주문 픽업 완료 처리
     * 
     * @param order 주문
     */
    public void markOrderAsPickedUp(Order order) {
        try {
            order.changeStatus(OrderStatus.PICKED_UP);
            orderRepository.save(order);
        } catch (Exception e) {
            throw OrderException.orderUpdateFailed(String.valueOf(order.getOrderId().value()), e);
        }
    }
    
    /**
     * 가게 ID로 PAYMENT_PENDING 이후 상태의 주문 목록 조회
     * (PAID, AWAITING_CONFIRMATION, CONFIRMED, PICKED_UP 상태)
     * 
     * @param storeId 가게 ID
     * @return 주문 목록
     */
    public List<Order> findByStoreIdAfterPaymentPending(StoreId storeId) {
        return orderRepository.findByStoreIdAfterPaymentPending(storeId);
    }
}
