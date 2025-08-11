package com.kkulddip.owner.dto.response;

import java.time.LocalDateTime;

public record OwnerProfileResponse(
    Long ownerId,
    String email,
    String name,
    String profileImageUrl,
    String oauth2Provider,
    LocalDateTime lastActiveAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String businessNumber,
    String representativeName,
    Integer totalStoreCount,
    Integer activeStoreCount
) {}