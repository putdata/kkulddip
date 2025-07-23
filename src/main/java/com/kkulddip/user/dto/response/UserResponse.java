package com.kkulddip.user.dto.response;

import com.kkulddip.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
    Long userId,
    String phoneNumber,
    Boolean isVerified,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean notificationEnabled
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
            .userId(user.getUserId())
            .phoneNumber(user.getPhoneNumber())
            .isVerified(user.getIsVerified())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .notificationEnabled(user.getNotificationEnabled())
            .build();
    }
} 