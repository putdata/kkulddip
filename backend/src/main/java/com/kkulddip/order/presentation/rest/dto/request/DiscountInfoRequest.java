package com.kkulddip.order.presentation.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record DiscountInfoRequest(
    @NotNull Long discountCode,
    @NotNull @PositiveOrZero Integer discountAmount
) {}