package com.kkulddip.favorite.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 즐겨찾기 목록 조회 응답 DTO
 * 즐겨찾기 목록 페이지에서 사용되는 카드뷰 형태의 데이터
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetFavoritesResponse(
    Long favoriteId,
    Long storeId,
    String storeName,
    String storeProfileImage,
    String representationDdipboxName,
    String representationDdipboxProfileImage,
    Double reviewRating,
    Double distanceFromCustomer,
    String category,
    LocalDateTime createdAt
) {}