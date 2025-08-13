package com.kkulddip.order.domain.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;

import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.command.AddOrderItemCommand;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.OrderItemId;
import com.kkulddip.order.domain.model.vo.ProductId;

@Getter
public class OrderItem {
    
    private OrderItemId orderItemId;
    private Order order;
    private ProductId productId;
    private Integer quantity; // 주문 수량
    private Money unitPrice; // 개당 정가 - 띱박스의 판매가

    private List<DiscountInfo> discountInfos; // 각 주문 유닛당 상세 정보

    protected OrderItem() {}

    @Builder
    protected OrderItem(OrderItemId orderItemId, Order order, ProductId productId, Integer quantity, Money unitPrice) {

        this.orderItemId = orderItemId;
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountInfos = new ArrayList<>(); // 빈 리스트로 초기화
    }

    public static OrderItem create(Order order, AddOrderItemCommand addOrderItemCommand) {
        OrderItem orderItem = OrderItem.builder()
            .orderItemId(addOrderItemCommand.orderItemId())
            .order(order)
            .productId(addOrderItemCommand.productId())
            .quantity(addOrderItemCommand.quantity())
            .unitPrice(addOrderItemCommand.unitPrice())
            .build();

        List<DiscountInfo> discountInfos = addOrderItemCommand.discountInfos().stream()
            .map(discountInfo -> DiscountInfo.create(
                orderItem,
                discountInfo
            ))
            .collect(Collectors.toList());

        orderItem.discountInfos = discountInfos;

        return orderItem;
    }

    public Money calcBasePrice() {
        Money result = this.unitPrice.multiply(this.quantity);
        return result;
    }

    public Money calcDiscountPrice() {
        // 기본 가격 = 수량 * 단가
        Money basePrice = calcBasePrice();
        
        // DiscountInfo가 없는 경우 기본 가격 반환
        if (discountInfos == null || discountInfos.isEmpty()) {
            return basePrice;
        }
        
        // 모든 DiscountInfo의 할인 금액 합산
        Money totalDiscount = discountInfos.stream()
            .map(DiscountInfo::getDiscountAmount)
            .filter(discount -> discount != null)
            .reduce(Money.of(0L), Money::add);
        
        
        // 총 가격 = 기본 가격 - 총 할인 금액
        Money finalPrice = basePrice.subtract(totalDiscount);
        return finalPrice;
    }
}
