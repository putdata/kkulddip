package com.kkulddip.order.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.model.vo.StoreId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 주문 도메인 저장소 인터페이스
 */
public interface OrderRepository {
    
    /**
     * 주문 저장
     * 
     * @param order 저장할 주문
     * @return 저장된 주문
     */
    Order save(Order order);
    
    /**
     * 주문 ID로 주문 조회
     * 
     * @param orderId 주문 ID
     * @return 주문 (Optional)
     */
    Optional<Order> findByOrderId(OrderId orderId);
    
    /**
     * 고객 ID로 주문 목록 조회
     * 
     * @param customerId 고객 ID
     * @return 주문 목록
     */
    List<Order> findByCustomerId(CustomerId customerId);
    
    /**
     * 가게 ID로 주문 목록 조회
     * 
     * @param storeId 가게 ID
     * @return 주문 목록
     */
    List<Order> findByStoreId(StoreId storeId);
    
    /**
     * 가게 ID와 주문 상태로 주문 목록 조회
     * 
     * @param storeId 가게 ID
     * @param orderStatus 주문 상태
     * @return 주문 목록
     */
    List<Order> findByStoreIdAndOrderStatus(StoreId storeId, OrderStatus orderStatus);
    
    /**
     * 고객 ID와 주문 상태로 주문 목록 조회
     * 
     * @param customerId 고객 ID
     * @param orderStatus 주문 상태
     * @return 주문 목록
     */
    List<Order> findByCustomerIdAndOrderStatus(CustomerId customerId, OrderStatus orderStatus);
    
    /**
     * 주문 상태로 주문 목록 조회
     * 
     * @param orderStatus 주문 상태
     * @return 주문 목록
     */
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    
    /**
     * 주문 삭제
     * 
     * @param orderId 주문 ID
     */
    void deleteByOrderId(OrderId orderId);
    
    /**
     * 주문 존재 여부 확인
     * 
     * @param orderId 주문 ID
     * @return 존재 여부
     */
    boolean existsByOrderId(OrderId orderId);

}