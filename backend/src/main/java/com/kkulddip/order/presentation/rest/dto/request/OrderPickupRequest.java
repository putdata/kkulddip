package com.kkulddip.order.presentation.rest.dto.request;

import lombok.Builder;

@Builder
public record OrderPickupRequest(
    // 픽업 확인 요청에는 특별한 필드가 필요 없음
    // 추후 필요시 픽업 확인자 정보 등을 추가할 수 있음
) {}