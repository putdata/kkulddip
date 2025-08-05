package com.kkulddip.store.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ddip_box_item")
public class DdipBoxItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "ddipbox_item_name", length = 255, nullable = false)
    private String ddipboxItemName;

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "item_quantity", nullable = false)
    private Integer itemQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ddipbox_id", nullable = false)
    private DdipBox ddipBox;
}