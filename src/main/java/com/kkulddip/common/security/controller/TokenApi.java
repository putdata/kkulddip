package com.kkulddip.common.security.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.oauth2.dto.OAuth2TokenRequest;
import com.kkulddip.common.security.oauth2.dto.OAuth2TokenResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@ApiResponses(value = {
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "토큰 교환 성공"
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(
            mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "유효하지 않은 Authorization Code",
                    value = """
                                        {
                                          "success": false,
                                          "status": 400,
                                          "code": "COMMON_INVALID_INPUT",
                                          "message": "잘못된 입력값입니다.",
                                          "timestamp": "2024-01-01T10:00:00"
                                        }
                                        """
                ),
                @ExampleObject(
                    name = "OAuth2 인증 실패",
                    value = """
                                        {
                                          "success": false,
                                          "status": 400,
                                          "code": "AUTH_OAUTH2_AUTHENTICATION_FAILED",
                                          "message": "OAuth2 인증에 실패했습니다.",
                                          "timestamp": "2024-01-01T10:00:00"
                                        }
                                        """
                )
            }
        )
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content = @Content(
            mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "만료된 Authorization Code",
                    value = """
                                        {
                                          "success": false,
                                          "status": 401,
                                          "code": "AUTH_EXPIRED_TOKEN",
                                          "message": "만료된 토큰입니다.",
                                          "timestamp": "2024-01-01T10:00:00"
                                        }
                                        """
                ),
                @ExampleObject(
                    name = "유효하지 않은 토큰",
                    value = """
                                        {
                                          "success": false,
                                          "status": 401,
                                          "code": "AUTH_INVALID_TOKEN",
                                          "message": "유효하지 않은 토큰입니다.",
                                          "timestamp": "2024-01-01T10:00:00"
                                        }
                                        """
                )
            }
        )
    ),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "내부 서버 오류",
        content = @Content(
            mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "서버 내부 오류",
                    value = """
                                        {
                                          "success": false,
                                          "status": 500,
                                          "code": "COMMON_INTERNAL_SERVER_ERROR",
                                          "message": "내부 서버 오류입니다.",
                                          "timestamp": "2024-01-01T10:00:00"
                                        }
                                        """
                ),
                @ExampleObject(
                    name = "OAuth2 사용자 생성 실패",
                    value = """
                                        {
                                          "success": false,
                                          "status": 500,
                                          "code": "AUTH_OAUTH2_USER_CREATION_FAILED",
                                          "message": "OAuth2 사용자 생성에 실패했습니다.",
                                          "timestamp": "2024-01-01T10:00:00"
                                        }
                                        """
                )
            }
        )
    )
})
public interface TokenApi {

    ApiResponse<OAuth2TokenResponse> exchangeCustomerToken(@Valid @RequestBody OAuth2TokenRequest request);

    ApiResponse<OAuth2TokenResponse> exchangeOwnerToken(@Valid @RequestBody OAuth2TokenRequest request);
}