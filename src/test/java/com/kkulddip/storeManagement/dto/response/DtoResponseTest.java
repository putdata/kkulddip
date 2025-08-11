package com.kkulddip.storeManagement.dto.response;

import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class DtoResponseTest {

    @Test
    @DisplayName("DdipBox 엔티티를 DdipBoxManagementResponse로 변환 성공")
    void fromDdipBox_to_DdipBoxManagementResponse_success() {
        // Given
        Store store = Store.builder().storeId(1L).ownerId(10L).build();
        DdipBox ddipBox = DdipBox.builder()
            .ddipboxId(100L)
            .store(store)
            .ddipboxName("Test DdipBox")
            .description("Description")
            .category("FOOD")
            .originalPrice(20000L)
            .salePrice(10000L)
            .dailyQuantity(20L)
            .remainingQuantity(15L)
            .maxPerCustomer(5L)
            .isActive(true)
            .build();

        // When
        DdipBoxManagementResponse response = DdipBoxManagementResponse.from(ddipBox);

        // Then
        assertThat(response.ddipboxId()).isEqualTo(100L);
        assertThat(response.storeId()).isEqualTo(1L);
        assertThat(response.ddipboxName()).isEqualTo("Test DdipBox");
        assertThat(response.isActive()).isTrue();
    }

    @Test
    @DisplayName("Store 엔티티를 StoreManagementResponse로 변환 성공")
    void fromStore_to_StoreManagementResponse_success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Store store = Store.builder()
            .storeId(1L)
            .ownerId(10L)
            .storeName("Test Store")
            .isActive(true)
            .createdAt(now)
            .build();

        // When
        StoreManagementResponse response = StoreManagementResponse.from(store);

        // Then
        assertThat(response.storeId()).isEqualTo(1L);
        assertThat(response.storeName()).isEqualTo("Test Store");
        assertThat(response.isActive()).isTrue();
        assertThat(response.createdAt()).isEqualTo(now);
    }
}