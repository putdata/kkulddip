package com.kkulddip.storeManagement.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 가게 관리 도메인 예외 클래스
 */
public class StoreManagementException extends BusinessException {
    
    public StoreManagementException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public StoreManagementException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public StoreManagementException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    // Store 관련 예외 팩토리 메서드
    public static StoreManagementException storeNotFound(Long storeId) {
        return new StoreManagementException(
            ErrorCode.STORE_NOT_FOUND, 
            "존재하지 않는 가게입니다. storeId: " + storeId
        );
    }
    
    public static StoreManagementException storeNotOwned(Long storeId, Long ownerId) {
        return new StoreManagementException(
            ErrorCode.ACCESS_DENIED, 
            "해당 가게의 소유자가 아닙니다. storeId: " + storeId + ", ownerId: " + ownerId
        );
    }
    
    public static StoreManagementException storeAlreadyInactive(Long storeId) {
        return new StoreManagementException(
            ErrorCode.BUSINESS_LOGIC_ERROR, 
            "가게가 이미 비활성화 상태입니다. storeId: " + storeId
        );
    }
    
    public static StoreManagementException storeAlreadyExists(String storeName, Long ownerId) {
        return new StoreManagementException(
            ErrorCode.DUPLICATE_RESOURCE, 
            "이미 존재하는 가게명입니다. storeName: " + storeName + ", ownerId: " + ownerId
        );
    }
    
    // DdipBox 관련 예외 팩토리 메서드
    public static StoreManagementException ddipBoxNotFound(Long ddipboxId) {
        return new StoreManagementException(
            ErrorCode.RESOURCE_NOT_FOUND, 
            "존재하지 않는 띱박스입니다. ddipboxId: " + ddipboxId
        );
    }
    
    public static StoreManagementException ddipBoxHasActiveOrders(Long ddipboxId) {
        return new StoreManagementException(
            ErrorCode.BUSINESS_LOGIC_ERROR, 
            "활성 주문이 있어 띱박스를 삭제할 수 없습니다. ddipboxId: " + ddipboxId
        );
    }
    
    public static StoreManagementException invalidQuantityUpdate(Long ddipboxId, String reason) {
        return new StoreManagementException(
            ErrorCode.BUSINESS_LOGIC_ERROR, 
            "잘못된 수량 업데이트 요청입니다. ddipboxId: " + ddipboxId + ", reason: " + reason
        );
    }
    
    public static StoreManagementException invalidPriceConfiguration(String reason) {
        return new StoreManagementException(
            ErrorCode.VALIDATION_ERROR, 
            "잘못된 가격 설정입니다. " + reason
        );
    }
}