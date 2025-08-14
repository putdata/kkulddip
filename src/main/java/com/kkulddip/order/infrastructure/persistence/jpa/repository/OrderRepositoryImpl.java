package com.kkulddip.order.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.domain.repository.OrderRepository;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.OrderItemEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.entity.DiscountInfoEntity;
import com.kkulddip.order.infrastructure.persistence.jpa.mapper.OrderEntityMapper;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.order.domain.model.entity.DiscountInfo;
import com.kkulddip.order.application.exception.OrderException;

@Slf4j
@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final DiscountInfoJpaRepository discountInfoJpaRepository;
    private final OrderEntityMapper orderEntityMapper;
    
    @Override
    @Transactional
    public Order save(Order order) {
        log.debug("주문 저장 시작 - orderId: {}", order.getOrderId().value());
        
        try {
            // 1. Order Entity 저장
            OrderEntity orderEntity = orderEntityMapper.toEntity(order);
            OrderEntity savedOrderEntity = orderJpaRepository.save(orderEntity);
            log.debug("주문 Entity 저장 완료 - entityId: {}", savedOrderEntity.getOrderId());
            
            // 2. OrderItem들 저장
            for (OrderItem orderItem : order.getOrderItems()) {
                OrderItemEntity orderItemEntity = orderEntityMapper.toOrderItemEntity(orderItem, savedOrderEntity);
                OrderItemEntity savedOrderItemEntity = orderItemJpaRepository.save(orderItemEntity);
                log.debug("주문 아이템 저장 완료 - orderItemId: {}", savedOrderItemEntity.getOrderItemId());
                
                // 3. 각 OrderItem의 DiscountInfo들 저장 (null 체크 추가)
                List<DiscountInfo> discountInfos = orderItem.getDiscountInfos();
                if (discountInfos != null && !discountInfos.isEmpty()) {
                    for (DiscountInfo discountInfo : discountInfos) {
                        DiscountInfoEntity discountInfoEntity = orderEntityMapper.toDiscountInfoEntity(discountInfo, savedOrderItemEntity);
                        DiscountInfoEntity savedDiscountInfoEntity = discountInfoJpaRepository.save(discountInfoEntity);
                        log.debug("할인 정보 저장 완료 - discountInfoId: {}", savedDiscountInfoEntity.getDiscountInfoId());
                    }
                } else {
                    log.debug("할인 정보 없음 - orderItemId: {}", savedOrderItemEntity.getOrderItemId());
                }
            }
            
            log.debug("주문 전체 저장 완료 - orderId: {}", savedOrderEntity.getOrderId());
            return orderEntityMapper.toDomain(savedOrderEntity);
        } catch (Exception e) {
            log.error("주문 데이터베이스 저장 작업 실패 - orderId: {}, operation: save, details: {}", 
                order.getOrderId().value(), e.getMessage(), e);
            throw OrderException.orderDatabaseError(e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderId(OrderId orderId) {
        log.debug("주문 조회 시작 - orderId: {}", orderId.value());
        
        try {
            Optional<OrderEntity> orderEntity = orderJpaRepository.findByOrderId(orderId.value());
            
            if (orderEntity.isPresent()) {
                log.debug("주문 조회 성공 - orderId: {}", orderId.value());
                return Optional.of(orderEntityMapper.toDomain(orderEntity.get()));
            } else {
                log.debug("주문 조회 실패 - orderId: {}", orderId.value());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("주문 데이터베이스 조회 작업 실패 - orderId: {}, operation: findByOrderId, details: {}", 
                orderId.value(), e.getMessage(), e);
            throw OrderException.orderDatabaseError(e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByCustomerId(CustomerId customerId) {
        log.debug("고객 주문 목록 조회 - customerId: {}", customerId.value());
        
        List<OrderEntity> orderEntities = orderJpaRepository.findByCustomerId(customerId.value());
        
        List<Order> orders = orderEntities.stream()
            .map(orderEntityMapper::toDomain)
            .collect(Collectors.toList());
        
        log.debug("고객 주문 목록 조회 완료 - customerId: {}, count: {}", 
            customerId.value(), orders.size());
        
        return orders;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStoreId(StoreId storeId) {
        log.debug("가게 주문 목록 조회 - storeId: {}", storeId.value());
        
        List<OrderEntity> orderEntities = orderJpaRepository.findByStoreId(storeId.value());
        
        List<Order> orders = orderEntities.stream()
            .map(orderEntityMapper::toDomain)
            .collect(Collectors.toList());
        
        log.debug("가게 주문 목록 조회 완료 - storeId: {}, count: {}", 
            storeId.value(), orders.size());
        
        return orders;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStoreIdAndOrderStatus(StoreId storeId, OrderStatus orderStatus) {
        log.debug("가게별 상태별 주문 목록 조회 - storeId: {}, status: {}", 
            storeId.value(), orderStatus);
        
        List<OrderEntity> orderEntities = orderJpaRepository.findByStoreIdAndOrderStatus(
            storeId.value(), orderStatus);
        
        List<Order> orders = orderEntities.stream()
            .map(orderEntityMapper::toDomain)
            .collect(Collectors.toList());
        
        log.debug("가게별 상태별 주문 목록 조회 완료 - storeId: {}, status: {}, count: {}", 
            storeId.value(), orderStatus, orders.size());
        
        return orders;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByCustomerIdAndOrderStatus(CustomerId customerId, OrderStatus orderStatus) {
        log.debug("고객별 상태별 주문 목록 조회 - customerId: {}, status: {}", 
            customerId.value(), orderStatus);
        
        List<OrderEntity> orderEntities = orderJpaRepository.findByCustomerIdAndOrderStatus(
            customerId.value(), orderStatus);
        
        List<Order> orders = orderEntities.stream()
            .map(orderEntityMapper::toDomain)
            .collect(Collectors.toList());
        
        log.debug("고객별 상태별 주문 목록 조회 완료 - customerId: {}, status: {}, count: {}", 
            customerId.value(), orderStatus, orders.size());
        
        return orders;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByOrderStatus(OrderStatus orderStatus) {
        log.debug("상태별 주문 목록 조회 - status: {}", orderStatus);
        
        List<OrderEntity> orderEntities = orderJpaRepository.findByOrderStatus(orderStatus);
        
        List<Order> orders = orderEntities.stream()
            .map(orderEntityMapper::toDomain)
            .collect(Collectors.toList());
        
        log.debug("상태별 주문 목록 조회 완료 - status: {}, count: {}", 
            orderStatus, orders.size());
        
        return orders;
    }
    
    @Override
    @Transactional
    public void deleteByOrderId(OrderId orderId) {
        log.debug("주문 삭제 시작 - orderId: {}", orderId.value());
        
        try {
            orderJpaRepository.deleteByOrderId(orderId.value());
            log.debug("주문 삭제 완료 - orderId: {}", orderId.value());
        } catch (Exception e) {
            log.error("주문 데이터베이스 삭제 작업 실패 - orderId: {}, operation: deleteByOrderId, details: {}", 
                orderId.value(), e.getMessage(), e);
            throw OrderException.orderDatabaseError(e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrderId(OrderId orderId) {
        log.debug("주문 존재 여부 확인 - orderId: {}", orderId.value());
        
        boolean exists = orderJpaRepository.existsByOrderId(orderId.value());
        
        log.debug("주문 존재 여부 확인 완료 - orderId: {}, exists: {}", 
            orderId.value(), exists);
        
        return exists;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderIdAndCustomerId(OrderId orderId, CustomerId customerId) {
        log.debug("주문 조회 시작 - orderId: {}, customerId: {}", 
            orderId.value(), customerId.value());
        
        try {
            Optional<OrderEntity> orderEntity = orderJpaRepository.findByOrderIdAndCustomerId(
                orderId.value(), customerId.value());
            
            if (orderEntity.isPresent()) {
                log.debug("주문 조회 성공 - orderId: {}, customerId: {}", 
                    orderId.value(), customerId.value());
                return Optional.of(orderEntityMapper.toDomain(orderEntity.get()));
            } else {
                log.debug("주문 조회 결과 없음 - orderId: {}, customerId: {}", 
                    orderId.value(), customerId.value());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("주문 데이터베이스 조회 작업 실패 - orderId: {}, customerId: {}, operation: findByOrderIdAndCustomerId, details: {}", 
                orderId.value(), customerId.value(), e.getMessage(), e);
            throw OrderException.orderDatabaseError(e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStoreIdAfterPaymentPending(StoreId storeId) {
        log.debug("가게 PAYMENT_PENDING 이후 주문 목록 조회 - storeId: {}", storeId.value());
        
        try {
            List<OrderEntity> orderEntities = orderJpaRepository.findByStoreIdAfterPaymentPending(storeId.value());
            
            List<Order> orders = orderEntities.stream()
                .map(orderEntityMapper::toDomain)
                .collect(Collectors.toList());
            
            log.debug("가게 PAYMENT_PENDING 이후 주문 목록 조회 완료 - storeId: {}, count: {}", 
                storeId.value(), orders.size());
            
            return orders;
        } catch (Exception e) {
            log.error("주문 데이터베이스 조회 작업 실패 - storeId: {}, operation: findByStoreIdAfterPaymentPending, details: {}", 
                storeId.value(), e.getMessage(), e);
            throw OrderException.orderDatabaseError(e);
        }
    }
}