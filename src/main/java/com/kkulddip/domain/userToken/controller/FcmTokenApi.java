package com.kkulddip.domain.userToken.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * FCM 토큰 관리 API 명세
 * Swagger UI를 위한 API 문서화 인터페이스
 *
 * @author 이석규
 * @since 1.0
 */
@Tag(name = "FCM 토큰", description = "Firebase Cloud Messaging 토큰 관리 API")
public interface FcmTokenApi {

    @Operation(
        summary = "FCM 토큰 등록",
        description = "사용자의 FCM 토큰을 등록하거나 업데이트합니다. 기존 토큰이 있는 경우 새로운 토큰으로 업데이트되며, 푸시 알림 발송 대상에 포함됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "토큰 등록 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": true,
                          "status": 200,
                          "body": null
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 400,
                          "code": "COMMON_INVALID_INPUT",
                          "message": "입력값이 올바르지 않습니다.",
                          "timestamp": "2025-08-11T15:30:00",
                          "body": [
                            {
                              "field": "fcmToken",
                              "message": "FCM 토큰은 필수입니다",
                              "rejectedValue": ""
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 401,
                          "code": "AUTH_TOKEN_MISSING",
                          "message": "인증 토큰이 필요합니다.",
                          "timestamp": "2025-08-11T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        )
    })
    ApiResponse<Void> registerToken(
        @Parameter(description = "인증된 사용자 정보", hidden = true)
        @AuthenticationPrincipal JwtUserInfo userInfo,
        @Parameter(description = "FCM 토큰 등록 요청 데이터", required = true)
        @Valid @RequestBody FcmTokenRequest request
    );

    @Operation(
        summary = "FCM 토큰 비활성화",
        description = "특정 사용자의 FCM 토큰을 비활성화합니다. 비활성화된 토큰은 푸시 알림 발송 대상에서 제외됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "토큰 비활성화 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "토큰을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    ApiResponse<Void> deactivateToken(
        @Parameter(description = "사용자 ID", required = true, example = "1")
        @PathVariable Long userId
    );

    @Operation(
        summary = "FCM 토큰 삭제",
        description = "특정 FCM 토큰을 완전히 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "토큰 삭제 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "토큰을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    ApiResponse<Void> deleteToken(
        @Parameter(description = "FCM 토큰", required = true)
        @PathVariable String fcmToken
    );
}