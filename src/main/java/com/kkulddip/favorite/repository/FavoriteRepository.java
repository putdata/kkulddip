package com.kkulddip.favorite.repository;

import com.kkulddip.favorite.entity.Favorite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 즐겨찾기 Repository
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 고객의 특정 가게 즐겨찾기 존재 여부 확인
     */
    @Query("""
        SELECT COUNT(f) > 0 FROM Favorite f 
        WHERE f.customerId = :customerId 
        AND f.storeId = :storeId
        """)
    boolean existsByCustomerIdAndStoreId(
        @Param("customerId") Long customerId,
        @Param("storeId") Long storeId
    );

    /**
     * 고객의 특정 가게 즐겨찾기 조회
     */
    @Query("""
        SELECT f FROM Favorite f 
        WHERE f.customerId = :customerId 
        AND f.storeId = :storeId
        """)
    Optional<Favorite> findByCustomerIdAndStoreId(
        @Param("customerId") Long customerId,
        @Param("storeId") Long storeId
    );

    /**
     * 고객의 즐겨찾기 수 조회
     */
    @Query("""
        SELECT COUNT(f) FROM Favorite f 
        WHERE f.customerId = :customerId
        """)
    long countByCustomerId(@Param("customerId") Long customerId);

    /**
     * 가게별 즐겨찾기 수 조회
     */
    @Query("""
        SELECT COUNT(f) FROM Favorite f 
        WHERE f.storeId = :storeId
        """)
    long countByStoreId(@Param("storeId") Long storeId);

    /**
     * 등록순 내림차순 커서 페이지네이션
     */
    @Query("""
        SELECT f FROM Favorite f 
        WHERE f.customerId = :customerId 
        AND (
            :cursorDate IS NULL OR 
            f.createdAt < :cursorDate OR 
            (f.createdAt = :cursorDate AND f.favoriteId < :cursorId)
        )
        ORDER BY f.createdAt DESC, f.favoriteId DESC
        """)
    List<Favorite> findFavoritesWithCursorByCreatedAtDesc(
        @Param("customerId") Long customerId,
        @Param("cursorDate") LocalDateTime cursorDate,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    /**
     * 즐겨찾기 ID 기준 오름차순 커서 페이지네이션
     */
    @Query("""
        SELECT f FROM Favorite f 
        WHERE f.customerId = :customerId 
        AND (:cursor IS NULL OR f.favoriteId > :cursor)
        ORDER BY f.favoriteId ASC
        """)
    List<Favorite> findFavoritesWithCursorById(
        @Param("customerId") Long customerId,
        @Param("cursor") Long cursor,
        Pageable pageable
    );

    /**
     * Store와 조인하여 이름 기준 오름차순 커서 페이지네이션 (Native Query)
     */
    @Query(value = """
        SELECT f.* FROM favorite f 
        JOIN store s ON f.store_id = s.store_id 
        WHERE f.customer_id = :customerId 
        AND s.is_active = true
        AND (
            :cursorStoreName IS NULL OR 
            s.store_name > :cursorStoreName OR 
            (s.store_name = :cursorStoreName AND f.favorite_id > :cursorId)
        )
        ORDER BY s.store_name ASC, f.favorite_id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Favorite> findFavoritesWithCursorByStoreNameAsc(
        @Param("customerId") Long customerId,
        @Param("cursorStoreName") String cursorStoreName,
        @Param("cursorId") Long cursorId,
        @Param("limit") int limit
    );

    /**
     * Store와 조인하여 거리 기준 오름차순 커서 페이지네이션 (Native Query)
     */
    @Query(value = """
        SELECT f.*, 
               (6371 * acos(cos(radians(:userLat)) 
                          * cos(radians(s.latitude)) 
                          * cos(radians(s.longitude) - radians(:userLng)) 
                          + sin(radians(:userLat)) 
                          * sin(radians(s.latitude)))) AS distance
        FROM favorite f
        JOIN store s ON f.store_id = s.store_id
        WHERE f.customer_id = :customerId 
        AND s.is_active = true
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
             AND f.favorite_id > :cursorId)
        )
        ORDER BY distance ASC, f.favorite_id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Favorite> findFavoritesWithCursorByDistance(
        @Param("customerId") Long customerId,
        @Param("userLat") Double userLatitude,
        @Param("userLng") Double userLongitude,
        @Param("cursorDistance") Double cursorDistance,
        @Param("cursorId") Long cursorId,
        @Param("limit") int limit
    );

    /**
     * Store와 조인하여 카테고리 기준 커서 페이지네이션 (Native Query)
     */
    @Query(value = """
        SELECT f.* FROM favorite f 
        JOIN store s ON f.store_id = s.store_id 
        WHERE f.customer_id = :customerId 
        AND s.is_active = true
        AND s.store_id IN (
            SELECT d.store_id FROM ddip_box d
            WHERE d.is_active = true AND d.category = :category
        )
        AND (:cursor IS NULL OR f.favorite_id > :cursor)
        ORDER BY f.favorite_id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Favorite> findFavoritesWithCursorByCategory(
        @Param("customerId") Long customerId,
        @Param("category") String category,
        @Param("cursor") Long cursor,
        @Param("limit") int limit
    );

    /**
     * Store와 조인하여 리뷰 평점 기준 내림차순 커서 페이지네이션 (Native Query)
     */
    @Query(value = """
        SELECT f.* FROM favorite f 
        JOIN store s ON f.store_id = s.store_id 
        WHERE f.customer_id = :customerId 
        AND s.is_active = true
        AND s.rating_average IS NOT NULL
        AND (
            :cursorRating IS NULL OR 
            s.rating_average < :cursorRating OR 
            (s.rating_average = :cursorRating AND f.favorite_id > :cursorId)
        )
        ORDER BY s.rating_average DESC, f.favorite_id ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Favorite> findFavoritesWithCursorByRatingDesc(
        @Param("customerId") Long customerId,
        @Param("cursorRating") Double cursorRating,
        @Param("cursorId") Long cursorId,
        @Param("limit") int limit
    );
}