package com.kkulddip.order.domain.model.entity;

import lombok.Builder;
import lombok.Getter;

import com.kkulddip.order.domain.model.command.AddDiscountInfoCommand;
import com.kkulddip.order.domain.model.enums.DiscountType;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.DiscountCode;
import com.kkulddip.order.domain.model.vo.DiscountInfoId;

@Getter
public class DiscountInfo {

    private DiscountInfoId discountInfoId;
    private OrderItem orderItem;
    private DiscountType discountType;
    private DiscountCode discountCode;
    private Money discountAmount;

    protected DiscountInfo() {}

    @Builder
    protected DiscountInfo(DiscountInfoId discountInfoId, OrderItem orderItem,
        DiscountType discountType, DiscountCode discountCode, Money discountAmount) {

        this.discountInfoId = discountInfoId;
        this.orderItem = orderItem;
        this.discountType = discountType;
        this.discountCode = discountCode;
        this.discountAmount = discountAmount;
    }

    public static DiscountInfo create(OrderItem orderItem, AddDiscountInfoCommand addDiscountInfoCommand) {
        addDiscountInfoCommand.validate();

        return DiscountInfo.builder()
            .discountInfoId(addDiscountInfoCommand.discountInfoId())
            .orderItem(orderItem)
            .discountType(addDiscountInfoCommand.discountType())
            .discountCode(addDiscountInfoCommand.discountCode())
            .discountAmount(addDiscountInfoCommand.discountAmount())
            .build();
    }
}
