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
    private LocalDateTime pickupTime;

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
     * 저장된 데이터로부터 Order를 복원할 때 사용
     * (인프라스트럭처 레이어에서만 사용)
     * 
     * @param orderId 주문 ID
     * @param customerId 고객 ID
     * @param storeId 매장 ID
     * @param orderItems 주문 아이템 목록
     * @param originalPrice 원래 가격
     * @param finalPrice 최종 가격
     * @param orderStatus 주문 상태
     * @param orderDate 주문 날짜
     * @param pickupTime 픽업 시간
     * @return 복원된 Order
     */
    public static Order restore(OrderId orderId, CustomerId customerId, StoreId storeId,
        List<OrderItem> orderItems, Money originalPrice, Money finalPrice,
        OrderStatus orderStatus, LocalDateTime orderDate, LocalDateTime pickupTime) {

        Order order = new Order();
        order.orderId = orderId;
        order.customerId = customerId;
        order.storeId = storeId;
        order.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        order.originalPrice = originalPrice;
        order.finalPrice = finalPrice;
        order.orderStatus = orderStatus;
        order.orderDate = orderDate;
        order.pickupTime = pickupTime;
        return order;
    }

    /**
     * 주문 아이템 추가
     * 
     * @param addOrderItemCommand 주문 아이템 추가 명령
     */
    public void addOrderItem(AddOrderItemCommand addOrderItemCommand) {
        if (this.orderStatus != OrderStatus.CREATED) {
            throw new IllegalStateException("CREATED 상태에서만 주문 아이템을 추가할 수 있습니다.");
        }

        addOrderItemCommand.validate();

        OrderItem orderItem = OrderItem.create(this, addOrderItemCommand);

        this.orderItems.add(orderItem);
        
        Money itemBasePrice = orderItem.calcBasePrice();
        Money itemDiscountPrice = orderItem.calcDiscountPrice();
        
        this.originalPrice = this.originalPrice.add(itemBasePrice);
        this.finalPrice = this.finalPrice.add(itemDiscountPrice);
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

    /**
     * 예상 픽업 시간 설정
     * 
     * @param pickupTime 픽업 시간
     */
    public void setPickupTime(LocalDateTime pickupTime) {
        if (this.orderStatus != OrderStatus.AWAITING_CONFIRMATION) {
            throw new IllegalStateException("AWAITING_CONFIRMATION 상태에서만 픽업 시간을 설정할 수 있습니다.");
        }
        this.pickupTime = pickupTime;
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
