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
@Tag(name = "Favorite", description = "즐겨찾기 관련 API")
public interface FavoriteApi {

    @Operation(
        summary = "즐겨찾기 추가",
        description = "특정 고객이 특정 가게를 즐겨찾기에 추가합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "즐겨찾기 추가 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "가게를 찾을 수 없음"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "이미 즐겨찾기에 추가된 가게"
        )
    })
    ApiResponse<AddFavoriteResponse> addFavorite(
        @Valid @RequestBody AddFavoriteRequest request
    );

    @Operation(
        summary = "즐겨찾기 목록 조회",
        description = "특정 고객의 즐겨찾기 목록을 커서 기반 페이지네이션으로 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "즐겨찾기 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
        )
    })
    ApiResponse<Page<GetFavoritesResponse>> getFavorites(
        @Parameter(description = "고객 ID", required = true)
        @RequestParam @NotNull Long customerId,

        @Parameter(description = "사용자 위도 (거리 계산용)", example = "37.5665")
        @RequestParam(required = false) Double userLatitude,

        @Parameter(description = "사용자 경도 (거리 계산용)", example = "126.9780")
        @RequestParam(required = false) Double userLongitude,

        @Parameter(description = "정렬 기준", example = "CREATED_DESC")
        @RequestParam(defaultValue = "CREATED_DESC") FavoriteSortType sortBy,

        @Parameter(description = "페이지 크기 (1-100)", example = "20")
        @RequestParam(defaultValue = "20")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
        Integer size,

        @Parameter(description = "페이지네이션 커서")
        @RequestParam(required = false) String cursor
    );

    @Operation(
        summary = "즐겨찾기 삭제 (ID로)",
        description = "즐겨찾기 ID로 즐겨찾기를 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "즐겨찾기 삭제 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "즐겨찾기를 찾을 수 없음"
        )
    })
    ApiResponse<DeleteFavoriteResponse> deleteFavorite(
        @Parameter(description = "즐겨찾기 ID", required = true)
        @PathVariable Long favoriteId
    );

    @Operation(
        summary = "즐겨찾기 삭제 (고객/가게로)",
        description = "특정 고객의 특정 가게 즐겨찾기를 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "즐겨찾기 삭제 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "즐겨찾기를 찾을 수 없음"
        )
    })
    ApiResponse<DeleteFavoriteResponse> deleteFavoriteByCustomerAndStore(
        @Parameter(description = "고객 ID", required = true)
        @RequestParam @NotNull Long customerId,

        @Parameter(description = "가게 ID", required = true)
        @RequestParam @NotNull Long storeId
    );

    @Operation(
        summary = "즐겨찾기 여부 확인",
        description = "특정 고객이 특정 가게를 즐겨찾기했는지 확인합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "즐겨찾기 여부 확인 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    ApiResponse<Boolean> isFavorite(
        @Parameter(description = "고객 ID", required = true)
        @RequestParam @NotNull Long customerId,

        @Parameter(description = "가게 ID", required = true)
        @RequestParam @NotNull Long storeId
    );
}