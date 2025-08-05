package com.kkulddip.store.dto.response;

/**
 * record 형식으로 고칠거임
 */
public class DdipBoxCardViewResponse {
    private Long ddipboxId;
    private String ddipboxName;
    private Long originalPrice;
    private Long salePrice;
    private List<DdipBoxItemDto> itemList; // 구성 상품 리스트 (실제 구성상품 정보)
    private Long remainingQuantity; // 현재 재고 수량
    private String ddipboxImage; // 확장성을 위한 속성
}
