package com.kkulddip.store.mapper;

import com.kkulddip.store.dto.response.DdipBoxCardViewDto;
import com.kkulddip.store.dto.response.DdipBoxItemDto;
import com.kkulddip.store.dto.response.StoreDetailDto;
import com.kkulddip.store.dto.response.StoreResponseDto;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.DdipBoxItem;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.DdipBoxItemRepository;
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

    private final DdipBoxRepository ddipBoxRepository;
    private final DdipBoxItemRepository ddipBoxItemRepository;

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

        return StoreResponseDto.builder()
            .storeId(store.getStoreId())
            .ownerId(store.getOwnerId())
            .storeName(store.getStoreName())
            .storeAddress(store.getStoreAddress())
            .description(store.getDescription())
            .operatingHours(store.getOperatingHours())
            .phoneNumber(store.getPhone())
            .ratingAverage(store.getRatingAverage())
            .reviewNum(store.getReviewCount())
            .distanceFromUser(distance)
            .representativeDdipboxName(representativeDdipboxName)
            .representativeOriginalPrice(representativeOriginalPrice)
            .representativeSalePrice(representativeSalePrice)
            .storeProfileImage(store.getStoreProfileImage())
            .active(store.getIsActive())
            .build();
    }

    /**
     * Store 엔티티를 StoreDetailDto로 변환
     *
     * @param store 변환할 Store 엔티티
     * @return 변환된 StoreDetailDto
     */
    public StoreDetailDto toStoreDetailDto(Store store) {
        List<DdipBox> ddipBoxes = ddipBoxRepository.findActiveByStoreId(store.getStoreId());
        
        List<StoreDetailDto.DdipBoxSummaryDto> ddipBoxSummaries = ddipBoxes.stream()
            .map(ddipBox -> StoreDetailDto.DdipBoxSummaryDto.builder()
                .ddipboxId(ddipBox.getDdipboxId())
                .ddipboxName(ddipBox.getDdipboxName())
                .category(ddipBox.getCategory())
                .originalPrice(ddipBox.getOriginalPrice())
                .salePrice(ddipBox.getSalePrice())
                .remainingQuantity(ddipBox.getRemainingQuantity())
                .active(ddipBox.getIsActive())
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
            .active(store.getIsActive())
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
        List<DdipBoxItem> ddipBoxItems = ddipBoxItemRepository.findByDdipBoxId(ddipBox.getDdipboxId());
        
        List<DdipBoxItemDto> items = ddipBoxItems.stream()
            .map(this::toDdipBoxItemDto)
            .collect(Collectors.toList());

        DdipBoxCardViewDto dto = DdipBoxCardViewDto.builder()
            .ddipboxId(ddipBox.getDdipboxId())
            .storeId(ddipBox.getStore().getStoreId())
            .ddipboxName(ddipBox.getDdipboxName())
            .description(ddipBox.getDescription())
            .category(ddipBox.getCategory())
            .originalPrice(ddipBox.getOriginalPrice())
            .salePrice(ddipBox.getSalePrice())
            .discountRate(null) // will be calculated
            .dailyQuantity(ddipBox.getDailyQuantity())
            .remainingQuantity(ddipBox.getRemainingQuantity())
            .maxPerCustomer(ddipBox.getMaxPerCustomer())
            .active(ddipBox.getIsActive())
            .soldOut(null) // will be calculated
            .items(items)
            .build();

        // Create a new record with calculated values
        return DdipBoxCardViewDto.builder()
            .ddipboxId(dto.ddipboxId())
            .storeId(dto.storeId())
            .ddipboxName(dto.ddipboxName())
            .description(dto.description())
            .category(dto.category())
            .originalPrice(dto.originalPrice())
            .salePrice(dto.salePrice())
            .discountRate(dto.calculateDiscountRate())
            .dailyQuantity(dto.dailyQuantity())
            .remainingQuantity(dto.remainingQuantity())
            .maxPerCustomer(dto.maxPerCustomer())
            .active(dto.active())
            .soldOut(dto.calculateIsSoldOut())
            .items(dto.items())
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
            .weight(item.getWeight())
            .build();
    }

    /**
     * 가게의 대표 띱박스를 찾습니다 (활성화된 것 중 첫 번째)
     *
     * @param store 가게 엔티티
     * @return 대표 띱박스 (없으면 empty)
     */
    private Optional<DdipBox> findRepresentativeDdipBox(Store store) {
        List<DdipBox> ddipBoxes = ddipBoxRepository.findActiveByStoreId(store.getStoreId());
        return ddipBoxes.stream().findFirst();
    }
}