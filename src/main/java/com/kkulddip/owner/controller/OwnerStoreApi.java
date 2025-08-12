package com.kkulddip.owner.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.owner.dto.response.OwnerStoreResponse;
import com.kkulddip.owner.dto.response.StoreListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Owner Store API", description = "Owner 가게 관리 API")
public interface OwnerStoreApi {

    @Operation(
        summary = "내 가게 목록 조회",
        description = "현재 로그인한 Owner가 소유한 가게 목록을 조회합니다. 각 가게의 기본 정보와 주문 통계(총 주문 수, 총 매출)가 포함됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": true,
                          "status": 200,
                          "body": {
                            "stores": [
                              {
                                "storeId": 1,
                                "storeName": "친환경 마트",
                                "phone": "010-1234-5678",
                                "description": "신선한 식품을 제공하는 마트입니다",
                                "operatingHours": "09:00-22:00",
                                "isActive": true,
                                "rating": 4.5,
                                "reviewCount": 25,
                                "businessNumber": "123-45-67890",
                                "address": "서울시 강남구 테헤란로 123",
                                "imageUrl": "https://example.com/store1.jpg",
                                "latitude": 37.5665,
                                "longitude": 126.9780,
                                "createdAt": "2025-01-01T09:00:00",
                                "updatedAt": "2025-08-11T10:00:00",
                                "totalOrderCount": 150,
                                "totalRevenue": 2500000.0
                              }
                            ],
                            "totalCount": 3,
                            "activeCount": 2,
                            "inactiveCount": 1,
                            "hasNext": false,
                            "nextCursor": null
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
        )
    })
    @GetMapping("/stores")
    ApiResponse<StoreListResponse> getOwnerStores(
        @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
        @Parameter(description = "활성 가게만 조회 여부") @RequestParam(required = false) Boolean activeOnly
    );

    @Operation(
        summary = "특정 가게 상세 조회",
        description = "Owner가 소유한 특정 가게의 상세 정보를 조회합니다. 가게 소유권이 확인되어야만 조회 가능하며, 주문 통계가 포함됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 상세 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": true,
                          "status": 200,
                          "body": {
                            "storeId": 1,
                            "storeName": "친환경 마트",
                            "phone": "010-1234-5678",
                            "description": "신선한 식품을 제공하는 마트입니다",
                            "operatingHours": "09:00-22:00",
                            "isActive": true,
                            "rating": 4.5,
                            "reviewCount": 25,
                            "businessNumber": "123-45-67890",
                            "address": "서울시 강남구 테헤란로 123",
                            "imageUrl": "https://example.com/store1.jpg",
                            "latitude": 37.5665,
                            "longitude": 126.9780,
                            "createdAt": "2025-01-01T09:00:00",
                            "updatedAt": "2025-08-11T10:00:00",
                            "totalOrderCount": 150,
                            "totalRevenue": 2500000.0
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
            responseCode = "403",
            description = "해당 가게에 대한 접근 권한 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 403,
                          "code": "OWNER_STORE_ACCESS_DENIED",
                          "message": "해당 가게에 대한 접근 권한이 없습니다.",
                          "timestamp": "2025-08-11T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "가게 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "STORE_NOT_FOUND",
                          "message": "가게를 찾을 수 없습니다.",
                          "timestamp": "2025-08-11T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 가게 ID 형식",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 400,
                          "code": "COMMON_INVALID_TYPE",
                          "message": "invalid의 타입이 올바르지 않습니다.",
                          "timestamp": "2025-08-11T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/stores/{storeId}")
    ApiResponse<OwnerStoreResponse> getOwnerStore(
        @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
        @Parameter(description = "가게 ID") @PathVariable Long storeId
    );
}