package com.kkulddip.store.repository;

import com.kkulddip.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CursorStoreRepository extends JpaRepository<Store, Long> {

    /**
     * ID 기준 Cursor 페이지네이션
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND (:cursor IS NULL OR s.storeId > :cursor)
        ORDER BY s.storeId ASC
        """)
    List<Store> findStoresWithCursorById(@Param("cursor") Long cursor,
                                         org.springframework.data.domain.Pageable pageable);

    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND (:cursor IS NULL OR s.storeId < :cursor)
        ORDER BY s.storeId DESC
        """)
    List<Store> findStoresWithCursorByIdDesc(@Param("cursor") Long cursor,
                                             org.springframework.data.domain.Pageable pageable);

    /**
     * 생성일 기준 Cursor 페이지네이션 (최신순)
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND (:cursorDate IS NULL OR s.createdAt < :cursorDate 
             OR (s.createdAt = :cursorDate AND s.storeId < :cursorId))
        ORDER BY s.createdAt DESC, s.storeId DESC
        """)
    List<Store> findStoresWithCursorByCreatedAtDesc(@Param("cursorDate") LocalDateTime cursorDate,
                                                    @Param("cursorId") Long cursorId,
                                                    org.springframework.data.domain.Pageable pageable);

    /**
     * 평점 기준 Cursor 페이지네이션 (높은 평점순)
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND s.ratingAverage IS NOT NULL
        AND (:cursorRating IS NULL OR s.ratingAverage < :cursorRating 
             OR (s.ratingAverage = :cursorRating AND s.storeId < :cursorId))
        ORDER BY s.ratingAverage DESC, s.storeId DESC
        """)
    List<Store> findStoresWithCursorByRatingDesc(@Param("cursorRating") Double cursorRating,
                                                 @Param("cursorId") Long cursorId,
                                                 org.springframework.data.domain.Pageable pageable);

    /**
     * 거리 기준 Cursor 페이지네이션 (가까운 순)
     */
    @Query(value = """
        SELECT s.*, 
               (6371 * ACOS(COS(RADIANS(:userLat)) 
                   * COS(RADIANS(s.latitude)) 
                   * COS(RADIANS(s.longitude) - RADIANS(:userLng)) 
                   + SIN(RADIANS(:userLat)) 
                   * SIN(RADIANS(s.latitude)))) AS distance
        FROM store s 
        WHERE s.is_active = true 
          AND s.latitude IS NOT NULL 
          AND s.longitude IS NOT NULL
          AND (:cursorDistance IS NULL OR 
               (6371 * ACOS(COS(RADIANS(:userLat)) 
                   * COS(RADIANS(s.latitude)) 
                   * COS(RADIANS(s.longitude) - RADIANS(:userLng)) 
                   + SIN(RADIANS(:userLat)) 
                   * SIN(RADIANS(s.latitude)))) > :cursorDistance
               OR ((6371 * ACOS(COS(RADIANS(:userLat)) 
                   * COS(RADIANS(s.latitude)) 
                   * COS(RADIANS(s.longitude) - RADIANS(:userLng)) 
                   + SIN(RADIANS(:userLat)) 
                   * SIN(RADIANS(s.latitude)))) = :cursorDistance 
                   AND s.store_id > :cursorId))
        ORDER BY distance ASC, s.store_id ASC
        LIMIT :limit
        """,
        nativeQuery = true)
    List<Store> findStoresWithCursorByDistance(@Param("userLat") Double userLatitude,
                                               @Param("userLng") Double userLongitude,
                                               @Param("cursorDistance") Double cursorDistance,
                                               @Param("cursorId") Long cursorId,
                                               @Param("limit") int limit);

    /**
     * 검색 + Cursor 페이지네이션
     */
    @Query("""
        SELECT s FROM Store s 
        WHERE s.isActive = true 
        AND LOWER(s.storeName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        AND (:cursor IS NULL OR s.storeId > :cursor)
        ORDER BY s.storeId ASC
        """)
    List<Store> findStoresWithCursorBySearch(@Param("keyword") String keyword,
                                             @Param("cursor") Long cursor,
                                             org.springframework.data.domain.Pageable pageable);

    /**
     * 다음 페이지 존재 여부 확인
     */
    @Query("""
        SELECT COUNT(s) > 0 FROM Store s 
        WHERE s.isActive = true 
        AND s.storeId > :lastId
        """)
    boolean hasNextPageById(@Param("lastId") Long lastId);

    @Query("""
        SELECT COUNT(s) > 0 FROM Store s 
        WHERE s.isActive = true 
        AND (s.createdAt < :lastCreatedAt 
             OR (s.createdAt = :lastCreatedAt AND s.storeId < :lastId))
        """)
    boolean hasNextPageByCreatedAt(@Param("lastCreatedAt") LocalDateTime lastCreatedAt,
                                   @Param("lastId") Long lastId);

    @Query("""
        SELECT COUNT(s) > 0 FROM Store s 
        WHERE s.isActive = true 
        AND s.ratingAverage IS NOT NULL
        AND (s.ratingAverage < :lastRating 
             OR (s.ratingAverage = :lastRating AND s.storeId < :lastId))
        """)
    boolean hasNextPageByRating(@Param("lastRating") Double lastRating,
                                @Param("lastId") Long lastId);

    /**
     * 카테고리별 + Cursor 페이지네이션
     */
    @Query("""
        SELECT DISTINCT s FROM Store s 
        JOIN s.ddipBoxes d 
        WHERE s.isActive = true 
        AND d.isActive = true
        AND LOWER(d.category) LIKE LOWER(CONCAT('%', :category, '%'))
        AND (:cursor IS NULL OR s.storeId > :cursor)
        ORDER BY s.storeId ASC
        """)
    List<Store> findStoresWithCursorByCategory(@Param("category") String category,
                                               @Param("cursor") Long cursor,
                                               org.springframework.data.domain.Pageable pageable);
}