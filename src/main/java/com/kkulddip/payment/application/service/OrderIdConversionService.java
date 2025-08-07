package com.kkulddip.payment.application.service;

import org.springframework.stereotype.Service;

@Service
public class OrderIdConversionService {

    private static final String ORDER_PREFIX = "ORDER-";

    public String toTossOrderId(Long orderId) {
        return ORDER_PREFIX + orderId;
    }

    public Long extractTossOrderId(String orderIdStr) {
        if (orderIdStr == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        
        if (orderIdStr.startsWith(ORDER_PREFIX)) {
            return Long.parseLong(orderIdStr.substring(ORDER_PREFIX.length()));
        }
        
        return Long.parseLong(orderIdStr);
    }
}