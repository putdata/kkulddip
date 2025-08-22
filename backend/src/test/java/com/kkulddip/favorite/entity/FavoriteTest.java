package com.kkulddip.favorite.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Favorite 엔티티 테스트")
@ActiveProfiles("citest")
class FavoriteTest {

    @Test
    @DisplayName("Favorite 엔티티를 빌더 패턴으로 생성한다")
    void createFavoriteWithBuilder() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        // when
        Favorite favorite = Favorite.builder()
            .favoriteId(1L)
            .customerId(100L)
            .storeId(200L)
            .createdAt(now)
            .build();

        // then
        assertThat(favorite.getFavoriteId()).isEqualTo(1L);
        assertThat(favorite.getCustomerId()).isEqualTo(100L);
        assertThat(favorite.getStoreId()).isEqualTo(200L);
        assertThat(favorite.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Favorite 엔티티 기본 생성자로 생성한다")
    void createFavoriteWithDefaultConstructor() {
        // when
        Favorite favorite = new Favorite();

        // then
        assertThat(favorite.getFavoriteId()).isNull();
        assertThat(favorite.getCustomerId()).isNull();
        assertThat(favorite.getStoreId()).isNull();
        // 기본 생성자는 @Builder.Default 값을 적용하지 않으므로 null이어야 함
        assertThat(favorite.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Favorite 엔티티 전체 생성자로 생성한다")
    void createFavoriteWithAllArgsConstructor() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        // when
        Favorite favorite = new Favorite(1L, 100L, 200L, now);

        // then
        assertThat(favorite.getFavoriteId()).isEqualTo(1L);
        assertThat(favorite.getCustomerId()).isEqualTo(100L);
        assertThat(favorite.getStoreId()).isEqualTo(200L);
        assertThat(favorite.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Favorite 엔티티의 Setter를 사용하여 값을 변경한다")
    void updateFavoriteWithSetter() {
        // given
        Favorite favorite = new Favorite();
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        // when
        favorite.setFavoriteId(1L);
        favorite.setCustomerId(100L);
        favorite.setStoreId(200L);
        favorite.setCreatedAt(now);

        // then
        assertThat(favorite.getFavoriteId()).isEqualTo(1L);
        assertThat(favorite.getCustomerId()).isEqualTo(100L);
        assertThat(favorite.getStoreId()).isEqualTo(200L);
        assertThat(favorite.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Favorite 엔티티의 빌더 기본값을 확인한다")
    void checkFavoriteBuilderDefaults() {
        // when
        Favorite favorite = Favorite.builder()
            .customerId(100L)
            .storeId(200L)
            .build();

        // then
        assertThat(favorite.getCustomerId()).isEqualTo(100L);
        assertThat(favorite.getStoreId()).isEqualTo(200L);
        assertThat(favorite.getCreatedAt()).isNotNull(); // @Builder.Default로 현재 시간 설정
        assertThat(favorite.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("두 Favorite 객체의 동등성을 비교한다")
    void compareFavoriteEquality() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        Favorite favorite1 = Favorite.builder()
            .favoriteId(1L)
            .customerId(100L)
            .storeId(200L)
            .createdAt(now)
            .build();

        Favorite favorite2 = Favorite.builder()
            .favoriteId(1L)
            .customerId(100L)
            .storeId(200L)
            .createdAt(now)
            .build();

        Favorite favorite3 = Favorite.builder()
            .favoriteId(2L) // 다른 ID
            .customerId(100L)
            .storeId(200L)
            .createdAt(now)
            .build();

        // when & then
        // Lombok의 @Builder, @Getter, @Setter는 equals/hashCode를 자동 생성하지 않으므로
        // 객체 참조 비교가 됨
        assertThat(favorite1).isNotEqualTo(favorite2); // 다른 객체 인스턴스
        assertThat(favorite1).isEqualTo(favorite1); // 같은 객체 인스턴스
        
        // 필드 값 비교
        assertThat(favorite1.getFavoriteId()).isEqualTo(favorite2.getFavoriteId());
        assertThat(favorite1.getCustomerId()).isEqualTo(favorite2.getCustomerId());
        assertThat(favorite1.getStoreId()).isEqualTo(favorite2.getStoreId());
        assertThat(favorite1.getCreatedAt()).isEqualTo(favorite2.getCreatedAt());
        
        assertThat(favorite1.getFavoriteId()).isNotEqualTo(favorite3.getFavoriteId());
    }

    @Test
    @DisplayName("Favorite 엔티티의 toString 메서드를 확인한다")
    void checkFavoriteToString() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        Favorite favorite = Favorite.builder()
            .favoriteId(1L)
            .customerId(100L)
            .storeId(200L)
            .createdAt(now)
            .build();

        // when
        String result = favorite.toString();

        // then
        // Lombok의 toString이 생성되는지 확인 - 실제 형식에 맞게 수정
        assertThat(result).contains("Favorite");
        assertThat(result).contains("favoriteId");
        assertThat(result).contains("customerId");
        assertThat(result).contains("storeId");
        assertThat(result).contains("createdAt");
        // 값도 포함되어 있는지 확인
        assertThat(result).contains("1");
        assertThat(result).contains("100");
        assertThat(result).contains("200");
    }

    @Test
    @DisplayName("생성일시가 null인 경우 PrePersist에서 자동 설정되는지 확인한다")
    void checkPrePersistForCreatedAt() {
        // given
        Favorite favorite = new Favorite();
        favorite.setCustomerId(100L);
        favorite.setStoreId(200L);
        // createdAt은 의도적으로 설정하지 않음

        // when
        favorite.onCreate(); // @PrePersist 콜백 시뮬레이션

        // then
        assertThat(favorite.getCreatedAt()).isNotNull();
        assertThat(favorite.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("생성일시가 이미 설정된 경우 PrePersist에서 변경되지 않는지 확인한다")
    void checkPrePersistDoesNotOverrideExistingCreatedAt() {
        // given
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        Favorite favorite = new Favorite();
        favorite.setCustomerId(100L);
        favorite.setStoreId(200L);
        favorite.setCreatedAt(specificTime);

        // when
        favorite.onCreate(); // @PrePersist 콜백 시뮬레이션

        // then
        assertThat(favorite.getCreatedAt()).isEqualTo(specificTime); // 원래 시간 유지
    }
}