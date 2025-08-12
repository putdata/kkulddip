package com.kkulddip.order.application.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.kkulddip.order.presentation.rest.dto.request.OrderItemRequest;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.entity.DdipBox;

/**
 * 가격 검증 서비스 실제 구현체
 * DdipBox 도메인과 통신하여 실제 상품 가격과 클라이언트 전달 가격을 비교 검증합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PriceValidationServiceImpl implements PriceValidationService {
    
    private final DdipBoxRepository ddipBoxRepository;
    
    @Override
    public void validatePrices(Long storeId, List<OrderItemRequest> orderItems) {
        log.info("가격 검증 시작 - storeId: {}, itemCount: {}", storeId, orderItems.size());
        
        for (OrderItemRequest item : orderItems) {
            validateOrderItem(storeId, item);
        }
        
        log.info("가격 검증 완료 - storeId: {}, itemCount: {}", storeId, orderItems.size());
    }
    
    private void validateOrderItem(Long storeId, OrderItemRequest item) {
        // 기본 값 검증
        if (item.quantity() == null || item.quantity() <= 0) {
            throw PriceValidationException.invalidQuantity(item.productId(), item.quantity());
        }
        
        if (item.unitPrice() == null || item.unitPrice() < 0) {
            throw PriceValidationException.invalidUnitPrice(item.productId(), item.unitPrice());
        }
        
        // DdipBox에서 실제 가격 조회 (productId = ddipboxId)
        Optional<DdipBox> ddipBoxOpt = ddipBoxRepository.findById(item.productId());
        
        if (ddipBoxOpt.isEmpty()) {
            log.warn("존재하지 않는 상품입니다 - storeId: {}, productId: {}", storeId, item.productId());
            throw PriceValidationException.priceMismatch(
                item.productId(), 
                item.unitPrice(), 
                -1  // 존재하지 않는 상품의 경우 -1로 표시
            );
        }
        
        DdipBox ddipBox = ddipBoxOpt.get();
        
        // 해당 매장의 상품인지 확인
        if (!ddipBox.getStore().getStoreId().equals(storeId)) {
            log.warn("다른 매장의 상품입니다 - storeId: {}, productId: {}, actualStoreId: {}", 
                storeId, item.productId(), ddipBox.getStore().getStoreId());
            throw PriceValidationException.priceMismatch(
                item.productId(), 
                item.unitPrice(), 
                -1
            );
        }
        
        // 활성화된 상품인지 확인
        if (!ddipBox.getIsActive()) {
            log.warn("비활성화된 상품입니다 - storeId: {}, productId: {}", storeId, item.productId());
            throw PriceValidationException.priceMismatch(
                item.productId(), 
                item.unitPrice(), 
                -1
            );
        }
        
        // 가격 비교 (Long salePrice -> Integer로 변환)
        Integer actualPrice = ddipBox.getSalePrice().intValue();
        
        if (!item.unitPrice().equals(actualPrice)) {
            log.warn("가격 불일치 - storeId: {}, productId: {}, requestPrice: {}, actualPrice: {}", 
                storeId, item.productId(), item.unitPrice(), actualPrice);
            throw PriceValidationException.priceMismatch(
                item.productId(), 
                item.unitPrice(), 
                actualPrice
            );
        }
        
        log.debug("가격 검증 성공 - storeId: {}, productId: {}, price: {}, quantity: {}", 
            storeId, item.productId(), item.unitPrice(), item.quantity());
    }
}