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
@Tag(name = "Store", description = "가게 관련 API")
public interface StoreControllerInterface {

    @Operation(
        summary = "가게 목록 조회",
        description = "Cursor 기반 페이지네이션을 사용하여 가게 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
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
        description = "키워드를 통해 가게를 검색합니다. 가게명을 기준으로 검색됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 검색 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
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
        description = "띱박스 카테고리를 기준으로 가게 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "카테고리별 가게 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 파라미터"
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
        description = "특정 가게의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 상세 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게"
        )
    })
    ApiResponse<StoreDetailDto> getStoreDetail(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId
    );

    @Operation(
        summary = "가게의 띱박스 목록 조회",
        description = "특정 가게의 활성화된 띱박스 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "띱박스 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게"
        )
    })
    ApiResponse<List<DdipBoxCardViewDto>> getStoreDdipBoxes(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId
    );
}