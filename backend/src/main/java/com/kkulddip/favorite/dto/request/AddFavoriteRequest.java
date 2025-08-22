package com.kkulddip.favorite.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 즐겨찾기 추가 요청 DTO
 */
public record AddFavoriteRequest(
    @NotNull(message = "소비자 아이디는 필수입니다.")
    Long customerId,
    
    @NotNull(message = "가게 아이디는 필수입니다.")
    Long storeId
) {}