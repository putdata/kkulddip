package com.kkulddip.favorite.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.favorite.dto.request.AddFavoriteRequest;
import com.kkulddip.favorite.dto.response.AddFavoriteResponse;
import com.kkulddip.favorite.dto.response.DeleteFavoriteResponse;
import com.kkulddip.favorite.dto.response.GetFavoritesResponse;
import com.kkulddip.favorite.enums.FavoriteSortType;
import com.kkulddip.store.common.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Favorite API Controller Interface
 * Swagger 문서화를 위한 인터페이스
 */
@Tag(name = "Favorite", description = "즐겨찾기 관리 API - 고객 인증 필요")
@SecurityRequirement(name = "bearerAuth")
public interface FavoriteApi {

    @Operation(
        summary = "즐겨찾기 추가",
        description = """
        특정 고객이 특정 가게를 즐겨찾기에 추가합니다.
        
        **요청 구조:**
        ```json
        {
          "customerId": 1,
          "storeId": 10
        }
        ```
        
        **응답 구조:**
        ```json
        {
          "success": true,
          "status": 201,
          "body": {
            "favoriteId": 1,
            "customerId": 1,
            "storeId": 10,
            "storeName": "친환경 농장 가게",
            "createdAt": "2025-01-10T15:30:00"
          }
        }
        ```
        
        **비즈니스 규칙:**
        - 동일한 고객이 동일한 가게를 중복으로 즐겨찾기할 수 없습니다
        - 비활성화된 가게는 즐겨찾기할 수 없습니다
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "즐겨찾기 추가 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":201,\"body\":{\"favoriteId\":1,\"customerId\":1,\"storeId\":10,\"storeName\":\"친환경 농장 가게\",\"createdAt\":\"2025-01-10T15:30:00\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터 - 필수 필드 누락",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":400,\"message\":\"고객 ID는 필수입니다\",\"errors\":[{\"field\":\"customerId\",\"message\":\"고객 ID는 필수입니다\"}]}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패 - JWT 토큰 없음 또는 만료",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":401,\"message\":\"인증되지 않은 사용자입니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "가게를 찾을 수 없음 또는 비활성화된 가게",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":404,\"message\":\"존재하지 않는 가게입니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "이미 즐겨찾기에 추가된 가게",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":409,\"message\":\"이미 즐겨찾기에 등록된 가게입니다\"}"
                )
            )
        )
    })
    ApiResponse<AddFavoriteResponse> addFavorite(
        @Parameter(
            description = "즐겨찾기 추가 요청 정보",
            required = true,
            schema = @Schema(
                example = "{\"customerId\":1,\"storeId\":10}"
            )
        )
        @Valid @RequestBody AddFavoriteRequest request
    );

    @Operation(
        summary = "즐겨찾기 목록 조회",
        description = """
        특정 고객의 즐겨찾기 목록을 커서 기반 페이지네이션으로 조회합니다.
        
        **정렬 옵션:**
        - `CREATED_DESC`: 즐겨찾기 등록일 내림차순 (최신순, 기본값)
        - `CREATED_ASC`: 즐겨찾기 등록일 오름차순
        - `STORE_NAME_ASC`: 가게명 오름차순
        - `DISTANCE_ASC`: 거리순 (사용자 좌표 필요)
        
        **응답 구조:**
        ```json
        {
          "success": true,
          "status": 200,
          "body": {
            "content": [
              {
                "favoriteId": 1,
                "storeId": 10,
                "storeName": "친환경 농장 가게",
                "storeAddress": "서울시 강남구 테헤란로 123",
                "storeProfileImage": "https://s3.../profile.jpg",
                "ratingAverage": 4.5,
                "reviewCount": 42,
                "distanceFromUser": 1.2,
                "isStoreActive": true,
                "favoriteCreatedAt": "2025-01-10T15:30:00"
              }
            ],
            "nextCursor": "eyJmYXZvcml0ZUlkIjoxLCJjcmVhdGVkQXQiOiIyMDI1LTAxLTEwIn0=",
            "hasNext": true,
            "size": 20
          }
        }
        ```
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "즐겨찾기 목록 조회 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"content\":[{\"favoriteId\":1,\"storeId\":10,\"storeName\":\"친환경 농장\",\"storeAddress\":\"서울시 강남구\",\"ratingAverage\":4.5,\"distanceFromUser\":1.2,\"isStoreActive\":true}],\"hasNext\":true,\"size\":20}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터 - 유효하지 않은 sortBy 또는 size 범위 초과",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":400,\"message\":\"페이지 크기는 1-100 사이여야 합니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        )
    })
    ApiResponse<Page<GetFavoritesResponse>> getFavorites(
        @Parameter(
            description = "고객 ID", 
            required = true,
            example = "1",
            schema = @Schema(minimum = "1")
        )
        @RequestParam @NotNull Long customerId,

        @Parameter(
            description = "사용자 위도 (거리 계산용, DISTANCE_ASC 정렬시 필수)", 
            example = "37.5665",
            schema = @Schema(minimum = "-90", maximum = "90")
        )
        @RequestParam(required = false) Double userLatitude,

        @Parameter(
            description = "사용자 경도 (거리 계산용, DISTANCE_ASC 정렬시 필수)", 
            example = "126.9780",
            schema = @Schema(minimum = "-180", maximum = "180")
        )
        @RequestParam(required = false) Double userLongitude,

        @Parameter(
            description = "정렬 기준", 
            example = "CREATED_DESC",
            schema = @Schema(
                allowableValues = {"CREATED_DESC", "CREATED_ASC", "STORE_NAME_ASC", "DISTANCE_ASC"},
                defaultValue = "CREATED_DESC"
            )
        )
        @RequestParam(defaultValue = "CREATED_DESC") FavoriteSortType sortBy,

        @Parameter(
            description = "페이지 크기 (1-100)", 
            example = "20",
            schema = @Schema(minimum = "1", maximum = "100", defaultValue = "20")
        )
        @RequestParam(defaultValue = "20")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
        Integer size,

        @Parameter(
            description = "페이지네이션 커서 (Base64 인코딩된 값)",
            example = "eyJmYXZvcml0ZUlkIjoxLCJjcmVhdGVkQXQiOiIyMDI1LTAxLTEwIn0="
        )
        @RequestParam(required = false) String cursor
    );

    @Operation(
        summary = "즐겨찾기 삭제 (ID로)",
        description = """
        즐겨찾기 ID로 즐겨찾기를 삭제합니다.
        
        **응답 구조:**
        ```json
        {
          "success": true,
          "status": 200,
          "body": {
            "deletedFavoriteId": 1,
            "customerId": 1,
            "storeId": 10,
            "deletedAt": "2025-01-10T16:00:00"
          }
        }
        ```
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "즐겨찾기 삭제 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"deletedFavoriteId\":1,\"customerId\":1,\"storeId\":10,\"deletedAt\":\"2025-01-10T16:00:00\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "즐겨찾기를 찾을 수 없음 또는 다른 사용자의 즐겨찾기",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":404,\"message\":\"존재하지 않는 즐겨찾기입니다\"}"
                )
            )
        )
    })
    ApiResponse<DeleteFavoriteResponse> deleteFavorite(
        @Parameter(
            description = "즐겨찾기 ID", 
            required = true,
            example = "1",
            schema = @Schema(minimum = "1")
        )
        @PathVariable Long favoriteId
    );

    @Operation(
        summary = "즐겨찾기 삭제 (고객/가게로)",
        description = """
        특정 고객의 특정 가게 즐겨찾기를 삭제합니다.
        즐겨찾기 ID를 모르는 경우 사용할 수 있는 대안 API입니다.
        
        **사용 시나리오:**
        - 가게 상세 페이지에서 즐겨찾기 해제 버튼 클릭
        - 즐겨찾기 ID 없이 고객ID + 가게ID만으로 삭제
        
        **응답:** 삭제된 즐겨찾기 정보 반환 (deleteFavorite와 동일)
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "즐겨찾기 삭제 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"deletedFavoriteId\":1,\"customerId\":1,\"storeId\":10,\"deletedAt\":\"2025-01-10T16:00:00\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터 - customerId 또는 storeId 누락",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":400,\"message\":\"고객 ID와 가게 ID는 필수입니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "즐겨찾기를 찾을 수 없음",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":404,\"message\":\"해당 가게는 즐겨찾기에 등록되지 않았습니다\"}"
                )
            )
        )
    })
    ApiResponse<DeleteFavoriteResponse> deleteFavoriteByCustomerAndStore(
        @Parameter(
            description = "고객 ID", 
            required = true,
            example = "1",
            schema = @Schema(minimum = "1")
        )
        @RequestParam @NotNull Long customerId,

        @Parameter(
            description = "가게 ID", 
            required = true,
            example = "10",
            schema = @Schema(minimum = "1")
        )
        @RequestParam @NotNull Long storeId
    );

    @Operation(
        summary = "즐겨찾기 여부 확인",
        description = """
        특정 고객이 특정 가게를 즐겨찾기했는지 확인합니다.
        
        **사용 시나리오:**
        - 가게 상세 페이지 로드 시 하트 아이콘 상태 확인
        - 가게 목록에서 각 가게별 즐겨찾기 상태 표시
        
        **응답 구조:**
        ```json
        {
          "success": true,
          "status": 200,
          "body": true
        }
        ```
        
        **반환값:**
        - `true`: 즐겨찾기에 등록됨
        - `false`: 즐겨찾기에 등록되지 않음
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "즐겨찾기 여부 확인 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":true}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":400,\"message\":\"고객 ID와 가게 ID는 필수입니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        )
    })
    ApiResponse<Boolean> isFavorite(
        @Parameter(
            description = "고객 ID", 
            required = true,
            example = "1",
            schema = @Schema(minimum = "1")
        )
        @RequestParam @NotNull Long customerId,

        @Parameter(
            description = "가게 ID", 
            required = true,
            example = "10",
            schema = @Schema(minimum = "1")
        )
        @RequestParam @NotNull Long storeId
    );
}