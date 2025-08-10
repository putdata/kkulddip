//package com.kkulddip.storeManagement.service;
//
//import com.kkulddip.storeManagement.dto.request.CreateDdipBoxRequest;
//import com.kkulddip.storeManagement.dto.request.UpdateDdipBoxRequest;
//import com.kkulddip.storeManagement.dto.request.UpdateDdipBoxQuantityRequest;
//import com.kkulddip.storeManagement.dto.request.UpdateStoreStatusRequest;
//import com.kkulddip.storeManagement.dto.response.DdipBoxManagementResponse;
//import com.kkulddip.store.entity.Store;
//import com.kkulddip.store.entity.DdipBox;
//import com.kkulddip.store.repository.StoreRepository;
//import com.kkulddip.store.repository.DdipBoxRepository;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 띱박스 관리 서비스
// */
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class DdipBoxManagementService {
//
//    private final StoreRepository storeRepository;
//    private final DdipBoxRepository ddipBoxRepository;
//
//    /**
//     * 띱박스 생성
//     */
//    @Transactional
//    public DdipBoxManagementResponse createDdipBox(Long storeId, CreateDdipBoxRequest request, Long ownerId) {
//        log.info("띱박스 생성 시작 - storeId: {}, ddipboxName: {}, ownerId: {}",
//            storeId, request.ddipboxName(), ownerId);
//
//        // 가게 존재 및 소유권 확인
//        Store store = validateStoreOwnership(storeId, ownerId);
//
//        // 가격 유효성 검증
//        validatePriceConfiguration(request.originalPrice(), request.salePrice());
//
//        // 수량 유효성 검증
//        validateQuantityConfiguration(request.dailyQuantity(), request.maxPerCustomer());
//
//        // DdipBox 엔티티 생성
//        DdipBox ddipBox = DdipBox.builder()
//            .store(store)
//            .ddipboxName(request.ddipboxName())
//            .description(request.description())
//            .category(request.category())
//            .originalPrice(request.originalPrice())
//            .salePrice(request.salePrice())
//            .dailyQuantity(request.dailyQuantity())
//            .remainingQuantity(request.dailyQuantity()) // 초기값은 일일 수량과 동일
//            .maxPerCustomer(request.maxPerCustomer())
//            .isActive(true)
//            .build();
//
//        DdipBox savedDdipBox = ddipBoxRepository.save(ddipBox);
//
//        log.info("띱박스 생성 완료 - ddipboxId: {}, storeId: {}", savedDdipBox.getDdipboxId(), storeId);
//        return DdipBoxManagementResponse.from(savedDdipBox);
//    }
//
//    /**
//     * 띱박스 정보 수정
//     */
//    @Transactional
//    public DdipBoxManagementResponse updateDdipBox(Long storeId, Long ddipboxId,
//                                                  UpdateDdipBoxRequest request, Long ownerId) {
//        log.info("띱박스 정보 수정 시작 - storeId: {}, ddipboxId: {}, ownerId: {}",
//            storeId, ddipboxId, ownerId);
//
//        // 가게 소유권 확인
//        validateStoreOwnership(storeId, ownerId);
//
//        // 띱박스 존재 확인
//        DdipBox ddipBox = ddipBoxRepository.findById(ddipboxId)
//            .orElseThrow(() -> StoreManagementException.ddipBoxNotFound(ddipboxId));
//
//        // 띱박스가 해당 가게의 것인지 확인
//        if (!ddipBox.getStore().getStoreId().equals(storeId)) {
//            throw StoreManagementException.ddipBoxNotFound(ddipboxId);
//        }
//
//        // 업데이트할 내용이 있는지 확인
//        if (!request.hasUpdates()) {
//            log.warn("수정할 내용이 없습니다 - ddipboxId: {}", ddipboxId);
//            return DdipBoxManagementResponse.from(ddipBox);
//        }
//
//        // 필드별 업데이트
//        if (request.ddipboxName() != null) {
//            ddipBox.setDdipboxName(request.ddipboxName());
//        }
//        if (request.description() != null) {
//            ddipBox.setDescription(request.description());
//        }
//        if (request.category() != null) {
//            ddipBox.setCategory(request.category());
//        }
//
//        // 가격 정보 업데이트 (둘 다 있는 경우 유효성 검증)
//        Long newOriginalPrice = request.originalPrice() != null ? request.originalPrice() : ddipBox.getOriginalPrice();
//        Long newSalePrice = request.salePrice() != null ? request.salePrice() : ddipBox.getSalePrice();
//
//        validatePriceConfiguration(newOriginalPrice, newSalePrice);
//
//        if (request.originalPrice() != null) {
//            ddipBox.setOriginalPrice(request.originalPrice());
//        }
//        if (request.salePrice() != null) {
//            ddipBox.setSalePrice(request.salePrice());
//        }
//
//        // 수량 정보 업데이트
//        if (request.dailyQuantity() != null || request.maxPerCustomer() != null) {
//            Long newDailyQuantity = request.dailyQuantity() != null ? request.dailyQuantity() : ddipBox.getDailyQuantity();
//            Long newMaxPerCustomer = request.maxPerCustomer() != null ? request.maxPerCustomer() : ddipBox.getMaxPerCustomer();
//
//            validateQuantityConfiguration(newDailyQuantity, newMaxPerCustomer);
//
//            if (request.dailyQuantity() != null) {
//                ddipBox.setDailyQuantity(request.dailyQuantity());
//                // 일일 수량 변경 시 잔여 수량도 조정 (기존 비율 유지)
//                if (ddipBox.getRemainingQuantity() > request.dailyQuantity()) {
//                    ddipBox.setRemainingQuantity(request.dailyQuantity());
//                }
//            }
//            if (request.maxPerCustomer() != null) {
//                ddipBox.setMaxPerCustomer(request.maxPerCustomer());
//            }
//        }
//
//        DdipBox updatedDdipBox = ddipBoxRepository.save(ddipBox);
//
//        log.info("띱박스 정보 수정 완료 - ddipboxId: {}", ddipboxId);
//        return DdipBoxManagementResponse.from(updatedDdipBox);
//    }
//
//    /**
//     * 띱박스 재고 수량 업데이트
//     */
//    @Transactional
//    public DdipBoxManagementResponse updateDdipBoxQuantity(Long storeId, Long ddipboxId,
//                                                          UpdateDdipBoxQuantityRequest request, Long ownerId) {
//        log.info("띱박스 재고 업데이트 시작 - storeId: {}, ddipboxId: {}, ownerId: {}",
//            storeId, ddipboxId, ownerId);
//
//        // 가게 소유권 확인
//        validateStoreOwnership(storeId, ownerId);
//
//        // 띱박스 존재 확인
//        DdipBox ddipBox = ddipBoxRepository.findById(ddipboxId)
//            .orElseThrow(() -> StoreManagementException.ddipBoxNotFound(ddipboxId));
//
//        // 띱박스가 해당 가게의 것인지 확인
//        if (!ddipBox.getStore().getStoreId().equals(storeId)) {
//            throw StoreManagementException.ddipBoxNotFound(ddipboxId);
//        }
//
//        // 유효한 업데이트 작업인지 확인
//        if (!request.hasValidOperation()) {
//            throw StoreManagementException.invalidQuantityUpdate(ddipboxId, "유효하지 않은 수량 업데이트 요청");
//        }
//
//        if (request.isDirectQuantityUpdate()) {
//            // 직접 잔여 수량 설정
//            if (request.remainingQuantity() > ddipBox.getDailyQuantity()) {
//                throw StoreManagementException.invalidQuantityUpdate(
//                    ddipboxId, "잔여 수량은 일일 수량을 초과할 수 없습니다"
//                );
//            }
//            ddipBox.setRemainingQuantity(request.remainingQuantity());
//
//        } else if (request.isDailyQuantityReset()) {
//            // 일일 수량 재설정 및 잔여 수량 초기화
//            ddipBox.setDailyQuantity(request.dailyQuantity());
//            ddipBox.setRemainingQuantity(request.dailyQuantity());
//        }
//
//        DdipBox updatedDdipBox = ddipBoxRepository.save(ddipBox);
//
//        log.info("띱박스 재고 업데이트 완료 - ddipboxId: {}, remainingQuantity: {}",
//            ddipboxId, updatedDdipBox.getRemainingQuantity());
//        return DdipBoxManagementResponse.from(updatedDdipBox);
//    }
//
//    /**
//     * 띱박스 활성화/비활성화 상태 변경
//     */
//    @Transactional
//    public DdipBoxManagementResponse updateDdipBoxStatus(Long storeId, Long ddipboxId,
//                                                        UpdateStoreStatusRequest request, Long ownerId) {
//        log.info("띱박스 상태 변경 시작 - storeId: {}, ddipboxId: {}, isActive: {}, ownerId: {}",
//            storeId, ddipboxId, request.isActive(), ownerId);
//
//        // 가게 소유권 확인
//        validateStoreOwnership(storeId, ownerId);
//
//        // 띱박스 존재 확인
//        DdipBox ddipBox = ddipBoxRepository.findById(ddipboxId)
//            .orElseThrow(() -> StoreManagementException.ddipBoxNotFound(ddipboxId));
//
//        // 띱박스가 해당 가게의 것인지 확인
//        if (!ddipBox.getStore().getStoreId().equals(storeId)) {
//            throw StoreManagementException.ddipBoxNotFound(ddipboxId);
//        }
//
//        // 현재 상태와 동일한 경우 스킵
//        if (ddipBox.getIsActive().equals(request.isActive())) {
//            log.info("띱박스 상태가 이미 동일합니다 - ddipboxId: {}, isActive: {}", ddipboxId, request.isActive());
//            return DdipBoxManagementResponse.from(ddipBox);
//        }
//
//        // 비활성화하는 경우 활성 주문 확인 (필요시 구현)
//        if (!request.isActive()) {
//            validateDdipBoxNoActiveOrders(ddipboxId);
//        }
//
//        ddipBox.setIsActive(request.isActive());
//        DdipBox updatedDdipBox = ddipBoxRepository.save(ddipBox);
//
//        log.info("띱박스 상태 변경 완료 - ddipboxId: {}, isActive: {}, reason: {}",
//            ddipboxId, request.isActive(), request.reason());
//        return DdipBoxManagementResponse.from(updatedDdipBox);
//    }
//
//    /**
//     * 띱박스 논리적 삭제 (비활성화)
//     */
//    @Transactional
//    public void deleteDdipBox(Long storeId, Long ddipboxId, Long ownerId) {
//        log.info("띱박스 삭제 시작 - storeId: {}, ddipboxId: {}, ownerId: {}", storeId, ddipboxId, ownerId);
//
//        UpdateStoreStatusRequest deleteRequest = UpdateStoreStatusRequest.builder()
//            .isActive(false)
//            .reason("띱박스 삭제")
//            .build();
//
//        updateDdipBoxStatus(storeId, ddipboxId, deleteRequest, ownerId);
//
//        log.info("띱박스 삭제 완료 - ddipboxId: {}", ddipboxId);
//    }
//
//    /**
//     * 가게의 모든 띱박스 조회 (관리용)
//     */
//    @Transactional(readOnly = true)
//    public List<DdipBoxManagementResponse> getDdipBoxesByStore(Long storeId, Long ownerId) {
//        log.info("가게 띱박스 목록 조회 - storeId: {}, ownerId: {}", storeId, ownerId);
//
//        // 가게 소유권 확인
//        validateStoreOwnership(storeId, ownerId);
//
//        // 모든 띱박스 조회 (비활성화된 것도 포함)
//        List<DdipBox> ddipBoxes = ddipBoxRepository.findByStore_StoreId(storeId);
//
//        return ddipBoxes.stream()
//            .map(DdipBoxManagementResponse::from)
//            .collect(Collectors.toList());
//    }
//
//    /**
//     * 가게 소유권 검증
//     */
//    private Store validateStoreOwnership(Long storeId, Long ownerId) {
//        Store store = storeRepository.findById(storeId)
//            .orElseThrow(() -> StoreManagementException.storeNotFound(storeId));
//
//        if (!store.getOwnerId().equals(ownerId)) {
//            throw StoreManagementException.storeNotOwned(storeId, ownerId);
//        }
//
//        return store;
//    }
//
//    /**
//     * 가격 구성 유효성 검증
//     */
//    private void validatePriceConfiguration(Long originalPrice, Long salePrice) {
//        if (salePrice > originalPrice) {
//            throw StoreManagementException.invalidPriceConfiguration(
//                "판매가는 정가보다 높을 수 없습니다. 정가: " + originalPrice + ", 판매가: " + salePrice
//            );
//        }
//    }
//
//    /**
//     * 수량 구성 유효성 검증
//     */
//    private void validateQuantityConfiguration(Long dailyQuantity, Long maxPerCustomer) {
//        if (maxPerCustomer > dailyQuantity) {
//            throw StoreManagementException.invalidQuantityUpdate(
//                null, "고객당 최대 구매 수량은 일일 수량보다 많을 수 없습니다. 일일수량: " + dailyQuantity + ", 고객당최대: " + maxPerCustomer
//            );
//        }
//    }
//
//    /**
//     * 띱박스 활성 주문 존재 여부 확인
//     */
//    private void validateDdipBoxNoActiveOrders(Long ddipboxId) {
//        // 현재 프로젝트 구조상 DdipBox와 Order의 직접적인 연관관계가 명확하지 않아
//        // 일단 스킵하고 필요시 Order 도메인에서 ProductId를 통해 확인하는 로직 추가 필요
//        log.info("띱박스 활성 주문 확인 스킵 - ddipboxId: {}", ddipboxId);
//    }
//}