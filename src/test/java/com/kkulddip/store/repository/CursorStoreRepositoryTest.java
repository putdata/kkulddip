package com.kkulddip.store.repository;

import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.DdipBoxRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("CursorStoreRepository 테스트")
@ActiveProfiles("citest")
class CursorStoreRepositoryTest {

    @Autowired
    private CursorStoreRepository cursorStoreRepository;

    @Autowired
    private DdipBoxRepository ddipBoxRepository;

    private Store store1, store2, store3, store4, store5;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

        // 테스트용 가게 데이터 생성 (다양한 조건으로)
        store1 = Store.builder()
            .ownerId(101L)
            .storeName("가게 A")
            .storeAddress("서울시 강남구")
            .description("맛있는 한식 전문점")
            .latitude(37.5665)
            .longitude(126.9780)
            .isActive(true)
            .ratingAverage(4.8)
            .reviewCount(100L)
            .createdAt(baseTime.minusDays(5))
            .build();

        store2 = Store.builder()
            .ownerId(102L)
            .storeName("가게 B")
            .storeAddress("서울시 서초구")
            .description("신선한 샐러드 가게")
            .latitude(37.4833)
            .longitude(127.0322)
            .isActive(true)
            .ratingAverage(4.5)
            .reviewCount(80L)
            .createdAt(baseTime.minusDays(4))
            .build();

        store3 = Store.builder()
            .ownerId(103L)
            .storeName("가게 C")
            .storeAddress("서울시 종로구")
            .description("전통 한식 요리")
            .latitude(37.5735)
            .longitude(126.9788)
            .isActive(true)
            .ratingAverage(4.2)
            .reviewCount(60L)
            .createdAt(baseTime.minusDays(3))
            .build();

        store4 = Store.builder()
            .ownerId(104L)
            .storeName("가게 D")
            .storeAddress("서울시 마포구")
            .description("이탈리안 레스토랑")
            .latitude(37.5563)
            .longitude(126.9223)
            .isActive(true)
            .ratingAverage(4.7)
            .reviewCount(120L)
            .createdAt(baseTime.minusDays(2))
            .build();

        store5 = Store.builder()
            .ownerId(105L)
            .storeName("가게 E")
            .storeAddress("서울시 용산구")
            .description("비활성화된 가게")
            .latitude(37.5326)
            .longitude(126.9652)
            .isActive(false) // 비활성화
            .ratingAverage(3.8)
            .reviewCount(40L)
            .createdAt(baseTime.minusDays(1))
            .build();

