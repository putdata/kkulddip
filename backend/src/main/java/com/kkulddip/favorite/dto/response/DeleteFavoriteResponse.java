package com.kkulddip.favorite.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * 즐겨찾기 삭제 응답 DTO
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeleteFavoriteResponse(
    String message,
    Long favoriteId
) {}