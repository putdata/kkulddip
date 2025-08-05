package com.kkulddip.store.mapper;

import com.kkulddip.store.dto.response.DdipBoxCardViewDto;
import com.kkulddip.store.dto.response.DdipBoxItemDto;
import com.kkulddip.store.dto.response.StoreDetailDto;
import com.kkulddip.store.dto.response.StoreResponseDto;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.DdipBoxItem;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Store 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class StoreMapper {

    /**
     * Store 엔티티를 StoreResponseDto로 변환
     * 
     * @param store 변환할 Store 엔티티
     * @param userLat 사용자 위도 (거리 계산용, null 가능)
     * @param userLng 사용자 경도 (거리 계산용, null 가능)
     * @return 변환된 StoreResponseDto
     */
    public StoreResponseDto toStoreResponseDto(Store store, Double userLat, Double userLng) {
        Double distance = null;
        if (DistanceCalculator.isValidCoordinate(userLat, userLng) 
            && DistanceCalculator.isValidCoordinate(store.getLatitude(), store.getLongitude())) {
            distance = DistanceCalculator.calculateDistance(userLat, userLng, store.getLatitude(), store.getLongitude());
        }

        StoreResponseDto.StoreResponseDtoBuilder builder = StoreResponseDto.builder()
                .storeId(store.getStoreId())
                .ownerId(store.getOwnerId())
                .storeName(store.getStoreName())
                .storeAddress(store.getStoreAddress())
                .description(store.getDescription())
                .operatingHours(store.getOperatingHours())
                .phoneNumber(store.getPhone())
                .ratingAverage(store.getRatingAverage())
                .reviewNum(store.getReviewCount())
                .storeProfileImage(store.getStoreProfileImage())
                .isActive(store.getIsActive())
                .distanceFromUser(distance);

        // 대표 띱박스 정보 추가
        findRepresentativeDdipBox(store).ifPresent(ddipBox -> {
            builder.representativeDdipboxName(ddipBox.getDdipboxName())
                    .representativeOriginalPrice(ddipBox.getOriginalPrice())
                    .representativeSalePrice(ddipBox.getSalePrice());
        });

        return builder.build();
    }

    /**
     * Store 엔티티를 StoreDetailDto로 변환
     * 
     * @param store 변환할 Store 엔티티
     * @return 변환된 StoreDetailDto
     */
    public StoreDetailDto toStoreDetailDto(Store store) {
        List<StoreDetailDto.DdipBoxSummaryDto> ddipBoxSummaries = store.getDdipBoxes().stream()
                .filter(DdipBox::getIsActive)
                .map(ddipBox -> StoreDetailDto.DdipBoxSummaryDto.builder()
                        .ddipboxId(ddipBox.getDdipboxId())
                        .ddipboxName(ddipBox.getDdipboxName())
                        .category(ddipBox.getCategory())
                        .originalPrice(ddipBox.getOriginalPrice())
                        .salePrice(ddipBox.getSalePrice())
                        .remainingQuantity(ddipBox.getRemainingQuantity())
                        .isActive(ddipBox.getIsActive())
                        .build())
                .collect(Collectors.toList());

        return StoreDetailDto.builder()
                .storeId(store.getStoreId())
                .ownerId(store.getOwnerId())
                .storeName(store.getStoreName())
                .storeAddress(store.getStoreAddress())
                .description(store.getDescription())
                .operatingHours(store.getOperatingHours())
                .phoneNumber(store.getPhone())
                .ratingAverage(store.getRatingAverage())
                .reviewCount(store.getReviewCount())
                .businessNumber(store.getBusinessNumber())
                .storeProfileImage(store.getStoreProfileImage())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .isActive(store.getIsActive())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .ddipBoxes(ddipBoxSummaries)
                .build();
    }

    /**
     * DdipBox 엔티티를 DdipBoxCardViewDto로 변환
     * 
     * @param ddipBox 변환할 DdipBox 엔티티
     * @return 변환된 DdipBoxCardViewDto
     */
    public DdipBoxCardViewDto toDdipBoxCardViewDto(DdipBox ddipBox) {
        List<DdipBoxItemDto> items = ddipBox.getDdipBoxItems().stream()
                .map(this::toDdipBoxItemDto)
                .collect(Collectors.toList());

        return DdipBoxCardViewDto.builder()
                .ddipboxId(ddipBox.getDdipboxId())
                .storeId(ddipBox.getStore().getStoreId())
                .ddipboxName(ddipBox.getDdipboxName())
                .description(ddipBox.getDescription())
                .category(ddipBox.getCategory())
                .originalPrice(ddipBox.getOriginalPrice())
                .salePrice(ddipBox.getSalePrice())
                .dailyQuantity(ddipBox.getDailyQuantity())
                .remainingQuantity(ddipBox.getRemainingQuantity())
                .maxPerCustomer(ddipBox.getMaxPerCustomer())
                .isActive(ddipBox.getIsActive())
                .items(items)
                .build();
    }

    /**
     * DdipBoxItem 엔티티를 DdipBoxItemDto로 변환
     * 
     * @param item 변환할 DdipBoxItem 엔티티
     * @return 변환된 DdipBoxItemDto
     */
    public DdipBoxItemDto toDdipBoxItemDto(DdipBoxItem item) {
        return DdipBoxItemDto.builder()
                .itemId(item.getItemId())
                .ddipboxItemName(item.getDdipboxItemName())
                .originalPrice(item.getOriginalPrice())
                .itemQuantity(item.getItemQuantity())
                .build();
    }

    /**
     * 가게의 대표 띱박스를 찾습니다 (활성화된 것 중 첫 번째)
     * 
     * @param store 가게 엔티티
     * @return 대표 띱박스 (없으면 empty)
     */
    private Optional<DdipBox> findRepresentativeDdipBox(Store store) {
        return store.getDdipBoxes().stream()
                .filter(DdipBox::getIsActive)
                .findFirst();
    }
}