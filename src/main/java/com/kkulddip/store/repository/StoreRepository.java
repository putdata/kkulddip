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
     * 활성화된 가게 단건 조회
     * 기존 findActiveStoreWithDdipBoxes는 제거됨
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.storeId = :storeId 
        AND s.isActive = true
        """)
    Optional<Store> findActiveStore(@Param("storeId") Long storeId);

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
     * 기존 calculateStoreRating은 Store 엔티티 필드를 바로 가져오므로 그대로 유지
     */
    @Query("""
        SELECT s.ratingAverage FROM Store s 
        WHERE s.storeId = :storeId
        """)
    Optional<Double> calculateStoreRating(@Param("storeId") Long storeId);

    /**
     * storeId로 ownerId를 조회합니다.
     */
    @Query("""
        SELECT s.ownerId FROM Store s 
        WHERE s.storeId = :storeId 
        AND s.isActive = true
        """)
    Optional<Long> findOwnerIdByStoreId(@Param("storeId") Long storeId);

    /**
     * ownerId로 관리하는 모든 활성 가게의 ID 목록을 조회합니다.
     */
    @Query("""
        SELECT s.storeId FROM Store s 
        WHERE s.ownerId = :ownerId 
        AND s.isActive = true
        """)
    List<Long> findStoreIdsByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * storeId로 상점명을 조회합니다. (비활성화된 상점 포함)
     */
    @Query("""
        SELECT s.storeName FROM Store s 
        WHERE s.storeId = :storeId
        """)
    Optional<String> findStoreNameByStoreId(@Param("storeId") Long storeId);

    /**
     * ownerId로 관리하는 모든 가게 목록을 조회합니다 (활성/비활성 포함)
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.ownerId = :ownerId 
        ORDER BY s.createdAt DESC
        """)
    List<Store> findAllByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * ownerId로 관리하는 활성 가게 목록을 조회합니다
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.ownerId = :ownerId 
        AND s.isActive = true 
        ORDER BY s.createdAt DESC
        """)
    List<Store> findActiveStoresByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * ownerId의 가게 수를 조회합니다
     */
    @Query("""
        SELECT COUNT(s) FROM Store s 
        WHERE s.ownerId = :ownerId
        """)
    Long countByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * ownerId의 활성 가게 수를 조회합니다
     */
    @Query("""
        SELECT COUNT(s) FROM Store s 
        WHERE s.ownerId = :ownerId 
        AND s.isActive = true
        """)
    Long countActiveByOwnerId(@Param("ownerId") Long ownerId);
}