package com.kkulddip.store.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.store.common.Page;
import com.kkulddip.store.dto.request.StoreListRequest;
import com.kkulddip.store.dto.request.StoreSearchRequest;
import com.kkulddip.store.dto.response.DdipBoxCardViewDto;
import com.kkulddip.store.dto.response.StoreDetailDto;
import com.kkulddip.store.dto.response.StoreResponseDto;
import com.kkulddip.store.service.StoreService;
import com.kkulddip.store.util.StoreValidator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Store API Controller
 * 가게 관련 REST API 엔드포인트를 제공
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/store")
@Validated
public class StoreController implements StoreControllerInterface {

    private final StoreService storeService;

    /**
     * 가게 목록 조회 (Cursor 기반 페이지네이션)
     * GET /
     */
    @GetMapping
    @Override
    public ApiResponse<Page<StoreResponseDto>> getStores(
            @RequestParam(required = false) Double userLatitude,
            @RequestParam(required = false) Double userLongitude,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "10") 
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
            @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
            Integer size,
            @RequestParam(required = false) String cursor) {

        log.info("가게 목록 조회 요청 - sortBy: {}, size: {}, userLocation: {},{}", 
                sortBy, size, userLatitude, userLongitude);

        StoreListRequest request = StoreListRequest.of(
                userLatitude,
                userLongitude,
                sortBy,
                size,
                cursor
        );

        Page<StoreResponseDto> result = storeService.getStores(request);
        
        log.info("가게 목록 조회 완료 - 조회된 가게 수: {}, hasNext: {}", 
                result.getActualSize(), result.getHasNext());

        return ApiResponse.of(result);
    }

    /**
     * 가게 검색 (Cursor 기반 페이지네이션)
     * GET /search
     */
    @GetMapping("/search")
    @Override
    public ApiResponse<Page<StoreResponseDto>> searchStores(
            @RequestParam String keyword,
            @RequestParam(required = false) Double userLatitude,
            @RequestParam(required = false) Double userLongitude,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
            @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
            Integer size,
            @RequestParam(required = false) String cursor) {

        log.info("가게 검색 요청 - keyword: {}, sortBy: {}, size: {}", keyword, sortBy, size);

        StoreValidator.validateSearchKeyword(keyword);

        StoreSearchRequest request = StoreSearchRequest.of(
                keyword.trim(),
                userLatitude,
                userLongitude,
                sortBy,
                size,
                cursor
        );

        Page<StoreResponseDto> result = storeService.searchStores(request);
        
        log.info("가게 검색 완료 - keyword: {}, 조회된 가게 수: {}", keyword, result.getActualSize());

        return ApiResponse.of(result);
    }

    /**
     * 카테고리별 가게 조회 (Cursor 기반 페이지네이션)
     * GET /category/{category}
     */
    @GetMapping("/category/{category}")
    @Override
    public ApiResponse<Page<StoreResponseDto>> getStoresByCategory(
            @PathVariable String category,
            @RequestParam(required = false) Double userLatitude,
            @RequestParam(required = false) Double userLongitude,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
            @Max(value = 50, message = "페이지 크기는 50 이하여야 합니다.")
            Integer size,
            @RequestParam(required = false) String cursor) {

        log.info("카테고리별 가게 조회 요청 - category: {}, sortBy: {}, size: {}", category, sortBy, size);

        StoreValidator.validateCategory(category);

        StoreListRequest request = StoreListRequest.of(
                userLatitude,
                userLongitude,
                sortBy,
                size,
                cursor
        );

        Page<StoreResponseDto> result = storeService.getStoresByCategory(category.trim(), request);
        
        log.info("카테고리별 가게 조회 완료 - category: {}, 조회된 가게 수: {}", category, result.getActualSize());

        return ApiResponse.of(result);
    }

    /**
     * 가게 상세 조회
     * GET /api/stores/{storeId}
     */
    @GetMapping("/{storeId}")
    @Override
    public ApiResponse<StoreDetailDto> getStoreDetail(@PathVariable Long storeId) {
        log.info("가게 상세 조회 요청 - storeId: {}", storeId);

        StoreValidator.validateStoreId(storeId);

        StoreDetailDto result = storeService.getStoreDetail(storeId);
        
        log.info("가게 상세 조회 완료 - storeId: {}, storeName: {}", storeId, result.storeName());

        return ApiResponse.of(result);
    }

    /**
     * 가게의 띱박스 목록 조회
     * GET /api/stores/{storeId}/ddipboxes
     */
    @GetMapping("/{storeId}/ddipboxes")
    @Override
    public ApiResponse<List<DdipBoxCardViewDto>> getStoreDdipBoxes(@PathVariable Long storeId) {
        log.info("가게 띱박스 목록 조회 요청 - storeId: {}", storeId);

        StoreValidator.validateStoreId(storeId);

        List<DdipBoxCardViewDto> result = storeService.getStoreDdipBoxes(storeId);
        
        log.info("가게 띱박스 목록 조회 완료 - storeId: {}, 띱박스 수: {}", storeId, result.size());

        return ApiResponse.of(result);
    }
}