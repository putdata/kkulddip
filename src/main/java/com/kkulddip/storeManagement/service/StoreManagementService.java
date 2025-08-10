package com.kkulddip.storeManagement.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.storeManagement.dto.request.CreateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreStatusRequest;
import com.kkulddip.storeManagement.dto.response.StoreManagementResponse;
import com.kkulddip.storeManagement.dto.response.StoreImageResponseDto;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 가게 관리 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class StoreManagementService {
    
    private final StoreRepository storeRepository;
    private final StoreImageService storeImageService;
    
    /**
     * 가게 생성
     */
    @Transactional
    public StoreManagementResponse createStore(CreateStoreRequest request, Long ownerId) {
        log.info("가게 생성 시작 - ownerId: {}, storeName: {}", ownerId, request.storeName());
        
        // 중복 가게명 검증 (같은 사장님)
        // 현재 owner 관련 도메인 구현 안됨
        // validateDuplicateStoreName(request.storeName(), ownerId);
        
        // Store 엔티티 생성
        Store store = Store.builder()
            .ownerId(ownerId)
            .storeName(request.storeName())
            .phone(request.phone())
            .description(request.description())
            .operatingHours(request.operatingHours())
            .businessNumber(request.businessNumber())
            .storeAddress(request.storeAddress())
            .storeProfileImage(request.storeProfileImage())
            .latitude(request.latitude())
            .longitude(request.longitude())
            .isActive(true)
            .ratingAverage(0.0)
            .reviewCount(0L)
            .createdAt(LocalDateTime.now())
            .build();
        
        Store savedStore = storeRepository.save(store);
        
        log.info("가게 생성 완료 - storeId: {}, ownerId: {}", savedStore.getStoreId(), ownerId);
        return StoreManagementResponse.from(savedStore);
    }

    /**
     * 가게 생성 (이미지 포함)
     */
    @Transactional
    public StoreManagementResponse createStoreWithImages(CreateStoreRequest request, List<MultipartFile> images, Long ownerId) {
        log.info("가게 생성 시작 (이미지 포함) - ownerId: {}, storeName: {}, imageCount: {}", 
            ownerId, request.storeName(), images != null ? images.size() : 0);
        
        // 기본 가게 생성
        StoreManagementResponse response = createStore(request, ownerId);
        
        // 이미지가 있으면 업로드
        if (images != null && !images.isEmpty()) {
            try {
                List<StoreImageResponseDto> uploadedImages = storeImageService.addImage(
                    response.storeId(), images, 0);
                
                log.info("가게 이미지 업로드 완료 - storeId: {}, uploadCount: {}", 
                    response.storeId(), uploadedImages.size());
                    
            } catch (Exception e) {
                log.error("가게 이미지 업로드 실패 - storeId: {}", response.storeId(), e);
                // 이미지 업로드 실패해도 가게 생성은 완료된 상태로 유지
            }
        }
        
        return response;
    }
    
    /**
     * 가게 정보 수정
     */
    @Transactional
    public StoreManagementResponse updateStore(Long storeId, UpdateStoreRequest request, Long ownerId) {
        log.info("가게 정보 수정 시작 - storeId: {}, ownerId: {}", storeId, ownerId);
        
        // 가게 존재 및 소유권 확인
        Store store = validateStoreOwnership(storeId, ownerId);
        
        // 업데이트할 내용이 있는지 확인
        if (!request.hasUpdates()) {
            log.warn("수정할 내용이 없습니다 - storeId: {}", storeId);
            return StoreManagementResponse.from(store);
        }
        
        // 가게명 중복 확인 (변경하는 경우만)
        if (request.storeName() != null && !request.storeName().equals(store.getStoreName())) {
            store.setStoreName(request.storeName());
        }
        
        // 필드별 업데이트
        if (request.phone() != null) {
            store.setPhone(request.phone());
        }
        if (request.description() != null) {
            store.setDescription(request.description());
        }
        if (request.operatingHours() != null) {
            store.setOperatingHours(request.operatingHours());
        }
        if (request.storeAddress() != null) {
            store.setStoreAddress(request.storeAddress());
        }
        if (request.storeProfileImage() != null) {
            store.setStoreProfileImage(request.storeProfileImage());
        }
        if (request.latitude() != null) {
            store.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            store.setLongitude(request.longitude());
        }
        
        store.setUpdatedAt(LocalDateTime.now());
        
        Store updatedStore = storeRepository.save(store);
        
        log.info("가게 정보 수정 완료 - storeId: {}", storeId);
        return StoreManagementResponse.from(updatedStore);
    }
    
    /**
     * 가게 활성화/비활성화 상태 변경
     */
    @Transactional
    public StoreManagementResponse updateStoreStatus(Long storeId, UpdateStoreStatusRequest request, Long ownerId) {
        log.info("가게 상태 변경 시작 - storeId: {}, isActive: {}, ownerId: {}", 
            storeId, request.isActive(), ownerId);
        
        // 가게 존재 및 소유권 확인
        Store store = validateStoreOwnership(storeId, ownerId);
        
        // 현재 상태와 동일한 경우 스킵
        if (store.getIsActive().equals(request.isActive())) {
            log.info("가게 상태가 이미 동일합니다 - storeId: {}, isActive: {}", storeId, request.isActive());
            return StoreManagementResponse.from(store);
        }
        
        // 비활성화하는 경우 활성 주문 확인
        if (!request.isActive()) {
            validateStoreCanBeDeactivated(storeId);
        }
        
        store.setIsActive(request.isActive());
        store.setUpdatedAt(LocalDateTime.now());
        
        Store updatedStore = storeRepository.save(store);
        
        log.info("가게 상태 변경 완료 - storeId: {}, isActive: {}, reason: {}", 
            storeId, request.isActive(), request.reason());
        return StoreManagementResponse.from(updatedStore);
    }
    
    /**
     * 가게 논리적 삭제 (비활성화)
     */
    @Transactional
    public void deleteStore(Long storeId, Long ownerId) {
        log.info("가게 삭제 시작 - storeId: {}, ownerId: {}", storeId, ownerId);

        // 가게 이미지 먼저 삭제
        try {
            storeImageService.deleteImagesBeforeDeleteStore(storeId);
        } catch (Exception e) {
            log.error("가게 삭제 전 이미지 정리 실패 - storeId: {}", storeId, e);
            // 이미지 삭제 실패해도 가게 삭제는 진행
        }

        UpdateStoreStatusRequest deleteRequest = UpdateStoreStatusRequest.builder()
            .isActive(false)
            .reason("가게 삭제")
            .build();

        updateStoreStatus(storeId, deleteRequest, ownerId);

        log.info("가게 삭제 완료 - storeId: {}", storeId);
    }

    /**
     * 가게 소유권 검증
     */
    private Store validateStoreOwnership(Long storeId, Long ownerId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_MANAGEMENT_NOT_FOUND, "가게를 찾을 수 없습니다."));

        if (!store.getOwnerId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.STORE_MANAGEMENT_NOT_OWNED, "해당 가게의 소유자가 아닙니다.");
        }

        return store;
    }
    
    /**
     * 가게 비활성화 가능 여부 검증 (활성 주문이 있는지 확인)
     */
    private void validateStoreCanBeDeactivated(Long storeId) {
        // 현재 프로젝트에서는 Order 도메인과의 직접적인 연관관계가 없으므로
        // 일단 로그만 남기고 필요시 Order 서비스에서 검증 로직 추가 필요
        log.info("가게 비활성화 검증 - storeId: {} (활성 주문 확인 필요)", storeId);
        // TODO: Order 도메인에서 해당 가게의 활성 주문이 있는지 확인하는 로직 추가 필요
        // if (orderService.hasActiveOrdersByStoreId(storeId)) {
        //     throw new BusinessException(ErrorCode.STORE_MANAGEMENT_HAS_ACTIVE_ORDERS, 
        //         "진행 중인 주문이 있어 가게를 비활성화할 수 없습니다.");
        // }
    }
    
}