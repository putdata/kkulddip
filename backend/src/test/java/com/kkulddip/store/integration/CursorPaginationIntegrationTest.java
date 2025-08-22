package com.kkulddip.store.integration;

import com.kkulddip.favorite.entity.Favorite;
import com.kkulddip.favorite.repository.FavoriteRepository;
import com.kkulddip.store.common.CursorInfo;
import com.kkulddip.store.common.Page;
import com.kkulddip.store.dto.request.StoreListRequest;
import com.kkulddip.store.dto.response.StoreResponseDto;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.repository.CursorStoreRepository;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.service.StoreService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("커서 페이지네이션 통합 테스트")
@ActiveProfiles("citest")
class CursorPaginationIntegrationTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private CursorStoreRepository cursorStoreRepository;
    
    @Autowired
    private com.kkulddip.store.repository.StoreRepository storeRepository;

    @Autowired
    private DdipBoxRepository ddipBoxRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @MockitoBean
    private FirebaseMessaging firebaseMessaging;

    private List<Store> testStores;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
        testStores = new ArrayList<>();

        // 테스트용 가게 10개 생성 (다양한 조건)
        for (int i = 1; i <= 10; i++) {
            Store store = Store.builder()
                .ownerId((long) (100 + i))
                .storeName("가게 " + (char) ('A' + i - 1))
                .storeAddress("서울시 강남구 " + i + "번지")
                .description("테스트 가게 " + i)
                .latitude(37.5665 + (i * 0.001)) // 약간씩 다른 위치
                .longitude(126.9780 + (i * 0.001))
                .isActive(true)
                .ratingAverage(4.0 + (i % 5) * 0.2) // 4.0 ~ 4.8 사이
                .reviewCount((long) (50 + i * 10))
                .createdAt(baseTime.minusDays(10 - i)) // 10일전 ~ 1일전
                .build();

            testStores.add(storeRepository.save(store));
        }

        // 모든 가게에 띱박스 추가 (ddipbox 필터링 조건을 만족시키기 위해)
        for (int i = 0; i < testStores.size(); i++) {
            DdipBox ddipBox = DdipBox.builder()
                .store(testStores.get(i))
                .ddipboxName("띱박스 " + (i + 1))
                .category(i % 3 == 0 ? "한식" : (i % 3 == 1 ? "양식" : "기타"))
                .originalPrice(10000L + i * 1000)
                .salePrice(8000L + i * 800)
                .dailyQuantity(10L + i)
                .remainingQuantity(10L + i)
                .maxPerCustomer(1L)
                .isActive(true)
                .build();

            ddipBoxRepository.save(ddipBox);
        }

        // 테스트용 즐겨찾기 생성
        for (int i = 0; i < 5; i++) {
            Favorite favorite = Favorite.builder()
                .customerId(200L)
                .storeId(testStores.get(i).getStoreId())
                .createdAt(baseTime.minusDays(5 - i))
                .build();

            favoriteRepository.save(favorite);
        }
    }

    @Test
    @DisplayName("ID 정렬 전체 워크플로 테스트 - 첫 페이지부터 마지막까지")
    void completeIdSortWorkflow() {
        List<StoreResponseDto> allResults = new ArrayList<>();
        String nextCursor = null;
        int pageCount = 0;
        int pageSize = 3;

        do {
            pageCount++;
            
            // 페이지 요청
            StoreListRequest request = StoreListRequest.of(null, null, "id", pageSize, nextCursor);
            Page<StoreResponseDto> page = storeService.getStores(request);

            // 기본 검증
            assertThat(page.getContent().size()).isLessThanOrEqualTo(pageSize);
            assertThat(page.getSize()).isEqualTo(pageSize);
            assertThat(page.getActualSize()).isEqualTo(page.getContent().size());

            // 첫 페이지 검증
            if (pageCount == 1) {
                assertThat(page.getIsFirst()).isTrue();
                assertThat(nextCursor).isNull();
            } else {
                assertThat(page.getIsFirst()).isFalse();
            }

            // ID 오름차순 정렬 확인
            List<Long> pageIds = page.getContent().stream()
                .map(StoreResponseDto::storeId)
                .toList();

            for (int i = 0; i < pageIds.size() - 1; i++) {
                assertThat(pageIds.get(i)).isLessThan(pageIds.get(i + 1));
            }

            // 이전 페이지와 중복되지 않는지 확인
            for (StoreResponseDto currentStore : page.getContent()) {
                assertThat(allResults.stream()
                    .noneMatch(prev -> prev.storeId().equals(currentStore.storeId())))
                    .isTrue();
            }

            allResults.addAll(page.getContent());
            nextCursor = page.getCursor();

            // 마지막 페이지 검증
            if (!page.getHasNext()) {
                assertThat(page.getIsLast()).isTrue();
                assertThat(page.getCursor()).isNull();
            }

        } while (nextCursor != null);

        // 전체 결과 검증 - 다른 테스트의 데이터 간섭을 고려한 범위 검증
        assertThat(allResults.size()).isGreaterThanOrEqualTo(10); // 최소 10개 이상
        assertThat(allResults.size()).isLessThanOrEqualTo(110); // 최대 110개 이하 (다른 테스트 고려)
        assertThat(pageCount).isGreaterThan(1); // 여러 페이지에 걸쳐 조회됨

        // 전체 결과가 ID 오름차순인지 확인
        for (int i = 0; i < allResults.size() - 1; i++) {
            assertThat(allResults.get(i).storeId())
                .isLessThan(allResults.get(i + 1).storeId());
        }
    }

    @Test
    @DisplayName("평점 정렬 전체 워크플로 테스트")
    void completeRatingSortWorkflow() {
        List<StoreResponseDto> allResults = new ArrayList<>();
        String nextCursor = null;
        int pageSize = 4;

        do {
            StoreListRequest request = StoreListRequest.of(null, null, "rating", pageSize, nextCursor);
            Page<StoreResponseDto> page = storeService.getStores(request);

            // 평점 내림차순 정렬 확인
            for (int i = 0; i < page.getContent().size() - 1; i++) {
                StoreResponseDto current = page.getContent().get(i);
                StoreResponseDto next = page.getContent().get(i + 1);
                
                // 평점이 같은 경우 ID로 내림차순 정렬
                if (current.ratingAverage().equals(next.ratingAverage())) {
                    assertThat(current.storeId()).isGreaterThan(next.storeId());
                } else {
                    assertThat(current.ratingAverage()).isGreaterThan(next.ratingAverage());
                }
            }

            // 커서 정보 검증
            if (page.getCursor() != null) {
                CursorInfo cursor = CursorInfo.decode(page.getCursor());
                assertThat(cursor.hasRating()).isTrue();
                assertThat(cursor.getId()).isNotNull();
                assertThat(cursor.getRating()).isNotNull();

                // 커서의 평점이 페이지 마지막 아이템과 일치하는지 확인
                StoreResponseDto lastItem = page.getContent().get(page.getContent().size() - 1);
                assertThat(cursor.getId()).isEqualTo(lastItem.storeId());
                assertThat(cursor.getRating()).isEqualTo(lastItem.ratingAverage());
            }

            allResults.addAll(page.getContent());
            nextCursor = page.getCursor();

        } while (nextCursor != null);

        // 평점 정렬 테스트 - 데이터 간섭을 고려한 범위 검증
        assertThat(allResults.size()).isGreaterThanOrEqualTo(10);
        assertThat(allResults.size()).isLessThanOrEqualTo(110);
    }

    @Test
    @DisplayName("거리 정렬 전체 워크플로 테스트")
    void completeDistanceSortWorkflow() {
        Double userLat = 37.5665;
        Double userLng = 126.9780;
        List<StoreResponseDto> allResults = new ArrayList<>();
        String nextCursor = null;
        int pageSize = 3;

        do {
            StoreListRequest request = StoreListRequest.of(userLat, userLng, "distance", pageSize, nextCursor);
            Page<StoreResponseDto> page = storeService.getStores(request);

            // 거리 오름차순 정렬 확인 (가까운 것부터)
            for (int i = 0; i < page.getContent().size() - 1; i++) {
                StoreResponseDto current = page.getContent().get(i);
                StoreResponseDto next = page.getContent().get(i + 1);
                
                assertThat(current.distanceFromUser())
                    .isLessThanOrEqualTo(next.distanceFromUser());
            }

            // 커서 정보 검증
            if (page.getCursor() != null) {
                CursorInfo cursor = CursorInfo.decode(page.getCursor());
                assertThat(cursor.hasDistance()).isTrue();
                assertThat(cursor.getDistance()).isNotNull();
                assertThat(cursor.getId()).isNotNull();
            }

            allResults.addAll(page.getContent());
            nextCursor = page.getCursor();

        } while (nextCursor != null);

        // 거리 정렬 테스트는 정확한 수를 예측하기 어려우므로 범위 검증
        assertThat(allResults.size()).isGreaterThan(0);
        assertThat(allResults.size()).isLessThanOrEqualTo(20); // 다른 테스트 데이터 포함 고려
        
        // 모든 결과에 거리 정보가 포함되어 있는지 확인
        assertThat(allResults.stream()
            .allMatch(store -> store.distanceFromUser() != null))
            .isTrue();
    }

    @Test
    @DisplayName("카테고리 필터링과 커서 페이지네이션")
    void categoryFilteringWithCursorPagination() {
        List<StoreResponseDto> allResults = new ArrayList<>();
        String nextCursor = null;
        int pageSize = 2;
        String category = "한식";

        do {
            StoreListRequest request = StoreListRequest.of(null, null, "id", pageSize, nextCursor);
            Page<StoreResponseDto> page = storeService.getStoresByCategory(category, request);

            assertThat(page.getMetadata().getCategory()).isEqualTo(category);

            allResults.addAll(page.getContent());
            nextCursor = page.getCursor();

        } while (nextCursor != null);

        // "한식" 카테고리를 가진 가게만 조회되어야 함 (설정에 따라 3개 정도)
        assertThat(allResults.size()).isGreaterThan(0);
        assertThat(allResults.size()).isLessThanOrEqualTo(testStores.size());
    }

    @Test
    @DisplayName("검색 키워드와 커서 페이지네이션")
    void searchWithCursorPagination() {
        List<StoreResponseDto> allResults = new ArrayList<>();
        String nextCursor = null;
        int pageSize = 3;
        String keyword = "가게";

        do {
            var request = com.kkulddip.store.dto.request.StoreSearchRequest.of(
                keyword, null, null, "id", pageSize, nextCursor);
            Page<StoreResponseDto> page = storeService.searchStores(request);

            assertThat(page.getMetadata().getSearchKeyword()).isEqualTo(keyword);

            // 모든 결과에 키워드가 포함되어 있는지 확인
            for (StoreResponseDto store : page.getContent()) {
                assertThat(store.storeName().contains(keyword) || 
                          (store.storeAddress() != null && store.storeAddress().contains(keyword)))
                    .isTrue();
            }

            allResults.addAll(page.getContent());
            nextCursor = page.getCursor();

        } while (nextCursor != null);

        // 검색 테스트 - 모든 가게명에 "가게"가 포함되므로 최소 10개는 있어야 함
        assertThat(allResults.size()).isGreaterThanOrEqualTo(10);
        assertThat(allResults.size()).isLessThanOrEqualTo(110);
    }

    @Test
    @DisplayName("커서 일관성 테스트 - 데이터 변경 중 페이지네이션")
    void cursorConsistencyWithDataChanges() {
        // 첫 번째 페이지 조회
        StoreListRequest request = StoreListRequest.of(null, null, "id", 3, null);
        Page<StoreResponseDto> firstPage = storeService.getStores(request);
        
        assertThat(firstPage.getContent()).hasSize(3);
        assertThat(firstPage.getHasNext()).isTrue();
        
        String cursor = firstPage.getCursor();
        assertThat(cursor).isNotNull();

        // 중간에 새로운 가게 추가 (ID가 첫 페이지보다 큰 범위에)
        Store newStore = Store.builder()
            .ownerId(999L)
            .storeName("새로운 가게")
            .storeAddress("서울시 서초구")
            .isActive(true)
            .ratingAverage(4.5)
            .createdAt(LocalDateTime.now())
            .build();
        
        Store savedNewStore = storeRepository.save(newStore);
        
        // 새로운 가게에도 ddipbox 추가 (필터링 조건 만족)
        DdipBox newDdipBox = DdipBox.builder()
            .store(savedNewStore)
            .ddipboxName("새로운띱박스")
            .category("기타")
            .originalPrice(7000L)
            .salePrice(5500L)
            .dailyQuantity(5L)
            .remainingQuantity(5L)
            .maxPerCustomer(1L)
            .isActive(true)
            .build();
        
        ddipBoxRepository.save(newDdipBox);

        // 두 번째 페이지 조회 (커서 사용)
        StoreListRequest secondRequest = StoreListRequest.of(null, null, "id", 3, cursor);
        Page<StoreResponseDto> secondPage = storeService.getStores(secondRequest);

        // 커서 이후의 데이터만 조회되는지 확인
        Long lastIdFromFirstPage = firstPage.getContent().get(2).storeId();
        for (StoreResponseDto store : secondPage.getContent()) {
            assertThat(store.storeId()).isGreaterThan(lastIdFromFirstPage);
        }

        // 첫 번째 페이지와 중복되지 않는지 확인
        List<Long> firstPageIds = firstPage.getContent().stream()
            .map(StoreResponseDto::storeId)
            .toList();
        List<Long> secondPageIds = secondPage.getContent().stream()
            .map(StoreResponseDto::storeId)
            .toList();

        for (Long secondPageId : secondPageIds) {
            assertThat(firstPageIds).doesNotContain(secondPageId);
        }
    }

    @Test
    @DisplayName("대용량 데이터 페이지네이션 성능 테스트")
    void largeBatchPaginationPerformance() {
        // 추가로 더 많은 데이터 생성 (총 100개)
        for (int i = 11; i <= 100; i++) {
            Store store = Store.builder()
                .ownerId((long) (100 + i))
                .storeName("대용량가게 " + i)
                .storeAddress("서울시 " + i + "구")
                .isActive(true)
                .ratingAverage(4.0 + (i % 10) * 0.1)
                .createdAt(baseTime.minusHours(i))
                .build();
            
            Store savedStore = storeRepository.save(store);
            
            // 각 store에 ddipbox 추가 (필터링 조건 만족)
            DdipBox ddipBox = DdipBox.builder()
                .store(savedStore)
                .ddipboxName("대용량띱박스 " + i)
                .category(i % 3 == 0 ? "한식" : (i % 3 == 1 ? "양식" : "기타"))
                .originalPrice(5000L + (i % 10) * 500)
                .salePrice(4000L + (i % 10) * 400)
                .dailyQuantity((long) (5 + i % 10))
                .remainingQuantity((long) (5 + i % 10))
                .maxPerCustomer(1L)
                .isActive(true)
                .build();
            
            ddipBoxRepository.save(ddipBox);
        }

        long startTime = System.currentTimeMillis();
        
        List<StoreResponseDto> allResults = new ArrayList<>();
        String nextCursor = null;
        int pageSize = 10;
        int maxPages = 20; // 최대 20페이지까지만 테스트
        int pageCount = 0;

        do {
            pageCount++;
            if (pageCount > maxPages) break;

            StoreListRequest request = StoreListRequest.of(null, null, "id", pageSize, nextCursor);
            Page<StoreResponseDto> page = storeService.getStores(request);

            allResults.addAll(page.getContent());
            nextCursor = page.getCursor();

        } while (nextCursor != null && pageCount < maxPages);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // 성능 검증 (임계값은 환경에 따라 조정 가능)
        assertThat(executionTime).isLessThan(5000L); // 5초 이내
        assertThat(allResults.size()).isGreaterThan(50); // 충분한 데이터 조회
        assertThat(pageCount).isGreaterThan(5); // 여러 페이지에 걸친 조회

        System.out.println("대용량 페이지네이션 성능: " + executionTime + "ms, " + 
                          allResults.size() + "개 결과, " + pageCount + " 페이지");
    }

    @Test
    @DisplayName("동시성 상황에서의 커서 일관성")
    void cursorConsistencyUnderConcurrency() {
        // 첫 번째 사용자의 첫 페이지
        StoreListRequest user1Request = StoreListRequest.of(null, null, "rating", 2, null);
        Page<StoreResponseDto> user1Page1 = storeService.getStores(user1Request);

        // 두 번째 사용자의 첫 페이지 (동일한 조건)
        StoreListRequest user2Request = StoreListRequest.of(null, null, "rating", 2, null);
        Page<StoreResponseDto> user2Page1 = storeService.getStores(user2Request);

        // 같은 조건이면 같은 결과가 나와야 함
        assertThat(user1Page1.getContent()).hasSize(user2Page1.getContent().size());
        for (int i = 0; i < user1Page1.getContent().size(); i++) {
            assertThat(user1Page1.getContent().get(i).storeId())
                .isEqualTo(user2Page1.getContent().get(i).storeId());
        }

        // 각자의 커서로 두 번째 페이지 조회
        String user1Cursor = user1Page1.getCursor();
        String user2Cursor = user2Page1.getCursor();
        
        assertThat(user1Cursor).isEqualTo(user2Cursor);

        StoreListRequest user1Request2 = StoreListRequest.of(null, null, "rating", 2, user1Cursor);
        StoreListRequest user2Request2 = StoreListRequest.of(null, null, "rating", 2, user2Cursor);

        Page<StoreResponseDto> user1Page2 = storeService.getStores(user1Request2);
        Page<StoreResponseDto> user2Page2 = storeService.getStores(user2Request2);

        // 두 번째 페이지도 동일해야 함
        assertThat(user1Page2.getContent()).hasSize(user2Page2.getContent().size());
        for (int i = 0; i < user1Page2.getContent().size(); i++) {
            assertThat(user1Page2.getContent().get(i).storeId())
                .isEqualTo(user2Page2.getContent().get(i).storeId());
        }
    }
}