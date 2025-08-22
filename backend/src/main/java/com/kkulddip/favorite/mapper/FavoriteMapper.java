package com.kkulddip.favorite.mapper;

import com.kkulddip.favorite.dto.response.AddFavoriteResponse;
import com.kkulddip.favorite.dto.response.GetFavoritesResponse;
import com.kkulddip.favorite.entity.Favorite;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.store.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Favorite 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class FavoriteMapper {

    private final StoreRepository storeRepository;
    private final DdipBoxRepository ddipBoxRepository;

    /**
     * Favorite 엔티티를 AddFavoriteResponse로 변환
     */
    public AddFavoriteResponse toAddFavoriteResponse(Favorite favorite) {
        return AddFavoriteResponse.builder()
            .favoriteId(favorite.getFavoriteId())
            .customerId(favorite.getCustomerId())
            .storeId(favorite.getStoreId())
            .createdAt(favorite.getCreatedAt())
            .build();
    }

    /**
     * Favorite 엔티티를 GetFavoritesResponse로 변환
     *
     * @param favorite 변환할 Favorite 엔티티
     * @param userLat 사용자 위도 (거리 계산용, null 가능)
     * @param userLng 사용자 경도 (거리 계산용, null 가능)
     * @return 변환된 GetFavoritesResponse
     */
    public GetFavoritesResponse toGetFavoritesResponse(Favorite favorite, Double userLat, Double userLng) {
        // Store 정보 조회
        Optional<Store> storeOpt = storeRepository.findActiveStore(favorite.getStoreId());
        if (storeOpt.isEmpty()) {
            return null; // 비활성화된 가게는 제외
        }

        Store store = storeOpt.get();

        // 거리 계산
        Double distance = null;
        if (DistanceCalculator.isValidCoordinate(userLat, userLng)
            && DistanceCalculator.isValidCoordinate(store.getLatitude(), store.getLongitude())) {
            distance = DistanceCalculator.calculateDistance(userLat, userLng, store.getLatitude(), store.getLongitude());
        }

        // 대표 띱박스 정보
        Optional<DdipBox> representativeDdipBox = findRepresentativeDdipBox(store);
        String representativeDdipboxName = representativeDdipBox.map(DdipBox::getDdipboxName).orElse(null);
        
        // 카테고리 정보 (대표 띱박스의 카테고리)
        String category = representativeDdipBox.map(DdipBox::getCategory).orElse(null);

        return GetFavoritesResponse.builder()
            .favoriteId(favorite.getFavoriteId())
            .storeId(store.getStoreId())
            .storeName(store.getStoreName())
            .storeProfileImage(store.getStoreProfileImage())
            .representationDdipboxName(representativeDdipboxName)
            .representationDdipboxProfileImage(null) // DdipBox에 프로필 이미지 필드가 없으므로 null
            .reviewRating(store.getRatingAverage())
            .distanceFromCustomer(distance)
            .category(category)
            .createdAt(favorite.getCreatedAt())
            .build();
    }

    /**
     * 가게의 대표 띱박스를 찾는 헬퍼 메서드
     * Store 도메인과 동일한 방식으로 구현
     */
    private Optional<DdipBox> findRepresentativeDdipBox(Store store) {
        List<DdipBox> ddipBoxes = ddipBoxRepository.findActiveByStoreId(store.getStoreId());
        return ddipBoxes.stream().findFirst();
    }
}