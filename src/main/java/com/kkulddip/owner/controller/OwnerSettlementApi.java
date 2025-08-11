package com.kkulddip.owner.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.owner.dto.request.SettlementQueryRequest;
import com.kkulddip.owner.dto.response.SettlementResponse;
import com.kkulddip.owner.dto.response.SettlementSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Owner Settlement API", description = "Owner 정산 관리 API")
public interface OwnerSettlementApi {

    @Operation(
        summary = "특정 가게 정산 조회",
        description = "Owner가 소유한 특정 가게의 월별 정산 정보를 조회합니다. 해당 월의 매출, 주문 수, 평균 주문 금액과 전월 대비 성장률을 제공합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "정산 정보 조회 성공",
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
                            "period": [2024, 8],
                            "totalRevenue": 1500000,
                            "orderCount": 120,
                            "avgOrderAmount": 12500,
                            "previousMonthRevenue": 1200000,
                            "revenueGrowthRate": 25,
                            "previousMonthOrderCount": 100,
                            "orderCountGrowthRate": 20
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (년/월 파라미터 오류)",
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
                              "field": "year",
                              "message": "년도는 2020년 이상이어야 합니다",
                              "rejectedValue": 2019
                            },
                            {
                              "field": "month",
                              "message": "월은 1 이상이어야 합니다",
                              "rejectedValue": 0
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
        )
    })
    @GetMapping("/stores/{storeId}/settlement")
    ApiResponse<SettlementResponse> getStoreSettlement(
        @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
        @Parameter(description = "가게 ID") @PathVariable Long storeId,
        @Valid SettlementQueryRequest request
    );

    @Operation(
        summary = "전체 가게 정산 요약 조회",
        description = "Owner가 소유한 모든 가게의 월별 정산 요약 정보를 조회합니다. 전체 매출, 가게 수, 평균 매출과 전월 대비 성장률을 제공합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "정산 요약 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": true,
                          "status": 200,
                          "body": {
                            "period": [2024, 8],
                            "totalRevenue": 4500000,
                            "totalStoreCount": 3,
                            "activeStoreCount": 2,
                            "averageRevenuePerStore": 2250000,
                            "previousMonthTotalRevenue": 3600000,
                            "revenueGrowthRate": 25,
                            "topPerformingStores": [
                              {
                                "storeId": 1,
                                "storeName": "친환경 마트",
                                "revenue": 1500000,
                                "orderCount": 120,
                                "growthRate": 30
                              },
                              {
                                "storeId": 2,
                                "storeName": "빠른배송마트",
                                "revenue": 1200000,
                                "orderCount": 95,
                                "growthRate": 15
                              }
                            ]
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (년/월 파라미터 오류)",
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
                              "field": "year",
                              "message": "년도는 2020년 이상이어야 합니다",
                              "rejectedValue": 2019
                            },
                            {
                              "field": "month",
                              "message": "월은 1 이상이어야 합니다",
                              "rejectedValue": 0
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
        )
    })
    @GetMapping("/settlement/summary")
    ApiResponse<SettlementSummaryResponse> getSettlementSummary(
        @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
        @Valid SettlementQueryRequest request
    );
}