package com.kkulddip.order.presentation.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DiscountInfoRequest(
    @NotNull Long discountCode,
    @NotNull Integer discountAmount
) {}