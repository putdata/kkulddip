package com.kkulddip.store.service;

import com.kkulddip.store.common.CursorInfo;
import com.kkulddip.store.common.Page;
import com.kkulddip.store.dto.request.StoreListRequest;
import com.kkulddip.store.dto.request.StoreSearchRequest;
import com.kkulddip.store.dto.response.DdipBoxCardViewDto;
import com.kkulddip.store.dto.response.StoreDetailDto;
import com.kkulddip.store.dto.response.StoreResponseDto;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.exception.StoreNotFoundException;
import com.kkulddip.store.mapper.StoreMapper;
import com.kkulddip.store.repository.CursorStoreRepository;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.store.util.DistanceCalculator;
import com.kkulddip.store.util.StoreValidator;
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

/**
 * 가게 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final CursorStoreRepository cursorStoreRepository;
    private final DdipBoxRepository ddipBoxRepository;
    private final StoreMapper storeMapper;

    /**
     * 가게 목록 조회 (Cursor 기반 페이지네이션)
     */
    public Page<StoreResponseDto> getStores(StoreListRequest request) {
        StoreValidator.validatePageSize(request.size());

        CursorInfo cursorInfo = CursorInfo.decode(request.cursor());
        Pageable pageable = PageRequest.of(0, request.size() + 1);

        List<Store> stores = fetchStoresBySortType(request, cursorInfo, pageable);

        boolean hasMore = stores.size() > request.size();
        if (hasMore) {
            stores = stores.subList(0, request.size());
        }

        List<StoreResponseDto> storeResponses = stores.stream()
            .map(store -> storeMapper.toStoreResponseDto(store, request.userLatitude(), request.userLongitude()))
            .collect(Collectors.toList());

        String nextCursor = createNextCursor(stores, request.sortBy(), hasMore, request.userLatitude(), request.userLongitude());
        boolean isFirstPage = request.cursor() == null;

        Page.PageMetadata metadata = Page.PageMetadata.builder()
            .sortBy(request.sortBy())
            .build();

        Page<StoreResponseDto> page = Page.of(
            storeResponses,
            request.size(),
            hasMore,
            nextCursor,
            isFirstPage
        );
        page.setMetadata(metadata);

        return page;
    }

    /**
     * 가게 검색 (Cursor 기반 페이지네이션)
     */
    public Page<StoreResponseDto> searchStores(StoreSearchRequest request) {
        StoreValidator.validatePageSize(request.size());

        CursorInfo cursorInfo = CursorInfo.decode(request.cursor());
        Pageable pageable = PageRequest.of(0, request.size() + 1);

        Long cursor = cursorInfo != null ? cursorInfo.getId() : null;
        List<Store> stores = cursorStoreRepository.findStoresWithCursorBySearch(
            request.keyword(), cursor, pageable);

        boolean hasMore = stores.size() > request.size();
        if (hasMore) {
            stores = stores.subList(0, request.size());
        }

        List<StoreResponseDto> storeResponses = stores.stream()
            .map(store -> storeMapper.toStoreResponseDto(store, request.userLatitude(), request.userLongitude()))
            .collect(Collectors.toList());

        String nextCursor = createNextCursor(stores, request.sortBy(), hasMore, request.userLatitude(), request.userLongitude());
        boolean isFirstPage = request.cursor() == null;

        Page.PageMetadata metadata = Page.PageMetadata.builder()
            .sortBy(request.sortBy())
            .searchKeyword(request.keyword())
            .build();

        Page<StoreResponseDto> page = Page.of(
            storeResponses,
            request.size(),
            hasMore,
            nextCursor,
            isFirstPage
        );
        page.setMetadata(metadata);

        return page;
    }

    /**
     * 카테고리별 가게 조회 (Cursor 기반 페이지네이션)
     */
    public Page<StoreResponseDto> getStoresByCategory(String category, StoreListRequest request) {
        StoreValidator.validatePageSize(request.size());

        CursorInfo cursorInfo = CursorInfo.decode(request.cursor());
        Pageable pageable = PageRequest.of(0, request.size() + 1);

        Long cursor = cursorInfo != null ? cursorInfo.getId() : null;
        List<Store> stores = cursorStoreRepository.findStoresWithCursorByCategory(
            category, cursor, pageable);

        boolean hasMore = stores.size() > request.size();
        if (hasMore) {
            stores = stores.subList(0, request.size());
        }

        List<StoreResponseDto> storeResponses = stores.stream()
            .map(store -> storeMapper.toStoreResponseDto(store, request.userLatitude(), request.userLongitude()))
            .collect(Collectors.toList());

        String nextCursor = createNextCursor(stores, request.sortBy(), hasMore, request.userLatitude(), request.userLongitude());
        boolean isFirstPage = request.cursor() == null;

        Page.PageMetadata metadata = Page.PageMetadata.builder()
            .sortBy(request.sortBy())
            .category(category)
            .build();

        Page<StoreResponseDto> page = Page.of(
            storeResponses,
            request.size(),
            hasMore,
            nextCursor,
            isFirstPage
        );
        page.setMetadata(metadata);

        return page;
    }

    /**
     * 가게 상세 조회
     */
    public StoreDetailDto getStoreDetail(Long storeId) {
        StoreValidator.validateStoreId(storeId);

        Store store = storeRepository.findActiveStore(storeId)
            .orElseThrow(() -> new StoreNotFoundException(storeId));

        return storeMapper.toStoreDetailDto(store);
    }

    /**
     * 가게의 띱박스 목록 조회
     */
    public List<DdipBoxCardViewDto> getStoreDdipBoxes(Long storeId) {
        StoreValidator.validateStoreId(storeId);

        if (!storeRepository.existsActiveStore(storeId)) {
            throw new StoreNotFoundException(storeId);
        }

        List<DdipBox> ddipBoxes = ddipBoxRepository.findActiveByStoreIdWithItems(storeId);

        return ddipBoxes.stream()
            .map(storeMapper::toDdipBoxCardViewDto)
            .collect(Collectors.toList());
    }

    /**
     * 정렬 타입에 따른 가게 조회
     */
    private List<Store> fetchStoresBySortType(StoreListRequest request, CursorInfo cursorInfo, Pageable pageable) {
        switch (request.sortBy().toLowerCase()) {
            case "id":
                return fetchStoresById(cursorInfo, pageable);
            case "created_at":
            case "createdat":
                return fetchStoresByCreatedAt(cursorInfo, pageable);
            case "rating":
                return fetchStoresByRating(cursorInfo, pageable);
            case "distance":
                if (request.userLatitude() != null && request.userLongitude() != null) {
                    return fetchStoresByDistance(cursorInfo, request.userLatitude(),
                        request.userLongitude(), request.size() + 1);
                }
                return fetchStoresById(cursorInfo, pageable);
            default:
                return fetchStoresById(cursorInfo, pageable);
        }
    }

    private List<Store> fetchStoresById(CursorInfo cursorInfo, Pageable pageable) {
        Long cursor = cursorInfo != null ? cursorInfo.getId() : null;
        return cursorStoreRepository.findStoresWithCursorById(cursor, pageable);
    }

    private List<Store> fetchStoresByCreatedAt(CursorInfo cursorInfo, Pageable pageable) {
        LocalDateTime cursorDate = null;
        Long cursorId = null;

        if (cursorInfo != null) {
            cursorDate = cursorInfo.getCreatedAt();
            cursorId = cursorInfo.getId();
        }

        return cursorStoreRepository.findStoresWithCursorByCreatedAtDesc(cursorDate, cursorId, pageable);
    }

    private List<Store> fetchStoresByRating(CursorInfo cursorInfo, Pageable pageable) {
        Double cursorRating = null;
        Long cursorId = null;

        if (cursorInfo != null) {
            cursorRating = cursorInfo.getRating();
            cursorId = cursorInfo.getId();
        }

        return cursorStoreRepository.findStoresWithCursorByRatingDesc(cursorRating, cursorId, pageable);
    }

    private List<Store> fetchStoresByDistance(CursorInfo cursorInfo, Double userLat, Double userLng, int limit) {
        Double cursorDistance = null;
        Long cursorId = null;

        if (cursorInfo != null) {
            cursorDistance = cursorInfo.getDistance();
            cursorId = cursorInfo.getId();
        }

        return cursorStoreRepository.findStoresWithCursorByDistance(userLat, userLng, cursorDistance, cursorId, limit);
    }

    /**
     * 다음 커서 생성
     */
    private String createNextCursor(List<Store> stores, String sortBy, boolean hasMore, Double userLat, Double userLng) {
        if (!hasMore || stores.isEmpty()) {
            return null;
        }

        Store lastStore = stores.get(stores.size() - 1);
        CursorInfo.CursorInfoBuilder builder = CursorInfo.builder().id(lastStore.getStoreId());

        switch (sortBy.toLowerCase()) {
            case "rating":
                builder.rating(lastStore.getRatingAverage());
                break;
            case "created_at":
            case "createdat":
                builder.createdAt(lastStore.getCreatedAt());
                break;
            case "distance":
                if (userLat != null && userLng != null && 
                    lastStore.getLatitude() != null && lastStore.getLongitude() != null) {
                    Double distance = DistanceCalculator
                        .calculateDistance(userLat, userLng, lastStore.getLatitude(), lastStore.getLongitude());
                    builder.distance(distance);
                }
                break;
        }

        return builder.build().encode();
    }
}