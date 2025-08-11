package com.kkulddip.order.presentation.rest.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Builder;

@Builder
public record OrderConfirmationRequest(
    @NotNull(message = "확정/거절 액션은 필수입니다")
    ConfirmationAction action,
    
    String rejectionReason,
    
    @Future(message = "픽업 시간은 현재 시간 이후여야 합니다")
    LocalDateTime pickupTime
) {}