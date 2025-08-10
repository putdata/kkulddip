package com.kkulddip.storeManagement.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.storeManagement.dto.request.CreateDdipBoxRequest;
import com.kkulddip.storeManagement.dto.request.UpdateDdipBoxQuantityRequest;
import com.kkulddip.storeManagement.dto.request.UpdateDdipBoxRequest;
import com.kkulddip.storeManagement.dto.response.DdipBoxManagementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("citest")
@ExtendWith(MockitoExtension.class)
public class DdipBoxManagementServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private DdipBoxRepository ddipBoxRepository;

    @InjectMocks
    private DdipBoxManagementService ddipBoxManagementService;

    private Store store;
    private Long storeId = 1L;
    private Long ownerId = 10L;

    @BeforeEach
    void setUp() {
        store = Store.builder()
            .storeId(storeId)
            .ownerId(ownerId)
            .storeName("Test Store")
            .build();
    }

    @Test
    @DisplayName("띱박스 생성 성공")
    void createDdipBox_success() {
        // Given
        CreateDdipBoxRequest request = new CreateDdipBoxRequest(
            "Test DdipBox", "Description", "FOOD", 10000L, 5000L, 10L, 3L
        );
        DdipBox ddipBox = DdipBox.builder()
            .ddipboxId(20L)
            .store(store)
            .ddipboxName(request.ddipboxName())
            .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(ddipBoxRepository.save(any(DdipBox.class))).thenReturn(ddipBox);

        // When
        DdipBoxManagementResponse response = ddipBoxManagementService.createDdipBox(storeId, request, ownerId);

        // Then
        assertThat(response.ddipboxId()).isEqualTo(20L);
        assertThat(response.ddipboxName()).isEqualTo("Test DdipBox");
        verify(storeRepository, times(1)).findById(storeId);
        verify(ddipBoxRepository, times(1)).save(any(DdipBox.class));
    }

    @Test
    @DisplayName("띱박스 생성 실패 - 가게 소유권 없음")
    void createDdipBox_fail_invalidOwnership() {
        // Given
        CreateDdipBoxRequest request = new CreateDdipBoxRequest(
            "Test DdipBox", "Description", "FOOD", 10000L, 5000L, 10L, 3L
        );
        Long otherOwnerId = 99L;

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> ddipBoxManagementService.createDdipBox(storeId, request, otherOwnerId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STORE_MANAGEMENT_NOT_OWNED);
        assertThat(exception.getMessage()).isEqualTo("해당 가게의 소유자가 아닙니다.");
        verify(storeRepository, times(1)).findById(storeId);
        verify(ddipBoxRepository, never()).save(any());
    }

    @Test
    @DisplayName("띱박스 정보 수정 성공")
    void updateDdipBox_success() {
        // Given
        Long ddipboxId = 30L;
        UpdateDdipBoxRequest request = UpdateDdipBoxRequest.builder()
            .ddipboxName("Updated DdipBox Name")
            .description("Updated Description")
            .salePrice(8000L)
            .originalPrice(15000L)
            .build();

        DdipBox existingDdipBox = DdipBox.builder()
            .ddipboxId(ddipboxId)
            .store(store)
            .ddipboxName("Original Name")
            .description("Original Description")
            .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(ddipBoxRepository.findById(ddipboxId)).thenReturn(Optional.of(existingDdipBox));
        when(ddipBoxRepository.save(any(DdipBox.class))).thenReturn(existingDdipBox);

        // When
        DdipBoxManagementResponse response = ddipBoxManagementService.updateDdipBox(storeId, ddipboxId, request, ownerId);

        // Then
        assertThat(response.ddipboxName()).isEqualTo("Updated DdipBox Name");
        assertThat(response.description()).isEqualTo("Updated Description");
        verify(storeRepository, times(1)).findById(storeId);
        verify(ddipBoxRepository, times(1)).findById(ddipboxId);
        verify(ddipBoxRepository, times(1)).save(any(DdipBox.class));
    }

    @Test
    @DisplayName("띱박스 수량 업데이트 성공")
    void updateDdipBoxQuantity_success() {
        // Given
        Long ddipboxId = 30L;
        UpdateDdipBoxQuantityRequest request = new UpdateDdipBoxQuantityRequest(5L, null, false);

        DdipBox existingDdipBox = DdipBox.builder()
            .ddipboxId(ddipboxId)
            .store(store)
            .ddipboxName("Test Name")
            .dailyQuantity(20L)
            .remainingQuantity(15L)
            .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(ddipBoxRepository.findById(ddipboxId)).thenReturn(Optional.of(existingDdipBox));
        when(ddipBoxRepository.save(any(DdipBox.class))).thenReturn(existingDdipBox);

        // When
        DdipBoxManagementResponse response = ddipBoxManagementService.updateDdipBoxQuantity(storeId, ddipboxId, request, ownerId);

        // Then
        assertThat(response.remainingQuantity()).isEqualTo(5L);
        assertThat(response.dailyQuantity()).isEqualTo(20L); // dailyQuantity는 변경되지 않음

        verify(storeRepository, times(1)).findById(storeId);
        verify(ddipBoxRepository, times(1)).findById(ddipboxId);
        verify(ddipBoxRepository, times(1)).save(any(DdipBox.class));
    }
}