package com.kkulddip.store.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ddip_box")
public class DdipBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ddipbox_id")
    private Long ddipboxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "ddipbox_name", length = 100, nullable = false)
    private String ddipboxName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "original_price", nullable = false)
    private Long originalPrice;

    @Column(name = "sale_price", nullable = false)
    private Long salePrice;

    @Column(name = "daily_quantity", nullable = false)
    private Long dailyQuantity;

    @Column(name = "remaining_quantity", nullable = false)
    private Long remainingQuantity;

    @Column(name = "max_per_customer", nullable = false)
    private Long maxPerCustomer;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // 구성 상품들과의 관계 (확장성을 위해 추가)
    @OneToMany(mappedBy = "ddipBox", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DdipBoxItem> ddipBoxItems = new ArrayList<>();
}