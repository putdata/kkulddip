package com.kkulddip.order.domain.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class OrderItem {

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Embedded
    private Money unitPrice;

    public OrderItem(Long productId, String productName, Integer quantity, Money unitPrice) {
        validateProductId(productId);
        validateProductName(productName);
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);
        
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("상품 ID는 양수여야 합니다.");
        }
    }

    private void validateProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (productName.length() > 100) {
            throw new IllegalArgumentException("상품명은 100자 이하여야 합니다.");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
    }

    private void validateUnitPrice(Money unitPrice) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("단가는 필수입니다.");
        }
    }

    public Money getTotalPrice() {
        return unitPrice.multiply(quantity);
    }

    public OrderItem changeQuantity(Integer newQuantity) {
        validateQuantity(newQuantity);
        return new OrderItem(this.productId, this.productName, newQuantity, this.unitPrice);
    }

    @Override
    public String toString() {
        return String.format("%s x %d = %s", productName, quantity, getTotalPrice());
    }
} 