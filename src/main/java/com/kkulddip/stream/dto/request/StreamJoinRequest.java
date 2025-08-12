package com.kkulddip.stream.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스트림 참가 요청")
public record StreamJoinRequest(
) {
        //토큰 생성은 Post 가 자연스럽지만 현재는 받을 request body 가 없음
}