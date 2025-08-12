package com.kkulddip.owner.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.owner.dto.request.UpdateOwnerProfileRequest;
import com.kkulddip.owner.dto.response.OwnerProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Owner Profile API", description = "Owner 프로필 관리 API")
public interface OwnerProfileApi {

    @Operation(
        summary = "Owner 프로필 조회",
        description = "현재 로그인한 Owner의 프로필 정보를 조회합니다. 프로필 조회 시 자동으로 마지막 활동 시간이 업데이트됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "프로필 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": true,
                          "status": 200,
                          "body": {
                            "ownerId": 1,
                            "email": "owner@example.com",
                            "name": "김사장",
                            "profileImageUrl": "https://example.com/profile.jpg",
                            "oauth2Provider": "GOOGLE",
                            "lastActiveAt": "2025-08-11T15:30:00",
                            "createdAt": "2025-01-01T09:00:00",
                            "updatedAt": "2025-08-11T15:30:00",
                            "businessNumber": "123-45-67890",
                            "representativeName": "김대표",
                            "totalStoreCount": 3,
                            "activeStoreCount": 2
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 401,
                          "code": "AUTH_UNAUTHORIZED",
                          "message": "인증이 필요합니다.",
                          "timestamp": "2025-08-11T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Owner 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "OWNER_NOT_FOUND",
                          "message": "사장을 찾을 수 없습니다.",
                          "timestamp": "2025-08-11T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/profile")
    ApiResponse<OwnerProfileResponse> getOwnerProfile(
        @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo
    );

    @Operation(
        summary = "Owner 프로필 수정",
        description = "현재 로그인한 Owner의 프로필 정보를 수정합니다. 수정 가능한 항목: 이름, 프로필 이미지, 사업자번호, 대표자명"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "프로필 수정 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": true,
                          "status": 200,
                          "body": {
                            "ownerId": 1,
                            "email": "owner@example.com",
                            "name": "수정된이름",
                            "profileImageUrl": "https://example.com/new-profile.jpg",
                            "oauth2Provider": "GOOGLE",
                            "lastActiveAt": "2025-08-11T15:35:00",
                            "createdAt": "2025-01-01T09:00:00",
                            "updatedAt": "2025-08-11T15:35:00",
                            "businessNumber": "987-65-43210",
                            "representativeName": "새대표",
                            "totalStoreCount": 3,
                            "activeStoreCount": 2
                          }
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
                              "field": "name",
                              "message": "이름은 필수입니다",
                              "rejectedValue": ""
                            },
                            {
                              "field": "name",
                              "message": "이름은 50자 이하여야 합니다",
                              "rejectedValue": "매우긴이름이매우긴이름이매우긴이름이매우긴이름이매우긴이름이매우긴이름이매우긴이름이"
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 401,
                          "code": "AUTH_UNAUTHORIZED",
                          "message": "인증이 필요합니다.",
                          "timestamp": "2025-08-11T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Owner 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "OWNER_NOT_FOUND",
                          "message": "사장을 찾을 수 없습니다.",
                          "timestamp": "2025-08-11T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        )
    })
    @PutMapping("/profile")
    ApiResponse<OwnerProfileResponse> updateOwnerProfile(
        @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
        @Valid @RequestBody UpdateOwnerProfileRequest request
    );
}