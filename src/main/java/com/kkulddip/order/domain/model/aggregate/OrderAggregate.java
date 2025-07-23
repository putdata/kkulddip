package com.kkulddip.order.domain.model.aggregate;

import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.OrderItem;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "orders")
public class OrderAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "customer_name", length = 50, nullable = false)
    private String customerName;

    @Column(name = "customer_email", length = 100, nullable = false)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private OrderStatus status;

    @ElementCollection
    @CollectionTable(
        name = "order_items",
        joinColumns = @JoinColumn(name = "order_id")
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private Money totalAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Builder
    public OrderAggregate(String customerName, String customerEmail, List<OrderItem> orderItems) {
        validateCustomerName(customerName);
        validateCustomerEmail(customerEmail);
        validateOrderItems(orderItems);

        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = OrderStatus.PENDING;
        this.orderItems = new ArrayList<>(orderItems);
        this.totalAmount = calculateTotalAmount();
    }

    private void validateCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("고객명은 필수입니다.");
        }
        if (customerName.length() > 50) {
            throw new IllegalArgumentException("고객명은 50자 이하여야 합니다.");
        }
    }

    private void validateCustomerEmail(String customerEmail) {
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("고객 이메일은 필수입니다.");
        }
        if (!customerEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }

    private void validateOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("주문 항목은 최소 1개 이상이어야 합니다.");
        }
    }

    private Money calculateTotalAmount() {
        return orderItems.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(new Money(0), Money::add);
    }

    public void confirm() {
        if (!status.canConfirm()) {
            throw new IllegalStateException("현재 상태에서는 주문을 확인할 수 없습니다.");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (!status.canCancel()) {
            throw new IllegalStateException("현재 상태에서는 주문을 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void deliver() {
        if (!status.canDeliver()) {
            throw new IllegalStateException("현재 상태에서는 배송 완료 처리할 수 없습니다.");
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void addOrderItem(OrderItem orderItem) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("대기중인 주문에만 항목을 추가할 수 있습니다.");
        }
        this.orderItems.add(orderItem);
        this.totalAmount = calculateTotalAmount();
    }

    public void removeOrderItem(int index) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("대기중인 주문에만 항목을 제거할 수 있습니다.");
        }
        if (index < 0 || index >= orderItems.size()) {
            throw new IllegalArgumentException("잘못된 주문 항목 인덱스입니다.");
        }
        this.orderItems.remove(index);
        this.totalAmount = calculateTotalAmount();
    }

    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }

    public boolean isConfirmed() {
        return status == OrderStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }

    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }
} 