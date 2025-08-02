package com.kkulddip.order.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import com.kkulddip.order.domain.model.command.AddOrderItemCommand;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.order.domain.model.entity.DiscountInfo;
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
    public static Order create(OrderId orderId, CustomerId customerId, StoreId storeId) {
        return new Order(orderId, customerId, storeId, LocalDateTime.now());
    }

    /**
     * 주문 아이템 추가
     * 
     * @param orderItem 주문 아이템
     */
    public void addOrderItem(AddOrderItemCommand addOrderItemCommand) {
        addOrderItemCommand.validate();

        OrderItem orderItem = OrderItem.builder()
            .order(this)
            .productId(addOrderItemCommand.productId())
            .quantity(addOrderItemCommand.quantity())
            .unitPrice(addOrderItemCommand.unitPrice())
            .build();
        
        List<DiscountInfo> discountInfos = addOrderItemCommand.orderItemUnitDetails().stream()
            .map(unitDetail -> DiscountInfo.builder()
                .orderItem(orderItem)
                .discountType(unitDetail.discountType())
                .discountCode(unitDetail.discountCode())
                .discountAmount(unitDetail.discountAmount())
                .build())
            .collect(Collectors.toList());

        orderItem.setDiscountInfos(discountInfos);

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
}
