package com.kkulddip.store.service;

import com.kkulddip.store.dto.response.StoreDetailPageResponse;
import com.kkulddip.store.entity.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CursorStoreService {

    private final CursorStoreRepository cursorStoreRepository;
    private final DdipBoxRepository ddipBoxRepository;
    //private final ReviewRepository reviewRepository;
    private final CursorPaginationService cursorPaginationService;

    /**
     * Cursor 기반 가게 리스트 조회 - Page 반환
     */
    public Page<StoreDetailPageResponse> getStoresWithCursorPage(CursorPageRequest request) {
        CursorInfo cursorInfo = CursorInfo.decode(request.getCursor());
        Pageable pageable = PageRequest.of(0, request.getSize() + 1); // +1로 다음 페이지 확인

        List<Store> stores = fetchStoresBySortType(request, cursorInfo, pageable);

        // 다음 페이지 존재 여부 확인
        boolean hasMore = stores.size() > request.getSize();
        if (hasMore) {
            stores = stores.subList(0, request.getSize()); // 실제 요청한 개수만큼만 반환
        }

        // Store를 StoreDetailPageResponse로 변환
        List<StoreDetailPageResponse> storeResponses = stores.stream()
            .map(store -> convertToStoreDetailPageResponse(store, request.getUserLatitude(), request.getUserLongitude()))
            .collect(Collectors.toList());

        // 다음 커서 생성
        String nextCursor = null;
        if (hasMore && !storeResponses.isEmpty()) {
            StoreDetailPageResponse lastStore = storeResponses.get(storeResponses.size() - 1);
            nextCursor = createCursorFromStore(lastStore, request.getSortBy());
        }

        // 첫 페이지 여부 판단
        boolean isFirstPage = request.getCursor() == null;

        // 메타데이터 설정
        Page.PageMetadata metadata = Page.PageMetadata.builder()
            .sortBy(request.getSortBy())
            .sortDirection(request.getSortDirection())
            .searchKeyword(request.getKeyword())
            .category(request.getCategory())
            .build();

        Page<StoreDetailPageResponse> page = Page.of(storeResponses, request.getSize(), hasMore, nextCursor, isFirstPage);
        page.setMetadata(metadata);

        return page;
    }

    /**
     * 검색 기능이 포함된 Cursor 페이지네이션 - Page 반환
     */
    public Page<StoreDetailPageResponse> searchStoresWithCursorPage(CursorPageRequest request) {
        CursorInfo cursorInfo = CursorInfo.decode(request.getCursor());
        Pageable pageable = PageRequest.of(0, request.getSize() + 1);

        Long cursor = cursorInfo != null ? cursorInfo.getId() : null;
        List<Store> stores = cursorStoreRepository.findStoresWithCursorBySearch(
            request.getKeyword(), cursor, pageable);

        boolean hasMore = stores.size() > request.getSize();
        if (hasMore) {
            stores = stores.subList(0, request.getSize());
        }

        List<StoreDetailPageResponse> storeResponses = stores.stream()
            .map(store -> convertToStoreDetailPageResponse(store, request.getUserLatitude(), request.getUserLongitude()))
            .collect(Collectors.toList());

        String nextCursor = null;
        if (hasMore && !storeResponses.isEmpty()) {
            StoreDetailPageResponse lastStore = storeResponses.get(storeResponses.size() - 1);
            nextCursor = createCursorFromStore(lastStore, request.getSortBy());
        }

        boolean isFirstPage = request.getCursor() == null;

        Page.PageMetadata metadata = Page.PageMetadata.builder()
            .sortBy(request.getSortBy())
            .sortDirection(request.getSortDirection())
            .searchKeyword(request.getKeyword())
            .build();

        Page<StoreDetailPageResponse> page = Page.of(storeResponses, request.getSize(), hasMore, nextCursor, isFirstPage);
        page.setMetadata(metadata);

        return page;
    }

    /**
     * 카테고리별 가게 조회 with Cursor - Page 반환
     */
    public Page<StoreDetailPageResponse> getStoresByCategoryWithCursorPage(CursorPageRequest request) {
        CursorInfo cursorInfo = CursorInfo.decode(request.getCursor());
        Pageable pageable = PageRequest.of(0, request.getSize() + 1);

        Long cursor = cursorInfo != null ? cursorInfo.getId() : null;
        List<Store> stores = cursorStoreRepository.findStoresWithCursorByCategory(
            request.getCategory(), cursor, pageable);

        boolean hasMore = stores.size() > request.getSize();
        if (hasMore) {
            stores = stores.subList(0, request.getSize());
        }

        List<StoreDetailPageResponse> storeResponses = stores.stream()
            .map(store -> convertToStoreDetailPageResponse(store, request.getUserLatitude(), request.getUserLongitude()))
            .collect(Collectors.toList());

        String nextCursor = null;
        if (hasMore && !storeResponses.isEmpty()) {
            StoreDetailPageResponse lastStore = storeResponses.get(storeResponses.size() - 1);
            nextCursor = createCursorFromStore(lastStore, request.getSortBy());
        }

        boolean isFirstPage = request.getCursor() == null;

        Page.PageMetadata metadata = Page.PageMetadata.builder()
            .sortBy(request.getSortBy())
            .sortDirection(request.getSortDirection())
            .category(request.getCategory())
            .build();

        Page<StoreDetailPageResponse> page = Page.of(storeResponses, request.getSize(), hasMore, nextCursor, isFirstPage);
        page.setMetadata(metadata);

        return page;
    }

    /**
     * Store를 StoreDetailPageResponse로 변환
     */
    private StoreDetailPageResponse convertToStoreDetailPageResponse(Store store, Double userLat, Double userLng) {
        // 대표 띱박스 조회
        Optional<DdipBox> representativeDdipBox = ddipBoxRepository.findRepresentativeDdipBoxByStoreId(store.getStoreId());

        // 거리 계산 (사용자 위치가 있는 경우에만)
        Double distance = null;
        if (userLat != null && userLng != null) {
            distance = calculateDistance(userLat, userLng, store.getLatitude(), store.getLongitude());
        }

        // 평점 계산
        Double averageRating = calculateAverageRating(store.getStoreId());

        StoreDetailPageResponse.StoreDetailPageResponseBuilder builder = StoreDetailPageResponse.builder()
            .storeId(store.getStoreId())
            .ownerId(store.getOwnerId())
            .storeName(store.getStoreName())
            .storeAddress(store.getStoreAddress())
            .description(store.getDescription())
            .operatingHours(store.getOperatingHours())
            .phoneNumber(store.getPhone())
            .ratingAverage(averageRating)
            .reviewNum(store.getReviewCount())
            .storeProfileImage(store.getStoreProfileImage())
            .businessNumber(store.getBusinessNumber())
            .isActive(store.getIsActive())
            .latitude(store.getLatitude())
            .longitude(store.getLongitude())
            .distanceFromUser(distance);

        if (representativeDdipBox.isPresent()) {
            DdipBox ddipBox = representativeDdipBox.get();
            builder.representativeDdipboxName(ddipBox.getDdipboxName())
                .representativeOriginalPrice(ddipBox.getOriginalPrice())
                .representativeSalePrice(ddipBox.getSalePrice());
        }

        return builder.build();
    }

    /**
     * StoreDetailPageResponse에서 Cursor 생성
     */
    private String createCursorFromStore(StoreDetailPageResponse store, String sortBy) {
        CursorInfo.CursorInfoBuilder builder = CursorInfo.builder()
            .id(store.getStoreId());

        switch (sortBy.toLowerCase()) {
            case "rating":
                builder.rating(store.getRatingAverage());
                break;
            case "distance":
                builder.distance(store.getDistanceFromUser());
                break;
            case "created_at":
                builder.createdAt(cursorPaginationService.formatDateTime(LocalDateTime.now())); // 실제로는 Store의 createdAt 사용
                break;
        }

        return builder.build().encode();
    }

    // 기존 메소드들 (fetchStoresBySortType, calculateDistance, calculateAverageRating 등)은 동일
    // ... (이전 코드와 동일)

    /**
     * 정렬 타입에 따른 가게 조회
     */
    private List<Store> fetchStoresBySortType(CursorPageRequest request,
                                              CursorInfo cursorInfo,
                                              Pageable pageable) {

        switch (request.getSortBy().toLowerCase()) {
            case "id":
                return fetchStoresById(cursorInfo, pageable, request.getSortDirection());

            case "created_at":
            case "createdat":
                return fetchStoresByCreatedAt(cursorInfo, pageable, request.getSortDirection());

            case "rating":
                return fetchStoresByRating(cursorInfo, pageable, request.getSortDirection());

            case "distance":
                if (request.getUserLatitude() != null && request.getUserLongitude() != null) {
                    return fetchStoresByDistance(cursorInfo, request.getUserLatitude(),
                        request.getUserLongitude(), request.getSize() + 1);
                }
                // 위치 정보가 없으면 ID 기준으로 fallback
                return fetchStoresById(cursorInfo, pageable, request.getSortDirection());

            default:
                return fetchStoresById(cursorInfo, pageable, request.getSortDirection());
        }
    }

    /**
     * ID 기준 조회
     */
    private List<Store> fetchStoresById(CursorInfo cursorInfo, Pageable pageable, String sortDirection) {
        Long cursor = cursorInfo != null ? cursorInfo.getId() : null;

        if (cursorPaginationService.isAscending(sortDirection)) {
            return cursorStoreRepository.findStoresWithCursorById(cursor, pageable);
        } else {
            return cursorStoreRepository.findStoresWithCursorByIdDesc(cursor, pageable);
        }
    }

    /**
     * 생성일 기준 조회
     */
    private List<Store> fetchStoresByCreatedAt(CursorInfo cursorInfo, Pageable pageable, String sortDirection) {
        LocalDateTime cursorDate = null;
        Long cursorId = null;

        if (cursorInfo != null) {
            cursorDate = cursorPaginationService.parseDateTime(cursorInfo.getCreatedAt());
            cursorId = cursorInfo.getId();
        }

        // 현재는 DESC만 구현 (최신순)
        return cursorStoreRepository.findStoresWithCursorByCreatedAtDesc(cursorDate, cursorId, pageable);
    }

    /**
     * 평점 기준 조회
     */
    private List<Store> fetchStoresByRating(CursorInfo cursorInfo, Pageable pageable, String sortDirection) {
        Double cursorRating = null;
        Long cursorId = null;

        if (cursorInfo != null) {
            cursorRating = cursorInfo.getRating();
            cursorId = cursorInfo.getId();
        }

        // 현재는 DESC만 구현 (높은 평점순)
        return cursorStoreRepository.findStoresWithCursorByRatingDesc(cursorRating, cursorId, pageable);
    }

    /**
     * 거리 기준 조회
     */
    private List<Store> fetchStoresByDistance(CursorInfo cursorInfo,
                                              Double userLatitude,
                                              Double userLongitude,
                                              int limit) {
        Double cursorDistance = null;
        Long cursorId = null;

        if (cursorInfo != null) {
            cursorDistance = cursorInfo.getDistance();
            cursorId = cursorInfo.getId();
        }

        return cursorStoreRepository.findStoresWithCursorByDistance(
            userLatitude, userLongitude, cursorDistance, cursorId, limit);
    }

    /**
     * 두 지점 간의 거리 계산 (Haversine formula)
     */
    private Double calculateDistance(Double userLat, Double userLng, Double storeLat, Double storeLng) {
        if (userLat == null || userLng == null || storeLat == null || storeLng == null) {
            return null;
        }

        final int EARTH_RADIUS = 6371; // 지구 반지름 (km)

        double latDistance = Math.toRadians(storeLat - userLat);
        double lngDistance = Math.toRadians(storeLng - userLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(storeLat))
            * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * 가게의 평균 평점 계산
     */
    private Double calculateAverageRating(Long storeId) {
        try {
            return reviewRepository.calculateAverageRatingByStoreId(storeId);
        } catch (Exception e) {
            log.warn("평균 평점 계산 중 오류 발생: {}", e.getMessage());
            return 0.0;
        }
    }
}