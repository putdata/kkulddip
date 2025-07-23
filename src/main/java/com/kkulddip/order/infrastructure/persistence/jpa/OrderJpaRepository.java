package com.kkulddip.order.infrastructure.persistence.jpa;

import com.kkulddip.order.domain.model.aggregate.OrderAggregate;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderAggregate, Long> {

    List<OrderAggregate> findByStatus(OrderStatus status);

    List<OrderAggregate> findByCustomerEmail(String customerEmail);

    List<OrderAggregate> findByCustomerName(String customerName);

    List<OrderAggregate> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Page<OrderAggregate> findByCustomerEmail(String customerEmail, Pageable pageable);

    long countByStatus(OrderStatus status);

    List<OrderAggregate> findByStatusAndCreatedAtBetween(
        OrderStatus status, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );

    List<OrderAggregate> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);

    Page<OrderAggregate> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
} 