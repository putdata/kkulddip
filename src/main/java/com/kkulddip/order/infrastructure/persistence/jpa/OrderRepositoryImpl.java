package com.kkulddip.order.infrastructure.persistence.jpa;

import com.kkulddip.order.domain.model.aggregate.OrderAggregate;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public OrderAggregate save(OrderAggregate orderAggregate) {
        return orderJpaRepository.save(orderAggregate);
    }

    @Override
    public Optional<OrderAggregate> findById(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public List<OrderAggregate> findAll() {
        return orderJpaRepository.findAll();
    }

    @Override
    public Page<OrderAggregate> findAll(Pageable pageable) {
        return orderJpaRepository.findAll(pageable);
    }

    @Override
    public List<OrderAggregate> findByStatus(OrderStatus status) {
        return orderJpaRepository.findByStatus(status);
    }

    @Override
    public List<OrderAggregate> findByCustomerEmail(String customerEmail) {
        return orderJpaRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);
    }

    @Override
    public List<OrderAggregate> findByCustomerName(String customerName) {
        return orderJpaRepository.findByCustomerName(customerName);
    }

    @Override
    public List<OrderAggregate> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return orderJpaRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public Page<OrderAggregate> findByCustomerEmail(String customerEmail, Pageable pageable) {
        return orderJpaRepository.findByCustomerEmail(customerEmail, pageable);
    }

    @Override
    public boolean existsById(Long orderId) {
        return orderJpaRepository.existsById(orderId);
    }

    @Override
    public void delete(OrderAggregate orderAggregate) {
        orderJpaRepository.delete(orderAggregate);
    }

    @Override
    public void deleteById(Long orderId) {
        orderJpaRepository.deleteById(orderId);
    }

    @Override
    public long countByStatus(OrderStatus status) {
        return orderJpaRepository.countByStatus(status);
    }

    @Override
    public List<OrderAggregate> findByStatusAndCreatedAtBetween(
        OrderStatus status, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    ) {
        return orderJpaRepository.findByStatusAndCreatedAtBetween(status, startDate, endDate);
    }
} 