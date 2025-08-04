package com.kkulddip.common.event;

public record OrderCreatedEvent(
    String orderId,
    long amount
) {
    public static OrderCreatedEvent of(String orderId, long amount) {
        return new OrderCreatedEvent(orderId, amount);
    }
}