package com.kkulddip.store.repository;

import com.kkulddip.store.entity.DailyDdipBoxInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyDdipBoxInventoryRepository extends JpaRepository<DailyDdipBoxInventory, Long>  {
    List<DailyDdipBoxInventory> findByDdipboxIdInAndCreateAtBetween(List<Long> ddipboxIds, LocalDate startDate, LocalDate endDate);
}
