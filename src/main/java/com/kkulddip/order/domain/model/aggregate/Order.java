package com.kkulddip.order.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import com.kkulddip.order.domain.model.command.AddOrderItemCommand;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.model.vo.StoreId;

@Getter
public class Order {

    private OrderId orderId;
    private CustomerId customerId;
    private StoreId storeId;
    private List<OrderItem> orderItems;
    private Money originalPrice;
    private Money finalPrice;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;

    /**
     * 생성자
     */
    private Order() {}

    private Order(OrderId orderId, CustomerId customerId, StoreId storeId, LocalDateTime orderDate) {

        this.orderId = orderId;
        this.customerId = customerId;
        this.storeId = storeId;

        this.orderItems = new ArrayList<>();
        this.originalPrice = Money.of(0);
        this.finalPrice = Money.of(0);

        this.orderStatus = OrderStatus.CREATED;
        this.orderDate = orderDate;
    }

    /**
     * 주문 생성
     * 
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param storeId 매장 ID
     */
    public static Order create(OrderId orderId,
        CustomerId customerId, StoreId storeId, LocalDateTime orderDate) {

        return new Order(orderId, customerId, storeId, orderDate);
    }

    /**
     * 주문 아이템 추가
     * 
     * @param orderItem 주문 아이템
     */
    public void addOrderItem(AddOrderItemCommand addOrderItemCommand) {
        if (this.orderStatus != OrderStatus.CREATED) {
            throw new IllegalStateException("CREATED 상태에서만 주문 아이템을 추가할 수 있습니다.");
        }

        addOrderItemCommand.validate();

        OrderItem orderItem = OrderItem.create(this, addOrderItemCommand);

        this.orderItems.add(orderItem);
        this.originalPrice = this.originalPrice.add(orderItem.calcBasePrice());
        this.finalPrice = this.finalPrice.add(orderItem.calcDiscountPrice());
    }

    /**
     * 주문 상태 변경
     * 
     * @param newStatus 새로운 주문 상태
     */
    public void changeStatus(OrderStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalStateException("잘못된 상태 전환입니다. 현재 상태: " + this.orderStatus + ", 변경할 상태: " + newStatus);
        }

        this.orderStatus = newStatus;
    }

    public boolean isConfirmed() {
        return this.orderStatus == OrderStatus.CONFIRMED;
    }

    public boolean isPaid() {
        return this.orderStatus == OrderStatus.PAID;
    }

    public boolean isAwaitingConfirmation() {
        return this.orderStatus == OrderStatus.AWAITING_CONFIRMATION;
    }

    public boolean isCancelled() {
        return this.orderStatus == OrderStatus.CANCELLED;
    }

    private boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this.orderStatus) {
            case CREATED -> newStatus == OrderStatus.PAYMENT_PENDING;

            case PAYMENT_PENDING -> newStatus == OrderStatus.PAID
                || newStatus == OrderStatus.CANCELLED
                || newStatus == OrderStatus.FAILED;

            case PAID -> newStatus == OrderStatus.AWAITING_CONFIRMATION
                || newStatus == OrderStatus.CANCELLED;

            case AWAITING_CONFIRMATION -> newStatus == OrderStatus.CONFIRMED
                || newStatus == OrderStatus.CANCELLED;

            case CONFIRMED -> newStatus == OrderStatus.CANCELLED;

            case CANCELLED, FAILED -> false;

            default -> false;
        };
    }
    
}
