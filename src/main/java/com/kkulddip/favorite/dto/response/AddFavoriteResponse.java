package com.kkulddip.favorite.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 즐겨찾기 추가 응답 DTO
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddFavoriteResponse(
    Long favoriteId,
    Long customerId,
    Long storeId,
    LocalDateTime createdAt
) {}