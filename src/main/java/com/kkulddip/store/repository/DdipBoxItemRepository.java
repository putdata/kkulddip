package com.kkulddip.store.repository;

import com.kkulddip.store.entity.DdipBoxItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Collection;
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
    
    /**
     * 특정 띱박스의 모든 아이템 무게 총합 계산
     */
    @Query("""
        SELECT SUM(i.weight * i.itemQuantity) 
        FROM DdipBoxItem i 
        WHERE i.ddipBox.ddipboxId = :ddipboxId
        """)
    Integer sumWeightByDdipboxId(@Param("ddipboxId") Long ddipboxId);
    
    /**
     * 여러 띱박스의 모든 아이템 무게 총합 계산
     */
    @Query("""
        SELECT SUM(i.weight * i.itemQuantity) 
        FROM DdipBoxItem i 
        WHERE i.ddipBox.ddipboxId IN :ddipboxIds
        """)
    Integer sumWeightByDdipboxIds(@Param("ddipboxIds") List<Long> ddipboxIds);
    
    /**
     * 여러 띱박스의 구성상품 목록을 배치로 조회 (성능 최적화용)
     */
    @Query("""
        SELECT i FROM DdipBoxItem i 
        WHERE i.ddipBox.ddipboxId IN :ddipboxIds 
        ORDER BY i.ddipBox.ddipboxId ASC, i.itemId ASC
        """)
    List<DdipBoxItem> findByDdipBoxIdIn(@Param("ddipboxIds") Collection<Long> ddipboxIds);
}