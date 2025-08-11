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
@Tag(name = "Store", description = "가게 조회 API - 고객용 공개 API로 인증 불필요")
public interface StoreApi {

    @Operation(
        summary = "가게 목록 조회",
        description = """
        Cursor 기반 페이지네이션을 사용하여 활성화된 가게 목록을 조회합니다.
        
        **정렬 옵션:**
        - `id`: 가게 ID 순 (기본값)
        - `created_at`: 등록일시 순 (최신순)
        - `rating`: 평점 순 (높은 순)
        - `distance`: 거리 순 (사용자 좌표 필요)
        
        **응답 구조:**
        ```json
        {
          "success": true,
          "status": 200,
          "body": {
            "content": [
              {
                "storeId": 1,
                "ownerId": 1,
                "storeName": "친환경 농장 가게",
                "storeAddress": "서울시 강남구 테헤란로 123",
                "description": "신선한 유기농 채소를 판매합니다",
                "operatingHours": "09:00-18:00",
                "phone": "02-1234-5678",
                "ratingAverage": 4.5,
                "reviewCount": 42,
                "distanceFromUser": 1.2,
                "representativeDdipboxName": "유기농 채소 박스",
                "representativeOriginalPrice": 15000,
                "representativeSalePrice": 12000,
                "storeProfileImage": "https://s3.../store-profile.jpg",
                "isActive": true
              }
            ],
            "nextCursor": "eyJpZCI6MTAsImNyZWF0ZWRBdCI6IjIwMjUtMDEtMTAifQ==",
            "hasNext": true,
            "size": 10
          }
        }
        ```
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 목록 조회 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"content\":[{\"storeId\":1,\"storeName\":\"친환경 농장\",\"storeAddress\":\"서울시 강남구\",\"ratingAverage\":4.5,\"reviewCount\":42,\"distanceFromUser\":1.2,\"isActive\":true}],\"hasNext\":true,\"size\":10}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터 - sortBy가 유효하지 않거나 size가 범위를 벗어남"
        )
    })
    ApiResponse<Page<StoreResponseDto>> getStores(
        @Parameter(
            description = "사용자 위도 (거리 계산용, distance 정렬시 필수)", 
            example = "37.5665",
            schema = @Schema(minimum = "-90", maximum = "90")
        )
        @RequestParam(required = false) Double userLatitude,

        @Parameter(
            description = "사용자 경도 (거리 계산용, distance 정렬시 필수)", 
            example = "126.9780",
            schema = @Schema(minimum = "-180", maximum = "180")
        )
        @RequestParam(required = false) Double userLongitude,

        @Parameter(
            description = "정렬 기준", 
            example = "id",
            schema = @Schema(
                allowableValues = {"id", "created_at", "rating", "distance"},
                defaultValue = "id"
            )
        )
        @RequestParam(defaultValue = "id") String sortBy,

        @Parameter(
            description = "페이지 크기 (1-50)", 
            example = "10",
            schema = @Schema(minimum = "1", maximum = "50", defaultValue = "10")
        )
        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
        Integer size,

        @Parameter(
            description = "커서 (페이지네이션용, Base64 인코딩된 값)",
            example = "eyJpZCI6MTAsImNyZWF0ZWRBdCI6IjIwMjUtMDEtMTAifQ=="
        )
        @RequestParam(required = false) String cursor
    );

    @Operation(
        summary = "가게 검색",
        description = """
        키워드를 통해 가게를 검색합니다. 가게명을 기준으로 부분 일치 검색을 수행합니다.
        
        **검색 대상:** 가게명 (storeName)
        **검색 방식:** 부분 일치 (LIKE '%keyword%')
        
        **응답 구조:** 가게 목록 조회와 동일
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 검색 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"content\":[{\"storeId\":1,\"storeName\":\"친환경 농장\",\"storeAddress\":\"서울시 강남구\"}],\"hasNext\":false,\"size\":10}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터 - keyword가 비어있거나 너무 짧음"
        )
    })
    ApiResponse<Page<StoreResponseDto>> searchStores(
        @Parameter(
            description = "검색 키워드 (가게명)", 
            required = true, 
            example = "친환경",
            schema = @Schema(minLength = 1, maxLength = 100)
        )
        @RequestParam String keyword,

        @Parameter(
            description = "사용자 위도 (거리 계산용)", 
            example = "37.5665",
            schema = @Schema(minimum = "-90", maximum = "90")
        )
        @RequestParam(required = false) Double userLatitude,

        @Parameter(
            description = "사용자 경도 (거리 계산용)", 
            example = "126.9780",
            schema = @Schema(minimum = "-180", maximum = "180")
        )
        @RequestParam(required = false) Double userLongitude,

        @Parameter(
            description = "정렬 기준", 
            example = "id",
            schema = @Schema(
                allowableValues = {"id", "created_at", "rating", "distance"},
                defaultValue = "id"
            )
        )
        @RequestParam(defaultValue = "id") String sortBy,

        @Parameter(
            description = "페이지 크기 (1-50)", 
            example = "10",
            schema = @Schema(minimum = "1", maximum = "50", defaultValue = "10")
        )
        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
        Integer size,

        @Parameter(
            description = "커서 (페이지네이션용)",
            example = "eyJpZCI6MTAsImNyZWF0ZWRBdCI6IjIwMjUtMDEtMTAifQ=="
        )
        @RequestParam(required = false) String cursor
    );

    @Operation(
        summary = "카테고리별 가게 조회",
        description = """
        띱박스 카테고리를 기준으로 가게 목록을 조회합니다.
        해당 카테고리의 활성화된 띱박스를 보유한 가게들만 반환됩니다.
        
        **지원 카테고리:** FOOD, BAKERY, CAFE, DESSERT, etc.
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "카테고리별 가게 조회 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"content\":[{\"storeId\":1,\"storeName\":\"베이커리 카페\",\"representativeDdipboxName\":\"빵 세트\"}],\"hasNext\":false}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 카테고리 또는 파라미터"
        )
    })
    ApiResponse<Page<StoreResponseDto>> getStoresByCategory(
        @Parameter(
            description = "띱박스 카테고리", 
            required = true, 
            example = "FOOD",
            schema = @Schema(
                allowableValues = {"FOOD", "BAKERY", "CAFE", "DESSERT", "GROCERY", "OTHER"}
            )
        )
        @PathVariable String category,

        @Parameter(
            description = "사용자 위도 (거리 계산용)", 
            example = "37.5665"
        )
        @RequestParam(required = false) Double userLatitude,

        @Parameter(
            description = "사용자 경도 (거리 계산용)", 
            example = "126.9780"
        )
        @RequestParam(required = false) Double userLongitude,

        @Parameter(
            description = "정렬 기준", 
            example = "id"
        )
        @RequestParam(defaultValue = "id") String sortBy,

        @Parameter(
            description = "페이지 크기 (1-50)", 
            example = "10"
        )
        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
        Integer size,

        @Parameter(
            description = "커서 (페이지네이션용)"
        )
        @RequestParam(required = false) String cursor
    );

    @Operation(
        summary = "가게 상세 조회",
        description = """
        특정 가게의 상세 정보를 조회합니다.
        가게 기본 정보와 함께 해당 가게의 활성화된 띱박스 목록도 포함됩니다.
        
        **응답 구조:**
        ```json
        {
          "success": true,
          "status": 200,
          "body": {
            "storeId": 1,
            "storeName": "친환경 농장 가게",
            "storeAddress": "서울시 강남구 테헤란로 123",
            "description": "신선한 유기농 채소를 판매합니다",
            "operatingHours": "09:00-18:00",
            "phone": "02-1234-5678",
            "ratingAverage": 4.5,
            "reviewCount": 42,
            "businessNumber": "123-45-67890",
            "storeProfileImage": "https://s3.../profile.jpg",
            "latitude": 37.5665,
            "longitude": 126.9780,
            "isActive": true,
            "createdAt": "2025-01-01T09:00:00",
            "updatedAt": "2025-01-10T15:30:00",
            "ddipBoxes": [
              {
                "ddipboxId": 1,
                "ddipboxName": "유기농 채소 박스",
                "category": "FOOD",
                "originalPrice": 15000,
                "salePrice": 12000,
                "remainingQuantity": 5,
                "isActive": true
              }
            ]
          }
        }
        ```
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 상세 조회 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"storeId\":1,\"storeName\":\"친환경 농장\",\"storeAddress\":\"서울시 강남구\",\"phone\":\"02-1234-5678\",\"ratingAverage\":4.5,\"reviewCount\":42,\"isActive\":true,\"ddipBoxes\":[]}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게 또는 비활성화된 가게",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":404,\"message\":\"존재하지 않는 가게입니다\"}"
                )
            )
        )
    })
    ApiResponse<StoreDetailDto> getStoreDetail(
        @Parameter(
            description = "가게 ID", 
            required = true, 
            example = "1",
            schema = @Schema(minimum = "1")
        )
        @PathVariable Long storeId
    );

    @Operation(
        summary = "가게의 띱박스 목록 조회",
        description = """
        특정 가게의 활성화된 띱박스 목록을 조회합니다.
        주문 가능한 띱박스만 반환되며, 잔여 수량 정보도 포함됩니다.
        
        **응답 구조:**
        ```json
        {
          "success": true,
          "status": 200,
          "body": [
            {
              "ddipboxId": 1,
              "ddipboxName": "유기농 채소 박스",
              "description": "신선한 유기농 채소 모음",
              "category": "FOOD",
              "originalPrice": 15000,
              "salePrice": 12000,
              "dailyQuantity": 10,
              "remainingQuantity": 5,
              "maxPerCustomer": 2,
              "isActive": true,
              "createdAt": "2025-01-01T09:00:00"
            }
          ]
        }
        ```
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "띱박스 목록 조회 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":[{\"ddipboxId\":1,\"ddipboxName\":\"유기농 채소 박스\",\"category\":\"FOOD\",\"originalPrice\":15000,\"salePrice\":12000,\"remainingQuantity\":5,\"isActive\":true}]}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":404,\"message\":\"존재하지 않는 가게입니다\"}"
                )
            )
        )
    })
    ApiResponse<List<DdipBoxCardViewDto>> getStoreDdipBoxes(
        @Parameter(
            description = "가게 ID", 
            required = true, 
            example = "1",
            schema = @Schema(minimum = "1")
        )
        @PathVariable Long storeId
    );
}