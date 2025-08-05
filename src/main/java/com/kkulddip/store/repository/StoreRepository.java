package com.kkulddip.store.repository;

import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 기본 Store Repository
 * 단건 조회 및 기본 CRUD 작업용
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * 활성화된 가게 단건 조회 (ddipBoxes Fetch Join)
     */
    @Query("""
        SELECT s FROM Store s 
        LEFT JOIN FETCH s.ddipBoxes d 
        WHERE s.storeId = :storeId 
        AND s.isActive = true
        AND (d IS NULL OR d.isActive = true)
        """)
    Optional<Store> findActiveStoreWithDdipBoxes(@Param("storeId") Long storeId);

    /**
     * 활성화된 가게 존재 여부 확인
     */
    @Query("""
        SELECT COUNT(s) > 0 FROM Store s 
        WHERE s.storeId = :storeId 
        AND s.isActive = true
        """)
    boolean existsActiveStore(@Param("storeId") Long storeId);

    /**
     * 가게의 실시간 평점 계산 (Review 테이블이 있다고 가정)
     * 현재는 Store 테이블의 ratingAverage 사용
     */
    @Query("""
        SELECT s.ratingAverage FROM Store s 
        WHERE s.storeId = :storeId
        """)
    Optional<Double> calculateStoreRating(@Param("storeId") Long storeId);

    /**
     * 가게의 대표 띱박스 정보 조회
     */
    @Query("""
        SELECT d FROM Store s 
        JOIN s.ddipBoxes d 
        WHERE s.storeId = :storeId 
        AND s.isActive = true 
        AND d.isActive = true
        ORDER BY d.salePrice DESC
        """)
    List<DdipBox> findRepresentativeDdipBox(@Param("storeId") Long storeId);
}