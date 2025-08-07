package com.kkulddip.store.repository;

import com.kkulddip.store.entity.DdipBoxItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * DdipBoxItem Repository
 */
@Repository
public interface DdipBoxItemRepository extends JpaRepository<DdipBoxItem, Long> {

    /**
     * 특정 띱박스의 구성상품 목록 조회
     */
    @Query("""
        SELECT i FROM DdipBoxItem i 
        WHERE i.ddipBox.ddipboxId = :ddipboxId 
        ORDER BY i.itemId ASC
        """)
    List<DdipBoxItem> findByDdipBoxId(@Param("ddipboxId") Long ddipboxId);
}