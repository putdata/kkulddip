package com.kkulddip.favorite.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.favorite.dto.request.AddFavoriteRequest;
import com.kkulddip.favorite.dto.request.GetFavoritesRequest;
import com.kkulddip.favorite.dto.response.AddFavoriteResponse;
import com.kkulddip.favorite.dto.response.DeleteFavoriteResponse;
import com.kkulddip.favorite.dto.response.GetFavoritesResponse;
import com.kkulddip.favorite.enums.FavoriteSortType;
import com.kkulddip.favorite.service.FavoriteService;
import com.kkulddip.store.common.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Favorite API Controller
 * 즐겨찾기 관련 REST API 엔드포인트를 제공
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/favorites")
@Validated
public class FavoriteController implements FavoriteApi {

    private final FavoriteService favoriteService;

    /**
     * 즐겨찾기 추가
     * POST /
     */
    @PostMapping
    @Override
    public ApiResponse<AddFavoriteResponse> addFavorite(@Valid @RequestBody AddFavoriteRequest request) {
        log.info("즐겨찾기 추가 요청 - customerId: {}, storeId: {}", 
            request.customerId(), request.storeId());

        AddFavoriteResponse response = favoriteService.addFavorite(request);
        return ApiResponse.of(HttpStatus.CREATED.value(), response);
    }

    /**
     * 즐겨찾기 목록 조회 (커서 기반 페이지네이션)
     * GET /
     */
    @GetMapping
    @Override
    public ApiResponse<Page<GetFavoritesResponse>> getFavorites(
        @RequestParam @NotNull Long customerId,
        @RequestParam(required = false) Double userLatitude,
        @RequestParam(required = false) Double userLongitude,
        @RequestParam(defaultValue = "CREATED_DESC") FavoriteSortType sortBy,
        @RequestParam(defaultValue = "20")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
        Integer size,
        @RequestParam(required = false) String cursor) {

        log.info("즐겨찾기 목록 조회 요청 - customerId: {}, sortBy: {}, size: {}, userLocation: {},{}",
            customerId, sortBy, size, userLatitude, userLongitude);

        GetFavoritesRequest request = GetFavoritesRequest.of(customerId, cursor, size, sortBy);
        Page<GetFavoritesResponse> response = favoriteService.getFavorites(request, userLatitude, userLongitude);
        
        return ApiResponse.of(response);
    }

    /**
     * 즐겨찾기 삭제 (ID로)
     * DELETE /{favoriteId}
     */
    @DeleteMapping("/{favoriteId}")
    @Override
    public ApiResponse<DeleteFavoriteResponse> deleteFavorite(@PathVariable Long favoriteId) {
        log.info("즐겨찾기 삭제 요청 - favoriteId: {}", favoriteId);

        DeleteFavoriteResponse response = favoriteService.deleteFavorite(favoriteId);
        return ApiResponse.of(response);
    }

    /**
     * 즐겨찾기 삭제 (고객/가게로)
     * DELETE /by-customer-store
     */
    @DeleteMapping("/by-customer-store")
    @Override
    public ApiResponse<DeleteFavoriteResponse> deleteFavoriteByCustomerAndStore(
        @RequestParam @NotNull Long customerId,
        @RequestParam @NotNull Long storeId) {
        
        log.info("고객의 가게 즐겨찾기 삭제 요청 - customerId: {}, storeId: {}", customerId, storeId);

        DeleteFavoriteResponse response = favoriteService.deleteFavoriteByCustomerAndStore(customerId, storeId);
        return ApiResponse.of(response);
    }

    /**
     * 즐겨찾기 여부 확인
     * GET /check
     */
    @GetMapping("/check")
    @Override
    public ApiResponse<Boolean> isFavorite(
        @RequestParam @NotNull Long customerId,
        @RequestParam @NotNull Long storeId) {
        
        log.debug("즐겨찾기 여부 확인 요청 - customerId: {}, storeId: {}", customerId, storeId);

        boolean isFavorite = favoriteService.isFavorite(customerId, storeId);
        return ApiResponse.of(isFavorite);
    }
}