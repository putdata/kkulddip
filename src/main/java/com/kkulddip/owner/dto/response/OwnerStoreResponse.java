package com.kkulddip.owner.dto.response;

import java.time.LocalDateTime;

public record OwnerStoreResponse(
    Long storeId,
    String storeName,
    String phone,
    String description,
    String operatingHours,
    Boolean isActive,
    Double ratingAverage,
    Long reviewCount,
    String businessNumber,
    String storeAddress,
    String storeProfileImage,
    Double latitude,
    Double longitude,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long totalOrderCount,
    Double totalRevenue
) {}