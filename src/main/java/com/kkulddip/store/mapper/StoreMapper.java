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

        // 대표 띱박스 정보
        Optional<DdipBox> representativeDdipBox = findRepresentativeDdipBox(store);
        String representativeDdipboxName = representativeDdipBox.map(DdipBox::getDdipboxName).orElse(null);
        Long representativeOriginalPrice = representativeDdipBox.map(DdipBox::getOriginalPrice).orElse(null);
        Long representativeSalePrice = representativeDdipBox.map(DdipBox::getSalePrice).orElse(null);

        return StoreResponseDto.of(
                store.getStoreId(),
                store.getOwnerId(),
                store.getStoreName(),
                store.getStoreAddress(),
                store.getDescription(),
                store.getOperatingHours(),
                store.getPhone(),
                store.getRatingAverage(),
                store.getReviewCount(),
                distance,
                representativeDdipboxName,
                representativeOriginalPrice,
                representativeSalePrice,
                store.getStoreProfileImage(),
                store.getIsActive()
        );
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
                .map(ddipBox -> StoreDetailDto.DdipBoxSummaryDto.of(
                        ddipBox.getDdipboxId(),
                        ddipBox.getDdipboxName(),
                        ddipBox.getCategory(),
                        ddipBox.getOriginalPrice(),
                        ddipBox.getSalePrice(),
                        ddipBox.getRemainingQuantity(),
                        ddipBox.getIsActive()
                ))
                .collect(Collectors.toList());

        return StoreDetailDto.of(
                store.getStoreId(),
                store.getOwnerId(),
                store.getStoreName(),
                store.getStoreAddress(),
                store.getDescription(),
                store.getOperatingHours(),
                store.getPhone(),
                store.getRatingAverage(),
                store.getReviewCount(),
                store.getBusinessNumber(),
                store.getStoreProfileImage(),
                store.getLatitude(),
                store.getLongitude(),
                store.getIsActive(),
                store.getCreatedAt(),
                store.getUpdatedAt(),
                ddipBoxSummaries
        );
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

        DdipBoxCardViewDto dto = DdipBoxCardViewDto.of(
                ddipBox.getDdipboxId(),
                ddipBox.getStore().getStoreId(),
                ddipBox.getDdipboxName(),
                ddipBox.getDescription(),
                ddipBox.getCategory(),
                ddipBox.getOriginalPrice(),
                ddipBox.getSalePrice(),
                null, // discountRate - will be calculated
                ddipBox.getDailyQuantity(),
                ddipBox.getRemainingQuantity(),
                ddipBox.getMaxPerCustomer(),
                ddipBox.getIsActive(),
                null, // soldOut - will be calculated
                items
        );

        // Create a new record with calculated values
        return DdipBoxCardViewDto.of(
                dto.ddipboxId(),
                dto.storeId(),
                dto.ddipboxName(),
                dto.description(),
                dto.category(),
                dto.originalPrice(),
                dto.salePrice(),
                dto.calculateDiscountRate(),
                dto.dailyQuantity(),
                dto.remainingQuantity(),
                dto.maxPerCustomer(),
                dto.active(),
                dto.calculateIsSoldOut(),
                dto.items()
        );
    }

    /**
     * DdipBoxItem 엔티티를 DdipBoxItemDto로 변환
     * 
     * @param item 변환할 DdipBoxItem 엔티티
     * @return 변환된 DdipBoxItemDto
     */
    public DdipBoxItemDto toDdipBoxItemDto(DdipBoxItem item) {
        return DdipBoxItemDto.of(
                item.getItemId(),
                item.getDdipboxItemName(),
                item.getOriginalPrice(),
                item.getItemQuantity()
        );
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