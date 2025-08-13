package com.kkulddip.order.application.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

public class OrderException extends BusinessException {

    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OrderException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public OrderException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    // Static factory methods for common order exceptions
    public static OrderException orderNotFound(String orderId) {
        return new OrderException(ErrorCode.ORDER_NOT_FOUND, 
            String.format("주문을 찾을 수 없습니다. 주문ID: %s", orderId));
    }

    public static OrderException orderCannotBeModified(String orderId) {
        return new OrderException(ErrorCode.ORDER_CANNOT_BE_MODIFIED,
            String.format("주문을 수정할 수 없습니다. 주문ID: %s", orderId));
    }

    public static OrderException orderCannotBeCancelled(String orderId) {
        return new OrderException(ErrorCode.ORDER_CANNOT_BE_CANCELLED,
            String.format("주문을 취소할 수 없습니다. 주문ID: %s", orderId));
    }

    public static OrderException orderCannotBeConfirmed(String orderId) {
        return new OrderException(ErrorCode.ORDER_CANNOT_BE_CONFIRMED,
            String.format("주문을 확인할 수 없습니다. 주문ID: %s", orderId));
    }

    public static OrderException orderCannotBeDelivered(String orderId) {
        return new OrderException(ErrorCode.ORDER_CANNOT_BE_DELIVERED,
            String.format("주문을 배송 완료 처리할 수 없습니다. 주문ID: %s", orderId));
    }

    public static OrderException orderInvalidStatus(String orderId, String currentStatus) {
        return new OrderException(ErrorCode.ORDER_INVALID_STATUS,
            String.format("잘못된 주문 상태입니다. 주문ID: %s, 현재 상태: %s", orderId, currentStatus));
    }

    public static OrderException orderEmptyItems(String orderId) {
        return new OrderException(ErrorCode.ORDER_EMPTY_ITEMS,
            String.format("주문 항목이 비어있습니다. 주문ID: %s", orderId));
    }

    public static OrderException orderPriceValidationFailed(String details) {
        return new OrderException(ErrorCode.ORDER_PRICE_VALIDATION_FAILED, details);
    }

    public static OrderException orderPriceMismatch(String orderId, String details) {
        return new OrderException(ErrorCode.ORDER_PRICE_MISMATCH,
            String.format("요청한 가격이 실제 상품 가격과 일치하지 않습니다. 주문ID: %s, %s", orderId, details));
    }

    public static OrderException orderInvalidQuantity(String orderId, String itemInfo) {
        return new OrderException(ErrorCode.ORDER_INVALID_QUANTITY,
            String.format("잘못된 주문 수량입니다. 주문ID: %s, %s", orderId, itemInfo));
    }

    public static OrderException orderInvalidUnitPrice(String orderId, String itemInfo) {
        return new OrderException(ErrorCode.ORDER_INVALID_UNIT_PRICE,
            String.format("잘못된 단가입니다. 주문ID: %s, %s", orderId, itemInfo));
    }

    public static OrderException orderCreationFailed(Throwable cause) {
        return new OrderException(ErrorCode.ORDER_CREATION_FAILED, cause);
    }

    public static OrderException orderUpdateFailed(String orderId, Throwable cause) {
        return new OrderException(ErrorCode.ORDER_UPDATE_FAILED, cause);
    }
    
    // 주문 중복 생성 방지
    public static OrderException orderAlreadyProcessing(Long customerId) {
        return new OrderException(ErrorCode.ORDER_ALREADY_EXISTS,
            "이미 주문을 처리 중입니다. 잠시 후 다시 시도해주세요. CustomerId: " + customerId);
    }

    public static OrderException orderPersistenceError(String orderId, Throwable cause) {
        return new OrderException(ErrorCode.ORDER_PERSISTENCE_ERROR, cause);
    }

    public static OrderException orderDatabaseError(Throwable cause) {
        return new OrderException(ErrorCode.ORDER_DATABASE_ERROR, cause);
    }

    public static OrderException orderExternalApiError(Throwable cause) {
        return new OrderException(ErrorCode.ORDER_EXTERNAL_API_ERROR, cause);
    }

    public static OrderException orderEventPublishFailed(Throwable cause) {
        return new OrderException(ErrorCode.ORDER_EVENT_PUBLISH_FAILED, cause);
    }

    public static OrderException accessDenied(String message) {
        return new OrderException(ErrorCode.ORDER_ACCESS_DENIED, message);
    }

    public static OrderException customerNotFound(Long customerId) {
        return new OrderException(ErrorCode.CUSTOMER_NOT_FOUND,
            String.format("고객을 찾을 수 없습니다. customerId: %s", customerId));
    }

    public static OrderException customerStatsUpdateFailed(Long customerId, Throwable cause) {
        return new OrderException(ErrorCode.CUSTOMER_STATS_UPDATE_FAILED, cause);
    }
}