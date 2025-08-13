package com.kkulddip.store.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.store.common.Page;
import com.kkulddip.store.dto.response.DdipBoxCardViewDto;
import com.kkulddip.store.dto.response.StoreDetailDto;
import com.kkulddip.store.dto.response.StoreResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * Store API Controller Interface
 * Swagger 문서화를 위한 인터페이스
 */
@Tag(name = "Store API", description = "가게 조회 관련 API")
public interface StoreApi {

    @Operation(
        summary = "가게 목록 조회",
        description = "Cursor 기반 페이지네이션을 사용하여 가게 목록을 조회합니다. 거리 계산을 위해 사용자 위치 정보를 선택적으로 제공할 수 있습니다. 정렬 옵션으로 거리순, 평점순, 최신순 정렬이 가능합니다."
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
                            "content": [
                              {
                                "storeId": 1,
                                "ownerId": 1,
                                "storeName": "친환경 마트",
                                "storeAddress": "서울시 강남구 테헤란로 123",
                                "description": "신선한 유기농 식품을 판매하는 친환경 마트입니다",
                                "operatingHours": "09:00-22:00",
                                "phone": "02-1234-5678",
                                "ratingAverage": 4.5,
                                "reviewCount": 128,
                                "distanceFromUser": 0.8,
                                "representativeDdipboxName": "오늘의 신선채소 박스",
                                "representativeOriginalPrice": 15000,
                                "representativeSalePrice": 8000,
                                "storeProfileImage": "https://example.com/store1.jpg",
                                "isActive": true
                              }
                            ],
                            "hasNext": true,
                            "nextCursor": "eyJzdG9yZUlkIjoxfQ==",
                            "actualSize": 10
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 400,
                          "code": "COMMON_INVALID_INPUT",
                          "message": "입력값이 올바르지 않습니다.",
                          "timestamp": "2025-08-13T15:30:00",
                          "body": [
                            {
                              "field": "size",
                              "message": "페이지 크기는 1 이상이어야 합니다.",
                              "rejectedValue": "0"
                            }
                          ]
                        }
                        """
                )
            )
        )
    })
    ApiResponse<Page<StoreResponseDto>> getStores(
        @Parameter(description = "사용자 위도 (거리 계산용)", example = "37.5665")
        @RequestParam(required = false) Double userLatitude,

        @Parameter(description = "사용자 경도 (거리 계산용)", example = "126.9780")
        @RequestParam(required = false) Double userLongitude,

        @Parameter(description = "정렬 기준 (id, created_at, rating, distance)", example = "id")
        @RequestParam(defaultValue = "id") String sortBy,

        @Parameter(description = "페이지 크기 (1-50)", example = "10")
        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
        Integer size,

        @Parameter(description = "커서 (페이지네이션용)")
        @RequestParam(required = false) String cursor
    );

    @Operation(
        summary = "가게 검색",
        description = "키워드를 통해 가게를 검색합니다. 가게명을 기준으로 LIKE 검색을 수행하며, Cursor 기반 페이지네이션을 지원합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 검색 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": true,
                          "status": 200,
                          "body": {
                            "content": [
                              {
                                "storeId": 1,
                                "ownerId": 1,
                                "storeName": "친환경 마트",
                                "storeAddress": "서울시 강남구 테헤란로 123",
                                "description": "신선한 유기농 식품을 판매하는 친환경 마트입니다",
                                "operatingHours": "09:00-22:00",
                                "phone": "02-1234-5678",
                                "ratingAverage": 4.5,
                                "reviewCount": 128,
                                "distanceFromUser": 0.8,
                                "representativeDdipboxName": "오늘의 신선채소 박스",
                                "representativeOriginalPrice": 15000,
                                "representativeSalePrice": 8000,
                                "storeProfileImage": "https://example.com/store1.jpg",
                                "isActive": true
                              }
                            ],
                            "hasNext": false,
                            "nextCursor": null,
                            "actualSize": 1
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 400,
                          "code": "STORE_INVALID_SEARCH_KEYWORD",
                          "message": "검색 키워드는 1자 이상 100자 이하여야 합니다.",
                          "timestamp": "2025-08-13T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        )
    })
    ApiResponse<Page<StoreResponseDto>> searchStores(
        @Parameter(description = "검색 키워드 (가게명)", required = true, example = "친환경")
        @RequestParam String keyword,

        @Parameter(description = "사용자 위도 (거리 계산용)", example = "37.5665")
        @RequestParam(required = false) Double userLatitude,

        @Parameter(description = "사용자 경도 (거리 계산용)", example = "126.9780")
        @RequestParam(required = false) Double userLongitude,

        @Parameter(description = "정렬 기준 (id, created_at, rating, distance)", example = "id")
        @RequestParam(defaultValue = "id") String sortBy,

        @Parameter(description = "페이지 크기 (1-50)", example = "10")
        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
        Integer size,

        @Parameter(description = "커서 (페이지네이션용)")
        @RequestParam(required = false) String cursor
    );

    @Operation(
        summary = "카테고리별 가게 조회",
        description = "띱박스 카테고리를 기준으로 가게 목록을 조회합니다. 해당 카테고리의 활성화된 띱박스를 보유한 가게만 반환됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "카테고리별 가게 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": true,
                          "status": 200,
                          "body": {
                            "content": [
                              {
                                "storeId": 1,
                                "ownerId": 1,
                                "storeName": "유기농 전문점",
                                "storeAddress": "서울시 강남구 테헤란로 456",
                                "description": "100% 유기농 인증 식품만을 판매합니다",
                                "operatingHours": "08:00-20:00",
                                "phone": "02-5678-9012",
                                "ratingAverage": 4.8,
                                "reviewCount": 95,
                                "distanceFromUser": 1.2,
                                "representativeDdipboxName": "유기농 채소 세트",
                                "representativeOriginalPrice": 20000,
                                "representativeSalePrice": 12000,
                                "storeProfileImage": "https://example.com/organic-store.jpg",
                                "isActive": true
                              }
                            ],
                            "hasNext": true,
                            "nextCursor": "eyJzdG9yZUlkIjoxfQ==",
                            "actualSize": 1
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 카테고리",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 400,
                          "code": "STORE_INVALID_CATEGORY",
                          "message": "유효하지 않은 카테고리입니다.",
                          "timestamp": "2025-08-13T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        )
    })
    ApiResponse<Page<StoreResponseDto>> getStoresByCategory(
        @Parameter(description = "띱박스 카테고리", required = true, example = "유기농")
        @PathVariable String category,

        @Parameter(description = "사용자 위도 (거리 계산용)", example = "37.5665")
        @RequestParam(required = false) Double userLatitude,

        @Parameter(description = "사용자 경도 (거리 계산용)", example = "126.9780")
        @RequestParam(required = false) Double userLongitude,

        @Parameter(description = "정렬 기준 (id, created_at, rating, distance)", example = "id")
        @RequestParam(defaultValue = "id") String sortBy,

        @Parameter(description = "페이지 크기 (1-50)", example = "10")
        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
        Integer size,

        @Parameter(description = "커서 (페이지네이션용)")
        @RequestParam(required = false) String cursor
    );

    @Operation(
        summary = "가게 상세 조회",
        description = "특정 가게의 상세 정보를 조회합니다. 가게 기본 정보, 운영 시간, 주소, 평점과 함께 해당 가게의 띱박스 요약 정보도 포함됩니다."
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
                            "ownerId": 1,
                            "storeName": "친환경 마트",
                            "storeAddress": "서울특별시 강남구 테헤란로 123",
                            "description": "신선한 유기농 식품을 판매하는 친환경 마트입니다",
                            "operatingHours": "09:00-22:00",
                            "phone": "02-1234-5678",
                            "ratingAverage": 4.5,
                            "reviewCount": 128,
                            "businessNumber": "123-45-67890",
                            "storeProfileImage": "https://example.com/store1.jpg",
                            "latitude": 37.5665,
                            "longitude": 126.9780,
                            "isActive": true,
                            "createdAt": "2025-01-01T09:00:00",
                            "updatedAt": "2025-08-13T15:30:00",
                            "ddipBoxes": [
                              {
                                "ddipboxId": 1,
                                "ddipboxName": "오늘의 신선채소 박스",
                                "category": "유기농",
                                "originalPrice": 15000,
                                "salePrice": 8000,
                                "remainingQuantity": 5,
                                "isActive": true
                              },
                              {
                                "ddipboxId": 2,
                                "ddipboxName": "과일 모음 세트",
                                "category": "과일",
                                "originalPrice": 20000,
                                "salePrice": 12000,
                                "remainingQuantity": 0,
                                "isActive": true
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
            description = "잘못된 가게 ID 형식",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 400,
                          "code": "STORE_INVALID_STORE_ID",
                          "message": "가게 ID는 양수여야 합니다.",
                          "timestamp": "2025-08-13T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "STORE_NOT_FOUND",
                          "message": "가게를 찾을 수 없습니다.",
                          "timestamp": "2025-08-13T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        )
    })
    ApiResponse<StoreDetailDto> getStoreDetail(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId
    );

    @Operation(
        summary = "가게의 띱박스 목록 조회",
        description = "특정 가게의 활성화된 띱박스 목록을 상세하게 조회합니다. 각 띱박스의 구성 상품 정보와 할인율, 재고 상태 등이 포함됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "띱박스 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": true,
                          "status": 200,
                          "body": [
                            {
                              "ddipboxId": 1,
                              "storeId": 1,
                              "ddipboxName": "오늘의 신선채소 박스",
                              "description": "농장 직송 신선한 제철 채소들을 담았습니다",
                              "category": "유기농",
                              "originalPrice": 15000,
                              "salePrice": 8000,
                              "discountRate": 47,
                              "dailyQuantity": 20,
                              "remainingQuantity": 5,
                              "maxPerCustomer": 2,
                              "isActive": true,
                              "soldOut": false,
                              "items": [
                                {
                                  "itemId": 1,
                                  "ddipboxItemName": "유기농 상추",
                                  "originalPrice": 3000,
                                  "itemQuantity": 2,
                                  "weight": 200
                                },
                                {
                                  "itemId": 2,
                                  "ddipboxItemName": "유기농 토마토",
                                  "originalPrice": 5000,
                                  "itemQuantity": 1,
                                  "weight": 500
                                }
                              ]
                            },
                            {
                              "ddipboxId": 2,
                              "storeId": 1,
                              "ddipboxName": "과일 모음 세트",
                              "description": "달콤한 제철 과일을 한번에",
                              "category": "과일",
                              "originalPrice": 20000,
                              "salePrice": 12000,
                              "discountRate": 40,
                              "dailyQuantity": 10,
                              "remainingQuantity": 0,
                              "maxPerCustomer": 1,
                              "isActive": true,
                              "soldOut": true,
                              "items": [
                                {
                                  "itemId": 3,
                                  "ddipboxItemName": "사과",
                                  "originalPrice": 8000,
                                  "itemQuantity": 3,
                                  "weight": 600
                                }
                              ]
                            }
                          ]
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
                          "code": "STORE_INVALID_STORE_ID",
                          "message": "가게 ID는 양수여야 합니다.",
                          "timestamp": "2025-08-13T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "STORE_NOT_FOUND",
                          "message": "가게를 찾을 수 없습니다.",
                          "timestamp": "2025-08-13T15:30:00",
                          "body": null
                        }
                        """
                )
            )
        )
    })
    ApiResponse<List<DdipBoxCardViewDto>> getStoreDdipBoxes(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId
    );
}