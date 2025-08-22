package com.kkulddip.storeManagement.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.storeManagement.dto.request.CreateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreRequest;
import com.kkulddip.storeManagement.dto.response.StoreManagementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreManagementServiceTest {

    @InjectMocks
    private StoreManagementService storeManagementService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreImageService storeImageService;

    private Long ownerId;
    private Store store;
    private CreateStoreRequest createStoreRequest;

    @BeforeEach
    void setUp() {
        ownerId = 1L;
        store = Store.builder()
            .storeId(10L)
            .ownerId(ownerId)
            .storeName("테스트 가게")
            .isActive(true)
            .build();
        createStoreRequest = CreateStoreRequest.builder()
            .storeName("새 가게")
            .storeAddress("서울시")
            .latitude(37.0)
            .longitude(127.0)
            .build();
    }

    @Test
    @DisplayName("가게 생성 성공")
    void createStore_success() {
        // Given
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        // When
        StoreManagementResponse response = storeManagementService.createStore(createStoreRequest, ownerId);

        // Then
        assertThat(response.storeId()).isEqualTo(store.getStoreId());
        assertThat(response.storeName()).isEqualTo(store.getStoreName());
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 정보 수정 성공")
    void updateStore_success() {
        // Given
        UpdateStoreRequest updateRequest = UpdateStoreRequest.builder()
            .storeName("수정된 가게명")
            .description("수정된 설명")
            .build();
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        // When
        StoreManagementResponse response = storeManagementService.updateStore(store.getStoreId(), updateRequest, ownerId);

        // Then
        assertThat(response.storeName()).isEqualTo(updateRequest.storeName());
        verify(storeRepository, times(1)).findById(store.getStoreId());
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("가게 정보 수정 시 소유자가 아니면 실패")
    void updateStore_notOwned_fail() {
        // Given
        Long anotherOwnerId = 99L;
        UpdateStoreRequest updateRequest = UpdateStoreRequest.builder().storeName("수정").build();
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeManagementService.updateStore(store.getStoreId(), updateRequest, anotherOwnerId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STORE_MANAGEMENT_NOT_OWNED);
    }
    
    @Test
    @DisplayName("가게 생성 (이미지 포함) 성공")
    void createStoreWithImage_success() {
        // Given
        MultipartFile image = new MockMultipartFile(
            "image.jpg",
            "image.jpg",
            "image/jpeg",
            "test".getBytes()
        );
        String imageUrl = "https://s3.amazonaws.com/test-bucket/store/uuid.jpg";
        
        when(storeImageService.uploadSingleImageToS3(any(MultipartFile.class))).thenReturn(imageUrl);
        when(storeRepository.save(any(Store.class))).thenReturn(store);
        
        // When
        StoreManagementResponse response = storeManagementService.createStoreWithImage(createStoreRequest, image, ownerId);
        
        // Then
        assertThat(response.storeId()).isEqualTo(store.getStoreId());
        assertThat(response.storeName()).isEqualTo(store.getStoreName());
        verify(storeImageService, times(1)).uploadSingleImageToS3(image);
        verify(storeRepository, times(1)).save(any(Store.class));
    }
    
    @Test
    @DisplayName("가게 이미지 업데이트 성공")
    void updateStoreImage_success() {
        // Given
        MultipartFile newImage = new MockMultipartFile(
            "new-image.jpg",
            "new-image.jpg",
            "image/jpeg",
            "new test".getBytes()
        );
        String oldImageUrl = "https://s3.amazonaws.com/test-bucket/store/old.jpg";
        String newImageUrl = "https://s3.amazonaws.com/test-bucket/store/new.jpg";
        
        store.setStoreProfileImage(oldImageUrl);
        
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(store));
        when(storeImageService.uploadSingleImageToS3(any(MultipartFile.class))).thenReturn(newImageUrl);
        when(storeRepository.save(any(Store.class))).thenReturn(store);
        doNothing().when(storeImageService).deleteImageFromS3(anyString());
        
        // When
        StoreManagementResponse response = storeManagementService.updateStoreImage(store.getStoreId(), newImage, ownerId);
        
        // Then
        assertThat(response.storeId()).isEqualTo(store.getStoreId());
        verify(storeImageService, times(1)).deleteImageFromS3(oldImageUrl);
        verify(storeImageService, times(1)).uploadSingleImageToS3(newImage);
        verify(storeRepository, times(1)).save(any(Store.class));
    }
}