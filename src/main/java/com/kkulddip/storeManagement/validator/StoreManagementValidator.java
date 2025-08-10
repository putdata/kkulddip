package com.kkulddip.storeManagement.validator;

/**
 * 가게 관리 도메인 유효성 검증 유틸리티 클래스
 */
public final class StoreManagementValidator {

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;
    
    private static final int MIN_STORE_NAME_LENGTH = 2;
    private static final int MAX_STORE_NAME_LENGTH = 255;
    
    private static final long MIN_PRICE = 0L;
    private static final long MAX_PRICE = 99_999_999L; // 1억원 미만
    
    private static final long MIN_QUANTITY = 1L;
    private static final long MAX_QUANTITY = 999L;

    private StoreManagementValidator() {
        // Utility class - prevent instantiation
    }

    /**
     * 사장님 ID 유효성 검증
     */
    public static void validateOwnerId(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("사장님 ID는 필수입니다.");
        }
    }

    /**
     * 가게명 유효성 검증
     */
    public static void validateStoreName(String storeName) {
        if (storeName == null || storeName.trim().isEmpty()) {
            throw new IllegalArgumentException("가게명은 필수입니다.");
        }
        
        String trimmedName = storeName.trim();
        if (trimmedName.length() < MIN_STORE_NAME_LENGTH) {
            throw new IllegalArgumentException("가게명은 " + MIN_STORE_NAME_LENGTH + "자 이상이어야 합니다.");
        }
        
        if (trimmedName.length() > MAX_STORE_NAME_LENGTH) {
            throw new IllegalArgumentException("가게명은 " + MAX_STORE_NAME_LENGTH + "자를 초과할 수 없습니다.");
        }
    }

    /**
     * 위도 유효성 검증
     */
    public static void validateLatitude(Double latitude) {
        if (latitude == null) {
            throw new IllegalArgumentException("위도는 필수입니다.");
        }
        
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new IllegalArgumentException("위도는 " + MIN_LATITUDE + " ~ " + MAX_LATITUDE + " 범위여야 합니다.");
        }
    }

    /**
     * 경도 유효성 검증
     */
    public static void validateLongitude(Double longitude) {
        if (longitude == null) {
            throw new IllegalArgumentException("경도는 필수입니다.");
        }
        
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException("경도는 " + MIN_LONGITUDE + " ~ " + MAX_LONGITUDE + " 범위여야 합니다.");
        }
    }

    /**
     * 가격 유효성 검증
     */
    public static void validatePrice(Long price, String priceType) {
        if (price == null) {
            throw new IllegalArgumentException(priceType + "는 필수입니다.");
        }
        
        if (price < MIN_PRICE) {
            throw new IllegalArgumentException(priceType + "는 " + MIN_PRICE + "원 이상이어야 합니다.");
        }
        
        if (price > MAX_PRICE) {
            throw new IllegalArgumentException(priceType + "는 " + MAX_PRICE + "원을 초과할 수 없습니다.");
        }
    }


    /**
     * 수량 유효성 검증
     */
    public static void validateQuantity(Long quantity, String quantityType) {
        if (quantity == null) {
            throw new IllegalArgumentException(quantityType + "는 필수입니다.");
        }
        
        if (quantity < MIN_QUANTITY) {
            throw new IllegalArgumentException(quantityType + "는 " + MIN_QUANTITY + "개 이상이어야 합니다.");
        }
        
        if (quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException(quantityType + "는 " + MAX_QUANTITY + "개를 초과할 수 없습니다.");
        }
    }


    /**
     * 카테고리 유효성 검증
     */
    public static void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        }
        
        String trimmedCategory = category.trim();
        if (trimmedCategory.length() > 50) {
            throw new IllegalArgumentException("카테고리는 50자를 초과할 수 없습니다.");
        }
        
        // 허용된 카테고리인지 검증 (필요시 구현)
        // validateAllowedCategory(trimmedCategory);
    }

    /**
     * 전화번호 유효성 검증 (간단한 형식 검증)
     */
    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return; // 전화번호는 선택 사항
        }
        
        String cleanPhone = phoneNumber.replaceAll("[\\s-]", ""); // 공백, 하이픈 제거
        
        if (!cleanPhone.matches("^[0-9]{8,15}$")) {
            throw new IllegalArgumentException("전화번호는 8~15자리 숫자여야 합니다.");
        }
    }

    /**
     * 비즈니스 상태 변경 유효성 검증
     */
    public static void validateStatusChange(Boolean currentStatus, Boolean newStatus, String entityType) {
        if (currentStatus == null) {
            throw new IllegalArgumentException("현재 " + entityType + " 상태를 알 수 없습니다.");
        }
        
        if (newStatus == null) {
            throw new IllegalArgumentException("새로운 " + entityType + " 상태는 필수입니다.");
        }
        
        if (currentStatus.equals(newStatus)) {
            throw new IllegalArgumentException(entityType + " 상태가 이미 " + 
                (newStatus ? "활성화" : "비활성화") + " 되어 있습니다.");
        }
    }
}