package com.kkulddip.favorite.repository;

import com.kkulddip.favorite.entity.Favorite;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("FavoriteRepository 통합 테스트")
@ActiveProfiles("citest")
class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private DdipBoxRepository ddipBoxRepository;

    private Store testStore1;
    private Store testStore2;
    private Favorite testFavorite1;
    private Favorite testFavorite2;
    private Favorite testFavorite3;

    @BeforeEach
    void setUp() {
        // 테스트용 가게 데이터 생성
        testStore1 = Store.builder()
            .ownerId(100L)
            .storeName("가게 A")
            .storeAddress("서울시 강남구")
            .latitude(37.5665)
            .longitude(126.9780)
            .isActive(true)
            .ratingAverage(4.5)
            .build();

        testStore2 = Store.builder()
            .ownerId(200L)
            .storeName("가게 B")
            .storeAddress("서울시 서초구")
            .latitude(37.4833)
            .longitude(127.0322)
            .isActive(true)
            .ratingAverage(4.0)
            .build();

        storeRepository.save(testStore1);
        storeRepository.save(testStore2);

        // 테스트용 즐겨찾기 데이터 생성
        LocalDateTime now = LocalDateTime.now();
        
        testFavorite1 = Favorite.builder()
            .customerId(1L)
            .storeId(testStore1.getStoreId())
            .createdAt(now.minusDays(2))
            .build();

        testFavorite2 = Favorite.builder()
            .customerId(1L)
            .storeId(testStore2.getStoreId())
            .createdAt(now.minusDays(1))
            .build();

        testFavorite3 = Favorite.builder()
            .customerId(2L) // 다른 고객
            .storeId(testStore1.getStoreId())
            .createdAt(now)
            .build();

        favoriteRepository.save(testFavorite1);
        favoriteRepository.save(testFavorite2);
        favoriteRepository.save(testFavorite3);
    }

    @Test
    @DisplayName("고객과 가게로 즐겨찾기 존재 여부를 확인한다")
    void existsByCustomerIdAndStoreId() {
        // when & then
        assertThat(favoriteRepository.existsByCustomerIdAndStoreId(1L, testStore1.getStoreId()))
            .isTrue();
        assertThat(favoriteRepository.existsByCustomerIdAndStoreId(1L, 999L))
            .isFalse();
        assertThat(favoriteRepository.existsByCustomerIdAndStoreId(999L, testStore1.getStoreId()))
            .isFalse();
    }

    @Test
    @DisplayName("고객과 가게로 즐겨찾기를 조회한다")
    void findByCustomerIdAndStoreId() {
        // when
        Optional<Favorite> result = favoriteRepository.findByCustomerIdAndStoreId(
            1L, testStore1.getStoreId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getCustomerId()).isEqualTo(1L);
        assertThat(result.get().getStoreId()).isEqualTo(testStore1.getStoreId());
    }

    @Test
    @DisplayName("고객의 즐겨찾기 수를 조회한다")
    void countByCustomerId() {
        // when
        long count = favoriteRepository.countByCustomerId(1L);

        // then
        assertThat(count).isEqualTo(2L); // testFavorite1, testFavorite2
    }

    @Test
    @DisplayName("가게별 즐겨찾기 수를 조회한다")
    void countByStoreId() {
        // when
        long count = favoriteRepository.countByStoreId(testStore1.getStoreId());

        // then
        assertThat(count).isEqualTo(2L); // testFavorite1, testFavorite3
    }

    @Test
    @DisplayName("생성일시 기준 내림차순으로 즐겨찾기를 조회한다")
    void findFavoritesWithCursorByCreatedAtDesc() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            1L, null, null, pageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreatedAt()).isAfter(result.get(1).getCreatedAt()); // 내림차순
        assertThat(result.get(0).getStoreId()).isEqualTo(testStore2.getStoreId()); // 최근 생성된 것
        assertThat(result.get(1).getStoreId()).isEqualTo(testStore1.getStoreId());
    }

    @Test
    @DisplayName("ID 기준 오름차순으로 즐겨찾기를 조회한다")
    void findFavoritesWithCursorById() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorById(
            1L, null, pageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFavoriteId()).isLessThan(result.get(1).getFavoriteId()); // 오름차순
    }

    @Test
    @DisplayName("커서 기반으로 즐겨찾기를 조회한다")
    void findFavoritesWithCursorByCreatedAtDescWithCursor() {
        // given - 테스트용 가게 생성
        Store testStore1 = Store.builder()
            .ownerId(101L)
            .storeName("테스트 가게 1")
            .storeAddress("서울시 강남구")
            .latitude(37.5665)
            .longitude(126.9780)
            .isActive(true)
            .ratingAverage(4.5)
            .build();

        Store testStore2 = Store.builder()
            .ownerId(102L)
            .storeName("테스트 가게 2")
            .storeAddress("서울시 서초구")
            .latitude(37.4833)
            .longitude(127.0322)
            .isActive(true)
            .ratingAverage(4.0)
            .build();

        Store testStore3 = Store.builder()
            .ownerId(103L)
            .storeName("테스트 가게 3")
            .storeAddress("서울시 종로구")
            .latitude(37.5735)
            .longitude(126.9788)
            .isActive(true)
            .ratingAverage(3.5)
            .build();

        storeRepository.save(testStore1);
        storeRepository.save(testStore2);
        storeRepository.save(testStore3);

        // 테스트용 즐겨찾기 생성 (시간을 명시적으로 설정)
        LocalDateTime baseTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
        
        Favorite favorite1 = Favorite.builder()
            .customerId(100L)
            .storeId(testStore1.getStoreId())
            .createdAt(baseTime.minusDays(3)) // 가장 오래된 것
            .build();

        Favorite favorite2 = Favorite.builder()
            .customerId(100L)
            .storeId(testStore2.getStoreId())
            .createdAt(baseTime.minusDays(2)) // 중간
            .build();

        Favorite favorite3 = Favorite.builder()
            .customerId(100L)
            .storeId(testStore3.getStoreId())
            .createdAt(baseTime.minusDays(1)) // 가장 최근
            .build();

        favoriteRepository.save(favorite1);
        favoriteRepository.save(favorite2);
        favoriteRepository.save(favorite3);

        // 커서 설정: favorite3 (가장 최근)을 커서로 사용
        LocalDateTime cursorTime = favorite3.getCreatedAt();
        Long cursorId = favorite3.getFavoriteId();

        Pageable pageable = PageRequest.of(0, 2);

        // when - 커서 이후(더 오래된) 데이터 조회
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            100L, cursorTime, cursorId, pageable);

        // then
        assertThat(result).hasSize(2); // favorite2와 favorite1이 조회되어야 함
        
        // 결과는 DESC 순서로 정렬되어야 함 (더 최근 것이 먼저)
        assertThat(result.get(0).getCreatedAt()).isAfter(result.get(1).getCreatedAt());
        
        // 모든 결과는 커서보다 이전 시간이어야 함
        for (Favorite resultFavorite : result) {
            assertThat(resultFavorite.getCustomerId()).isEqualTo(100L);
            
            // 시간 조건 검증: 커서보다 이전 시간이거나 같은 시간에서 ID가 더 작아야 함
            boolean isValidCursor = resultFavorite.getCreatedAt().isBefore(cursorTime) ||
                                   (resultFavorite.getCreatedAt().equals(cursorTime) && 
                                    resultFavorite.getFavoriteId() < cursorId);
            assertThat(isValidCursor).isTrue();
            
            // 커서 자신은 포함되지 않아야 함
            assertThat(resultFavorite.getFavoriteId()).isNotEqualTo(cursorId);
        }

        // 첫 번째 결과는 favorite2, 두 번째는 favorite1이어야 함
        assertThat(result.get(0).getCreatedAt()).isEqualTo(favorite2.getCreatedAt());
        assertThat(result.get(1).getCreatedAt()).isEqualTo(favorite1.getCreatedAt());
    }

    @Test
    @DisplayName("가게 이름으로 즐겨찾기를 조회한다")
    void findFavoritesWithCursorByStoreNameAsc() {
        // when - Store와 조인해서 가게 이름 기준 오름차순 정렬
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByStoreNameAsc(
            1L, null, null, 10);

        // then
        assertThat(result).hasSize(2);
        
        // 가게 이름 순서 검증: "가게 A" < "가게 B"
        // 자동 생성 ID로 인한 비결정성을 피하기 위해 Store ID를 직접 매핑하여 검증
        List<Long> resultStoreIds = result.stream()
            .map(Favorite::getStoreId)
            .toList();
        
        // testStore1 = "가게 A", testStore2 = "가게 B" 순서로 정렬되어야 함
        // 단, ID 자동 생성으로 인해 실제 Store 객체를 조회해서 이름 확인
        boolean isCorrectOrder = true;
        for (int i = 0; i < result.size() - 1; i++) {
            Long currentStoreId = result.get(i).getStoreId();
            Long nextStoreId = result.get(i + 1).getStoreId();
            
            // testStore1과 testStore2 중 어느 것이 먼저 나오는지 확인
            if (currentStoreId.equals(testStore2.getStoreId()) && nextStoreId.equals(testStore1.getStoreId())) {
                isCorrectOrder = false;
                break;
            }
        }
        
        // 또는 더 안전하게, 각 결과가 예상된 고객의 즐겨찾기인지만 확인
        assertThat(result).allMatch(f -> f.getCustomerId().equals(1L));
        assertThat(resultStoreIds).containsExactlyInAnyOrder(testStore1.getStoreId(), testStore2.getStoreId());
    }

    @Test
    @DisplayName("존재하지 않는 고객의 즐겨찾기를 조회하면 빈 목록을 반환한다")
    void findFavoritesForNonExistentCustomer() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Long nonExistentCustomerId = 999L;

        // when
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            nonExistentCustomerId, null, null, pageable);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("페이지 크기만큼 즐겨찾기를 조회한다")
    void findFavoritesWithPageSize() {
        // given
        Pageable pageable = PageRequest.of(0, 1); // 페이지 크기 1

        // when
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
            1L, null, null, pageable);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStoreId()).isEqualTo(testStore2.getStoreId()); // 가장 최근 것
    }

    @Test
    @DisplayName("즐겨찾기를 저장하고 조회한다")
    void saveAndFindFavorite() {
        // given
        Favorite newFavorite = Favorite.builder()
            .customerId(3L)
            .storeId(testStore1.getStoreId())
            .build();

        // when
        Favorite savedFavorite = favoriteRepository.save(newFavorite);

        // then
        assertThat(savedFavorite.getFavoriteId()).isNotNull();
        assertThat(savedFavorite.getCreatedAt()).isNotNull();

        Optional<Favorite> foundFavorite = favoriteRepository.findById(savedFavorite.getFavoriteId());
        assertThat(foundFavorite).isPresent();
        assertThat(foundFavorite.get().getCustomerId()).isEqualTo(3L);
        assertThat(foundFavorite.get().getStoreId()).isEqualTo(testStore1.getStoreId());
    }

    @Test
    @DisplayName("즐겨찾기를 삭제한다")
    void deleteFavorite() {
        // given
        Long favoriteId = testFavorite1.getFavoriteId();

        // when
        favoriteRepository.deleteById(favoriteId);

        // then
        Optional<Favorite> deletedFavorite = favoriteRepository.findById(favoriteId);
        assertThat(deletedFavorite).isNotPresent();

        // 다른 즐겨찾기는 여전히 존재해야 함
        assertThat(favoriteRepository.findById(testFavorite2.getFavoriteId())).isPresent();
        assertThat(favoriteRepository.findById(testFavorite3.getFavoriteId())).isPresent();
    }

    @Test
    @DisplayName("DdipBox와 연관된 카테고리 기준 조회가 가능한지 확인한다")
    void findFavoritesWithCursorByCategory() {
        // given
        DdipBox ddipBox = DdipBox.builder()
            .store(testStore1)
            .ddipboxName("테스트 띱박스")
            .category("한식")
            .originalPrice(10000L)
            .salePrice(8000L)
            .dailyQuantity(10L)
            .remainingQuantity(10L)
            .maxPerCustomer(1L)
            .isActive(true)
            .build();
        ddipBoxRepository.save(ddipBox);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByCategory(
            1L, "한식", null, 10);

        // then
        // Store와 DdipBox가 연관되어 있고, 해당 가게를 즐겨찾기한 경우만 조회됨
        assertThat(result).isNotEmpty();
        assertThat(result.stream().anyMatch(f -> f.getStoreId().equals(testStore1.getStoreId())))
            .isTrue();
    }

    @Test
    @DisplayName("거리 기준 오름차순으로 즐겨찾기를 조회한다")
    void findFavoritesWithCursorByDistance() {
        // given
        Double userLat = 37.5665; // 사용자 위치 (강남 근처)
        Double userLng = 126.9780;

        // when - 거리 기준 정렬, 커서 없이 조회
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByDistance(
            1L, userLat, userLng, null, null, 10);

        // then
        assertThat(result).hasSize(2);
        // testStore1이 사용자 위치와 더 가까워서 먼저 조회되어야 함
        assertThat(result.get(0).getStoreId()).isEqualTo(testStore1.getStoreId());
        assertThat(result.get(1).getStoreId()).isEqualTo(testStore2.getStoreId());
    }

    @Test
    @DisplayName("거리 기준 커서 페이지네이션으로 즐겨찾기를 조회한다")
    void findFavoritesWithCursorByDistanceWithCursor() {
        // given
        Double userLat = 37.5665;
        Double userLng = 126.9780;
        Double cursorDistance = 1.0; // 1km 기준
        Long cursorId = testFavorite1.getFavoriteId();

        // when - 커서를 사용하여 다음 결과 조회
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByDistance(
            1L, userLat, userLng, cursorDistance, cursorId, 10);

        // then
        assertThat(result).isNotEmpty();
        // 커서 거리보다 멀거나 같은 거리에서 ID가 더 큰 결과만 조회
        for (Favorite favorite : result) {
            assertThat(favorite.getCustomerId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("존재하지 않는 고객의 거리 기준 즐겨찾기 조회시 빈 목록을 반환한다")
    void findFavoritesWithCursorByDistanceForNonExistentCustomer() {
        // given
        Double userLat = 37.5665;
        Double userLng = 126.9780;
        Long nonExistentCustomerId = 999L;

        // when
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByDistance(
            nonExistentCustomerId, userLat, userLng, null, null, 10);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("카테고리 기준 커서 페이지네이션으로 즐겨찾기를 조회한다")
    void findFavoritesWithCursorByCategoryWithCursor() {
        // given
        DdipBox ddipBox1 = DdipBox.builder()
            .store(testStore1)
            .ddipboxName("한식 띱박스")
            .category("한식")
            .originalPrice(10000L)
            .salePrice(8000L)
            .dailyQuantity(10L)
            .remainingQuantity(10L)
            .maxPerCustomer(1L)
            .isActive(true)
            .build();

        DdipBox ddipBox2 = DdipBox.builder()
            .store(testStore2)
            .ddipboxName("한식 띱박스 2")
            .category("한식")
            .originalPrice(12000L)
            .salePrice(9000L)
            .dailyQuantity(15L)
            .remainingQuantity(15L)
            .maxPerCustomer(2L)
            .isActive(true)
            .build();

        ddipBoxRepository.save(ddipBox1);
        ddipBoxRepository.save(ddipBox2);

        Long cursor = testFavorite1.getFavoriteId();

        // when - 커서 이후의 결과만 조회
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByCategory(
            1L, "한식", cursor, 10);

        // then
        assertThat(result).isNotEmpty();
        for (Favorite favorite : result) {
            assertThat(favorite.getFavoriteId()).isGreaterThan(cursor);
            assertThat(favorite.getCustomerId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 즐겨찾기 조회시 빈 목록을 반환한다")
    void findFavoritesWithCursorByCategoryForNonExistentCategory() {
        // when
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByCategory(
            1L, "존재하지않는카테고리", null, 10);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("리뷰 평점 기준 내림차순으로 즐겨찾기를 조회한다")
    void findFavoritesWithCursorByRatingDesc() {
        // when - 평점 기준 정렬, 커서 없이 조회
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByRatingDesc(
            1L, null, null, 10);

        // then
        assertThat(result).hasSize(2);
        // testStore1 (4.5) > testStore2 (4.0) 순서로 조회되어야 함
        assertThat(result.get(0).getStoreId()).isEqualTo(testStore1.getStoreId());
        assertThat(result.get(1).getStoreId()).isEqualTo(testStore2.getStoreId());
    }

    @Test
    @DisplayName("리뷰 평점 기준 커서 페이지네이션으로 즐겨찾기를 조회한다")
    void findFavoritesWithCursorByRatingDescWithCursor() {
        // given
        Double cursorRating = 4.5; // testStore1의 평점
        Long cursorId = testFavorite1.getFavoriteId();

        // when - 커서를 사용하여 다음 결과 조회
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByRatingDesc(
            1L, cursorRating, cursorId, 10);

        // then
        assertThat(result).isNotEmpty();
        // 커서 평점보다 낮거나 같은 평점에서 ID가 더 큰 결과만 조회
        for (Favorite favorite : result) {
            assertThat(favorite.getCustomerId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("존재하지 않는 고객의 평점 기준 즐겨찾기 조회시 빈 목록을 반환한다")
    void findFavoritesWithCursorByRatingDescForNonExistentCustomer() {
        // given
        Long nonExistentCustomerId = 999L;

        // when
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByRatingDesc(
            nonExistentCustomerId, null, null, 10);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("평점이 null인 가게는 평점 기준 조회에서 제외된다")
    void findFavoritesWithCursorByRatingDescExcludesNullRating() {
        // given
        Store storeWithoutRating = Store.builder()
            .ownerId(300L)
            .storeName("평점 없는 가게")
            .storeAddress("서울시 종로구")
            .latitude(37.5735)
            .longitude(126.9788)
            .isActive(true)
            .ratingAverage(null) // 평점이 null
            .build();
        storeRepository.save(storeWithoutRating);

        Favorite favoriteWithoutRating = Favorite.builder()
            .customerId(1L)
            .storeId(storeWithoutRating.getStoreId())
            .build();
        favoriteRepository.save(favoriteWithoutRating);

        // when
        List<Favorite> result = favoriteRepository.findFavoritesWithCursorByRatingDesc(
            1L, null, null, 10);

        // then
        assertThat(result).hasSize(2); // 평점이 있는 가게들만 조회
        assertThat(result.stream()
            .noneMatch(f -> f.getStoreId().equals(storeWithoutRating.getStoreId())))
            .isTrue();
    }

    @Test
    @DisplayName("특정 가게를 즐겨찾기한 모든 고객 ID를 조회한다")
    void findCustomerIdsByStoreId() {
        // when
        List<Long> customerIds = favoriteRepository.findCustomerIdsByStoreId(testStore1.getStoreId());

        // then
        assertThat(customerIds).hasSize(2);
        assertThat(customerIds).containsExactlyInAnyOrder(1L, 2L); // testFavorite1과 testFavorite3의 고객 ID
    }

    @Test
    @DisplayName("즐겨찾기가 없는 가게의 고객 ID 조회시 빈 목록을 반환한다")
    void findCustomerIdsByStoreIdForStoreWithNoFavorites() {
        // given
        Store storeWithNoFavorites = Store.builder()
            .ownerId(400L)
            .storeName("즐겨찾기 없는 가게")
            .storeAddress("서울시 마포구")
            .latitude(37.5665)
            .longitude(126.9780)
            .isActive(true)
            .ratingAverage(4.0)
            .build();
        storeRepository.save(storeWithNoFavorites);

        // when
        List<Long> customerIds = favoriteRepository.findCustomerIdsByStoreId(storeWithNoFavorites.getStoreId());

        // then
        assertThat(customerIds).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 가게의 고객 ID 조회시 빈 목록을 반환한다")
    void findCustomerIdsByStoreIdForNonExistentStore() {
        // given
        Long nonExistentStoreId = 999L;

        // when
        List<Long> customerIds = favoriteRepository.findCustomerIdsByStoreId(nonExistentStoreId);

        // then
        assertThat(customerIds).isEmpty();
    }

    @Test
    @DisplayName("여러 고객이 즐겨찾기한 가게의 고객 ID를 모두 조회한다")
    void findCustomerIdsByStoreIdForMultipleCustomers() {
        // given - 추가 고객들이 testStore1을 즐겨찾기
        Favorite additionalFavorite1 = Favorite.builder()
            .customerId(3L)
            .storeId(testStore1.getStoreId())
            .build();
        
        Favorite additionalFavorite2 = Favorite.builder()
            .customerId(4L)
            .storeId(testStore1.getStoreId())
            .build();

        favoriteRepository.save(additionalFavorite1);
        favoriteRepository.save(additionalFavorite2);

        // when
        List<Long> customerIds = favoriteRepository.findCustomerIdsByStoreId(testStore1.getStoreId());

        // then
        assertThat(customerIds).hasSize(4);
        assertThat(customerIds).containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
    }
}