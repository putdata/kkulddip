package com.kkulddip.store.repository;

import com.kkulddip.store.entity.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cursor 기반 페이지네이션을 위한 Store Repository
 * 대용량 데이터 처리 및 무한 스크롤링 지원
 */
@Repository
public interface CursorStoreRepository extends JpaRepository<Store, Long> {

    /**
     * ID 기준 커서 페이지네이션
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND (:cursor IS NULL OR s.storeId > :cursor)
        ORDER BY s.storeId ASC
        """)
    List<Store> findStoresWithCursorById(
        @Param("cursor") Long cursor,
        Pageable pageable
    );

    /**
     * 생성일시 기준 커서 페이지네이션 (내림차순)
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND (
            :cursorDate IS NULL OR 
            s.createdAt < :cursorDate OR 
            (s.createdAt = :cursorDate AND s.storeId < :cursorId)
        )
        ORDER BY s.createdAt DESC, s.storeId DESC
        """)
    List<Store> findStoresWithCursorByCreatedAtDesc(
        @Param("cursorDate") LocalDateTime cursorDate,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    /**
     * 평점 기준 커서 페이지네이션 (내림차순)
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND (
            :cursorRating IS NULL OR 
            s.ratingAverage < :cursorRating OR 
            (s.ratingAverage = :cursorRating AND s.storeId < :cursorId)
        )
        ORDER BY s.ratingAverage DESC, s.storeId DESC
        """)
    List<Store> findStoresWithCursorByRatingDesc(
        @Param("cursorRating") Double cursorRating,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    /**
     * 거리 기준 커서 페이지네이션 (거리 계산 포함)
     */
    @Query(value = """
        SELECT s.*, 
               (6371 * acos(cos(radians(:userLat)) 
                          * cos(radians(s.latitude)) 
                          * cos(radians(s.longitude) - radians(:userLng)) 
                          + sin(radians(:userLat)) 
                          * sin(radians(s.latitude)))) AS distance
        FROM store s
        WHERE s.is_active = true 
        AND s.latitude IS NOT NULL 
        AND s.longitude IS NOT NULL
        AND (
            :cursorDistance IS NULL OR 
            (6371 * acos(cos(radians(:userLat)) 
                        * cos(radians(s.latitude)) 
                        * cos(radians(s.longitude) - radians(:userLng)) 
                        + sin(radians(:userLat)) 
                        * sin(radians(s.latitude)))) > :cursorDistance OR
            ((6371 * acos(cos(radians(:userLat)) 
                         * cos(radians(s.latitude)) 
                         * cos(radians(s.longitude) - radians(:userLng)) 
                         + sin(radians(:userLat)) 
                         * sin(radians(s.latitude)))) = :cursorDistance 
             AND s.store_id > :cursorId)
        )
        ORDER BY distance ASC, s.store_id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Store> findStoresWithCursorByDistance(
        @Param("userLat") Double userLatitude,
        @Param("userLng") Double userLongitude,
        @Param("cursorDistance") Double cursorDistance,
        @Param("cursorId") Long cursorId,
        @Param("limit") int limit
    );

    /**
     * 검색 키워드 기준 커서 페이지네이션
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND (s.storeName LIKE %:keyword% OR s.description LIKE %:keyword%)
        AND (:cursor IS NULL OR s.storeId > :cursor)
        ORDER BY s.storeId ASC
        """)
    List<Store> findStoresWithCursorBySearch(
        @Param("keyword") String keyword,
        @Param("cursor") Long cursor,
        Pageable pageable
    );

    /**
     * 카테고리 기준 커서 페이지네이션 (수정)
     * DdipBox가 Store를 ManyToOne으로 참조하는 관계를 활용하여 서브쿼리로 구현
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND s.storeId IN (
            SELECT d.store.storeId FROM DdipBox d
            WHERE d.isActive = true AND d.category = :category
        )
        AND (:cursor IS NULL OR s.storeId > :cursor)
        ORDER BY s.storeId ASC
        """)
    List<Store> findStoresWithCursorByCategory(
        @Param("category") String category,
        @Param("cursor") Long cursor,
        Pageable pageable
    );

    /**
     * 활성화된 가게 수 조회 (전체)
     */
    @Query("SELECT COUNT(s) FROM Store s WHERE s.isActive = true")
    long countActiveStores();

    /**
     * 검색 결과 수 조회
     */
    @Query("""
        SELECT COUNT(s) FROM Store s 
        WHERE s.isActive = true 
        AND (s.storeName LIKE %:keyword% OR s.description LIKE %:keyword%)
        """)
    long countBySearchKeyword(@Param("keyword") String keyword);

    /**
     * 카테고리별 가게 수 조회
     */
    @Query("""
        SELECT COUNT(DISTINCT s) FROM Store s 
        WHERE s.isActive = true 
        AND s.storeId IN (
            SELECT d.store.storeId FROM DdipBox d
            WHERE d.isActive = true AND d.category = :category
        )
        """)
    long countByCategory(@Param("category") String category);
}