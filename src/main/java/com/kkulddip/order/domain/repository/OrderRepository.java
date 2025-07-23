package com.kkulddip.order.domain.repository;

import com.kkulddip.order.domain.model.aggregate.OrderAggregate;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    OrderAggregate save(OrderAggregate orderAggregate);

    Optional<OrderAggregate> findById(Long orderId);

    List<OrderAggregate> findAll();

    Page<OrderAggregate> findAll(Pageable pageable);

    List<OrderAggregate> findByStatus(OrderStatus status);

    List<OrderAggregate> findByCustomerEmail(String customerEmail);

    List<OrderAggregate> findByCustomerName(String customerName);

    List<OrderAggregate> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Page<OrderAggregate> findByCustomerEmail(String customerEmail, Pageable pageable);

    boolean existsById(Long orderId);

    void delete(OrderAggregate orderAggregate);

    void deleteById(Long orderId);

    long countByStatus(OrderStatus status);

    List<OrderAggregate> findByStatusAndCreatedAtBetween(
        OrderStatus status, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
} 