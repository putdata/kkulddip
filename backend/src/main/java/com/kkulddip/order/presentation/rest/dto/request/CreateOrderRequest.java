package com.kkulddip.order.presentation.rest.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateOrderRequest(
    @NotNull Long customerId,
    @NotNull Long storeId,
    @NotEmpty @Valid List<OrderItemRequest> orderItems
) {}