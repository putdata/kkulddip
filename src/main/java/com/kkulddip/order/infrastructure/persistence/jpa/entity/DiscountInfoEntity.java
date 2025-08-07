package com.kkulddip.order.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.kkulddip.order.domain.model.enums.DiscountType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "discount_infos")
public class DiscountInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_info_id", nullable = false)
    private Long discountInfoId;
    
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", referencedColumnName = "order_item_id")
    private OrderItemEntity orderItem;
    
    @Column(name = "discount_code", nullable = false)
    private String discountCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;
    
    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;
    
    @Builder
    public DiscountInfoEntity(Long discountInfoId, String discountCode, 
            DiscountType discountType, Integer discountAmount) {
        this.discountInfoId = discountInfoId;
        this.discountCode = discountCode;
        this.discountType = discountType;
        this.discountAmount = discountAmount;
    }
}