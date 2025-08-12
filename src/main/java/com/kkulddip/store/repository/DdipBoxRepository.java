package com.kkulddip.store.repository;

import com.kkulddip.store.entity.DdipBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * DdipBox Repository
 */
@Repository
public interface DdipBoxRepository extends JpaRepository<DdipBox, Long> {

    /**
     * 특정 가게의 활성화된 띱박스 목록 조회 (구성상품 포함)
     * 성능상 이슈로 @OneToMany를 사용하지 않으므로 별도 조회 필요
     */
    @Query("""
        SELECT d FROM DdipBox d 
        WHERE d.store.storeId = :storeId 
        AND d.isActive = true 
        ORDER BY d.ddipboxId ASC
        """)
    List<DdipBox> findActiveByStoreIdWithItems(@Param("storeId") Long storeId);

    /**
     * 특정 가게의 활성화된 띱박스 목록 조회 (구성상품 제외)
     * storeId를 이용해 DdipBox를 조회하는 방식은 ManyToOne 관계에 적합.
     */
    @Query("""
        SELECT d FROM DdipBox d 
        WHERE d.store.storeId = :storeId 
        AND d.isActive = true 
        ORDER BY d.ddipboxId ASC
        """)
    List<DdipBox> findActiveByStoreId(@Param("storeId") Long storeId);

    /**
     * 카테고리별 띱박스가 있는 가게 ID 목록 조회
     */
    @Query("""
        SELECT DISTINCT d.store.storeId FROM DdipBox d 
        WHERE d.isActive = true 
        AND d.store.isActive = true
        AND LOWER(d.category) LIKE LOWER(CONCAT('%', :category, '%'))
        """)
    List<Long> findStoreIdsByCategory(@Param("category") String category);

    /**
     * 가게의 대표 띱박스 조회 (가격이 가장 높은 것)
     */
    @Query("""
        SELECT d FROM DdipBox d 
        WHERE d.store.storeId = :storeId 
        AND d.isActive = true 
        ORDER BY d.salePrice DESC
        """)
    List<DdipBox> findRepresentativeDdipBoxByStoreId(@Param("storeId") Long storeId);

    /**
     * 특정 가게의 특정 띱박스 ID들로 활성화된 띱박스 목록 조회 (이벤트 통신용)
     */
    @Query("""
        SELECT d FROM DdipBox d 
        WHERE d.store.storeId = :storeId 
        AND d.ddipboxId IN :ddipBoxIds
        AND d.isActive = true 
        ORDER BY d.ddipboxId ASC
        """)
    List<DdipBox> findActiveByStoreIdAndDdipBoxIds(
        @Param("storeId") Long storeId,
        @Param("ddipBoxIds") List<Long> ddipBoxIds
    );

    /**
     * ddipboxId로 띱박스명을 조회합니다. (비활성화된 띱박스 포함)
     */
    @Query("""
        SELECT d.ddipboxName FROM DdipBox d 
        WHERE d.ddipboxId = :ddipboxId
        """)
    Optional<String> findDdipBoxNameById(@Param("ddipboxId") Long ddipboxId);
}