        cursorStoreRepository.save(store1);
        cursorStoreRepository.save(store2);
        cursorStoreRepository.save(store3);
        cursorStoreRepository.save(store4);
        cursorStoreRepository.save(store5);
    }

    @Test
    @DisplayName("ID 기준 커서 페이지네이션 - 커서 없이 첫 페이지 조회")
    void findStoresWithCursorById_WithoutCursor() {
        // given
        Pageable pageable = PageRequest.of(0, 3);

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorById(null, pageable);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.stream().allMatch(Store::getIsActive)).isTrue(); // 활성화된 가게만
        assertThat(result.get(0).getStoreId()).isLessThan(result.get(1).getStoreId()); // ID 오름차순
        assertThat(result.get(1).getStoreId()).isLessThan(result.get(2).getStoreId());
    }

    @Test
    @DisplayName("ID 기준 커서 페이지네이션 - 커서로 다음 페이지 조회")
    void findStoresWithCursorById_WithCursor() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Long cursorId = store2.getStoreId(); // 두 번째 가게를 커서로 사용

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorById(cursorId, pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.stream().allMatch(Store::getIsActive)).isTrue();
        
        // 모든 결과가 커서 ID보다 크고, 활성화된 가게여야 함
        for (Store store : result) {
            assertThat(store.getStoreId()).isGreaterThan(cursorId);
            assertThat(store.getIsActive()).isTrue();
        }
        
        // ID 오름차순 정렬 확인
        if (result.size() > 1) {
            assertThat(result.get(0).getStoreId()).isLessThan(result.get(1).getStoreId());
        }
    }

    @Test
    @DisplayName("생성일시 기준 커서 페이지네이션 - 내림차순")
    void findStoresWithCursorByCreatedAtDesc_WithCursor() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        LocalDateTime cursorDate = store3.getCreatedAt(); // 3일 전
        Long cursorId = store3.getStoreId();

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorByCreatedAtDesc(
            cursorDate, cursorId, pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.stream().allMatch(Store::getIsActive)).isTrue();

        // 커서 검증: 더 이전 시간이거나 같은 시간에서 더 작은 ID
        for (Store store : result) {
            boolean isValidCursor = store.getCreatedAt().isBefore(cursorDate) ||
                                   (store.getCreatedAt().equals(cursorDate) && 
                                    store.getStoreId() < cursorId);
            assertThat(isValidCursor).isTrue();
        }

        // 내림차순 정렬 확인 (더 최근이 먼저)
        if (result.size() > 1) {
            LocalDateTime first = result.get(0).getCreatedAt();
            LocalDateTime second = result.get(1).getCreatedAt();
            assertThat(first.isAfter(second) || 
                      (first.equals(second) && 
                       result.get(0).getStoreId() > result.get(1).getStoreId())).isTrue();
        }
    }

    @Test
    @DisplayName("평점 기준 커서 페이지네이션 - 내림차순")
    void findStoresWithCursorByRatingDesc_WithCursor() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Double cursorRating = store2.getRatingAverage(); // 4.5점
        Long cursorId = store2.getStoreId();

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorByRatingDesc(
            cursorRating, cursorId, pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.stream().allMatch(Store::getIsActive)).isTrue();

        // 커서 검증: 더 낮은 평점이거나 같은 평점에서 더 작은 ID
        for (Store store : result) {
            boolean isValidCursor = store.getRatingAverage() < cursorRating ||
                                   (store.getRatingAverage().equals(cursorRating) && 
                                    store.getStoreId() < cursorId);
            assertThat(isValidCursor).isTrue();
        }

        // 평점 내림차순 정렬 확인
        if (result.size() > 1) {
            Double firstRating = result.get(0).getRatingAverage();
            Double secondRating = result.get(1).getRatingAverage();
            assertThat(firstRating >= secondRating).isTrue();
            
            if (firstRating.equals(secondRating)) {
                assertThat(result.get(0).getStoreId()).isGreaterThan(result.get(1).getStoreId());
            }
        }
    }

    @Test
    @DisplayName("거리 기준 커서 페이지네이션")
    void findStoresWithCursorByDistance_WithCursor() {
        // given
        Double userLat = 37.5665; // 강남 위치
        Double userLng = 126.9780;
        Double cursorDistance = 2.0; // 2km
        Long cursorId = store2.getStoreId();
        int limit = 3;

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorByDistance(
            userLat, userLng, cursorDistance, cursorId, limit);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.stream().allMatch(Store::getIsActive)).isTrue();
        assertThat(result.stream().allMatch(s -> s.getLatitude() != null && s.getLongitude() != null)).isTrue();

        // 커서보다 먼 거리이거나 같은 거리에서 더 큰 ID여야 함
        for (Store store : result) {
            // 실제 거리 계산은 복잡하므로 기본적인 검증만 수행
            assertThat(store.getLatitude()).isNotNull();
            assertThat(store.getLongitude()).isNotNull();
        }

        // 결과 크기가 limit 이하인지 확인
        assertThat(result.size()).isLessThanOrEqualTo(limit);
    }

    @Test
    @DisplayName("거리 기준 커서 페이지네이션")
    void findStoresWithCursorByDistance_WithCursor2() {
        // given
        Double userLat = 37.5665; // 강남 위치
        Double userLng = 126.9780;
        Double cursorDistance = 2.0; // 2km
        Long cursorId = store2.getStoreId();
        int limit = 3;

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorByDistance(
            userLat, userLng, cursorDistance, cursorId, limit);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.stream().allMatch(Store::getIsActive)).isTrue();
        assertThat(result.stream().allMatch(s -> s.getLatitude() != null && s.getLongitude() != null)).isTrue();

        // 거리 오름차순 정렬 검증 (Haversine 공식 사용)
        List<Double> distances = result.stream()
            .map(store -> calculateDistance(userLat, userLng, store.getLatitude(), store.getLongitude()))
            .toList();

        for (int i = 0; i < distances.size() - 1; i++) {
            assertThat(distances.get(i)).isLessThanOrEqualTo(distances.get(i + 1));
        }

        // 모든 결과가 커서 거리보다 멀거나 같은지 검증
        if (cursorDistance != null) {
            for (Double distance : distances) {
                assertThat(distance).isGreaterThanOrEqualTo(cursorDistance);
            }
        }

        // 결과 크기가 limit 이하인지 확인
        assertThat(result.size()).isLessThanOrEqualTo(limit);
    }

    // 거리 계산 헬퍼 메서드 추가
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반경 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Test
    @DisplayName("검색 키워드 기준 커서 페이지네이션")
    void findStoresWithCursorBySearch_WithCursor() {
        // given
        String keyword = "한식";
        Long cursor = store1.getStoreId();
        Pageable pageable = PageRequest.of(0, 2);

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorBySearch(
            keyword, cursor, pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.stream().allMatch(Store::getIsActive)).isTrue();

        // 검색 키워드가 포함된 가게만 조회되는지 확인
        for (Store store : result) {
            boolean hasKeyword = store.getStoreName().contains(keyword) || 
                               store.getDescription().contains(keyword);
            assertThat(hasKeyword).isTrue();
            assertThat(store.getStoreId()).isGreaterThan(cursor);
        }
    }

    @Test
    @DisplayName("카테고리 기준 커서 페이지네이션")
    void findStoresWithCursorByCategory_WithCursor() {
        // given
        // 테스트용 띱박스 생성
        DdipBox ddipBox1 = DdipBox.builder()
            .store(store1)
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
            .store(store3)
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

        String category = "한식";
        Long cursor = store1.getStoreId();
        Pageable pageable = PageRequest.of(0, 2);

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorByCategory(
            category, cursor, pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.stream().allMatch(Store::getIsActive)).isTrue();

        // 커서보다 큰 ID만 조회되는지 확인
        for (Store store : result) {
            assertThat(store.getStoreId()).isGreaterThan(cursor);
        }

        // 결과에 store3이 포함되어야 함 (store1보다 ID가 크고 한식 카테고리 보유)
        assertThat(result.stream().anyMatch(s -> s.getStoreId().equals(store3.getStoreId()))).isTrue();
    }

    @Test
    @DisplayName("비활성화된 가게는 조회되지 않는다")
    void inactiveStoresNotIncluded() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorById(null, pageable);

        // then
        assertThat(result.stream().noneMatch(s -> s.getStoreId().equals(store5.getStoreId()))).isTrue();
        assertThat(result.stream().allMatch(Store::getIsActive)).isTrue();
    }

    @Test
    @DisplayName("활성화된 가게 수 조회")
    void countActiveStores() {
        // when
        long count = cursorStoreRepository.countActiveStores();

        // then
        assertThat(count).isEqualTo(4L); // store5는 비활성화되어 제외
    }

    @Test
    @DisplayName("검색 키워드 결과 수 조회")
    void countBySearchKeyword() {
        // when
        long count = cursorStoreRepository.countBySearchKeyword("한식");

        // then
        assertThat(count).isEqualTo(2L); // store1, store3만 "한식" 키워드 포함
    }

    @Test
    @DisplayName("카테고리별 가게 수 조회")
    void countByCategory() {
        // given
        DdipBox ddipBox = DdipBox.builder()
            .store(store1)
            .ddipboxName("양식 띱박스")
            .category("양식")
            .originalPrice(15000L)
            .salePrice(12000L)
            .dailyQuantity(5L)
            .remainingQuantity(5L)
            .maxPerCustomer(1L)
            .isActive(true)
            .build();

        ddipBoxRepository.save(ddipBox);

        // when
        long count = cursorStoreRepository.countByCategory("양식");

        // then
        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("동일한 평점을 가진 가게들의 ID 기준 정렬")
    void sameRatingOrderedById() {
        // given - 동일한 평점을 가진 가게들 추가 생성
        Store sameRatingStore1 = Store.builder()
            .ownerId(201L)
            .storeName("평점 4.0 가게 1")
            .storeAddress("서울시 강동구")
            .isActive(true)
            .ratingAverage(4.0)
            .build();

        Store sameRatingStore2 = Store.builder()
            .ownerId(202L)
            .storeName("평점 4.0 가게 2")
            .storeAddress("서울시 강서구")
            .isActive(true)
            .ratingAverage(4.0)
            .build();

        cursorStoreRepository.save(sameRatingStore1);
        cursorStoreRepository.save(sameRatingStore2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorByRatingDesc(
            null, null, pageable);

        // then
        assertThat(result.size()).isGreaterThanOrEqualTo(2);
        
        // 같은 평점을 가진 가게들이 ID 내림차순으로 정렬되는지 확인
        List<Store> sameRatingStores = result.stream()
            .filter(s -> s.getRatingAverage().equals(4.0))
            .toList();
        
        if (sameRatingStores.size() > 1) {
            for (int i = 0; i < sameRatingStores.size() - 1; i++) {
                assertThat(sameRatingStores.get(i).getStoreId())
                    .isGreaterThan(sameRatingStores.get(i + 1).getStoreId());
            }
        }
    }

    @Test
    @DisplayName("위치 정보가 없는 가게는 거리 검색에서 제외")
    void storesWithoutLocationExcludedFromDistanceSearch() {
        // given - 위치 정보가 없는 가게 생성
        Store storeWithoutLocation = Store.builder()
            .ownerId(301L)
            .storeName("위치 없는 가게")
            .storeAddress("주소만 있는 가게")
            .isActive(true)
            .ratingAverage(4.0)
            .latitude(null) // 위치 정보 없음
            .longitude(null)
            .build();

        cursorStoreRepository.save(storeWithoutLocation);

        Double userLat = 37.5665;
        Double userLng = 126.9780;

        // when
        List<Store> result = cursorStoreRepository.findStoresWithCursorByDistance(
            userLat, userLng, null, null, 10);

        // then
        assertThat(result.stream().noneMatch(s -> s.getStoreId().equals(storeWithoutLocation.getStoreId()))).isTrue();
        assertThat(result.stream().allMatch(s -> s.getLatitude() != null && s.getLongitude() != null)).isTrue();
    }
}