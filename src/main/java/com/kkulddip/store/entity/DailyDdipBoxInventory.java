package com.kkulddip.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;

@Entity
@Table(name = "daily_ddipbox_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyDdipBoxInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_ddipbox_inventory_id")
    private Long dailyDdipboxInventoryId;

    @Column(name = "ddipbox_id", nullable = false)
    private Long ddipboxId;

    @Column(name = "daily_quantity", nullable = false)
    private Long dailyQuantity;

    @Column(name = "remaining_quantity", nullable = false)
    private Long remainingQuantity;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false)
    private LocalDate createAt;

    // DdipBox 엔티티와의 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ddipbox_id", insertable = false, updatable = false)
    private DdipBox ddipBox;
}
