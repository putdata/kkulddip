package com.kkulddip.payment.application.mapper;

import com.kkulddip.payment.domain.model.entity.Payment;
import com.kkulddip.payment.interfaces.dto.request.OrderRequestDto;
import com.kkulddip.payment.interfaces.dto.response.PaymentResponse;
import com.kkulddip.payment.domain.model.vo.Money;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    public Payment toEntity(OrderRequestDto orderRequest) {
        return new Payment(
                orderRequest.orderId(),
                orderRequest.orderName(),
                new Money(orderRequest.amount()),
                orderRequest.customerName(),
                orderRequest.customerEmail(),
                orderRequest.callbackUrl(),
                orderRequest.failUrl()
        );
    }

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.from(payment);
    }

    public List<PaymentResponse> toResponseList(List<Payment> payments) {
        return payments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}