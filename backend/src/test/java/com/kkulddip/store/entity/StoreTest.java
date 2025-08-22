package com.kkulddip.store.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Store 엔티티 테스트")
@ActiveProfiles("citest")
class StoreTest {

    @Test
    @DisplayName("Store 엔티티를 빌더 패턴으로 생성한다")
    void createStoreWithBuilder() {
        // given & when
        Store store = Store.builder()
                .storeId(1L)
                .ownerId(100L)
                .storeName("테스트 마트")
                .phone("02-1234-5678")
                .description("신선한 식품을 판매하는 마트")
                .operatingHours("09:00-22:00")
                .isActive(true)
                .ratingAverage(4.5)
                .reviewCount(128L)
                .businessNumber("123-45-67890")
                .storeAddress("서울시 강남구 테헤란로 123")
                .storeProfileImage("profile.jpg")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        // then
        assertThat(store.getStoreId()).isEqualTo(1L);
        assertThat(store.getOwnerId()).isEqualTo(100L);
        assertThat(store.getStoreName()).isEqualTo("테스트 마트");
        assertThat(store.getPhone()).isEqualTo("02-1234-5678");
        assertThat(store.getDescription()).isEqualTo("신선한 식품을 판매하는 마트");
        assertThat(store.getOperatingHours()).isEqualTo("09:00-22:00");
        assertThat(store.getIsActive()).isTrue();
        assertThat(store.getRatingAverage()).isEqualTo(4.5);
        assertThat(store.getReviewCount()).isEqualTo(128L);
        assertThat(store.getBusinessNumber()).isEqualTo("123-45-67890");
        assertThat(store.getStoreAddress()).isEqualTo("서울시 강남구 테헤란로 123");
        assertThat(store.getStoreProfileImage()).isEqualTo("profile.jpg");
        assertThat(store.getLatitude()).isEqualTo(37.5665);
        assertThat(store.getLongitude()).isEqualTo(126.9780);
    }

    @Test
    @DisplayName("Store 엔티티의 기본값이 올바르게 설정된다")
    void createStoreWithDefaultValues() {
        // given & when
        Store store = Store.builder()
                .ownerId(100L)
                .storeName("테스트 마트")
                .build();

        // then
        assertThat(store.getIsActive()).isTrue(); // 기본값 true
        assertThat(store.getReviewCount()).isEqualTo(0L); // 기본값 0L
        assertThat(store.getCreatedAt()).isNotNull(); // 기본값 현재 시간
    }

    @Test
    @DisplayName("Store 엔티티와 DdipBox 엔티티 간의 관계를 설정할 수 있다")
    void setRelationshipWithDdipBox() {
        // given
        Store store = Store.builder()
                .storeId(1L)
                .storeName("테스트 마트")
                .build();

        DdipBox ddipBox1 = DdipBox.builder()
                .ddipboxId(1L)
                .ddipboxName("유기농 세트")
                .store(store)
                .build();

        DdipBox ddipBox2 = DdipBox.builder()
                .ddipboxId(2L)
                .ddipboxName("신선한 과일 세트")
                .store(store)
                .build();

        // then - DdipBox가 Store를 참조하는지 확인
        assertThat(ddipBox1.getStore()).isEqualTo(store);
        assertThat(ddipBox2.getStore()).isEqualTo(store);
        assertThat(ddipBox1.getDdipboxName()).isEqualTo("유기농 세트");
        assertThat(ddipBox2.getDdipboxName()).isEqualTo("신선한 과일 세트");
    }

    @Test
    @DisplayName("Store 엔티티의 @PreUpdate 메서드가 updatedAt을 설정한다")
    void preUpdateSetsUpdatedAt() {
        // given
        Store store = Store.builder()
                .storeName("테스트 마트")
                .build();
        
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        store.onUpdate();

        // then
        assertThat(store.getUpdatedAt()).isNotNull();
        assertThat(store.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
    }

    @Test
    @DisplayName("Store 엔티티의 필수 필드만으로 생성할 수 있다")
    void createStoreWithRequiredFieldsOnly() {
        // given & when
        Store store = Store.builder()
                .ownerId(100L)
                .storeName("최소 정보 마트")
                .build();

        // then
        assertThat(store.getOwnerId()).isEqualTo(100L);
        assertThat(store.getStoreName()).isEqualTo("최소 정보 마트");
        assertThat(store.getIsActive()).isTrue();
        assertThat(store.getReviewCount()).isEqualTo(0L);
        assertThat(store.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Store 엔티티의 위치 정보를 설정할 수 있다")
    void setLocationInformation() {
        // given
        Store store = Store.builder()
                .storeName("위치 테스트 마트")
                .build();

        // when
        store.setLatitude(37.5665);
        store.setLongitude(126.9780);
        store.setStoreAddress("서울시 강남구 역삼동");

        // then
        assertThat(store.getLatitude()).isEqualTo(37.5665);
        assertThat(store.getLongitude()).isEqualTo(126.9780);
        assertThat(store.getStoreAddress()).isEqualTo("서울시 강남구 역삼동");
    }

    @Test
    @DisplayName("Store 엔티티의 평점 정보를 설정할 수 있다")
    void setRatingInformation() {
        // given
        Store store = Store.builder()
                .storeName("평점 테스트 마트")
                .build();

        // when
        store.setRatingAverage(4.2);
        store.setReviewCount(85L);

        // then
        assertThat(store.getRatingAverage()).isEqualTo(4.2);
        assertThat(store.getReviewCount()).isEqualTo(85L);
    }

    @Test
    @DisplayName("Store 엔티티의 영업 정보를 설정할 수 있다")
    void setBusinessInformation() {
        // given
        Store store = Store.builder()
                .storeName("영업 정보 테스트 마트")
                .build();

        // when
        store.setOperatingHours("10:00-21:00");
        store.setPhone("02-9876-5432");
        store.setBusinessNumber("987-65-43210");

        // then
        assertThat(store.getOperatingHours()).isEqualTo("10:00-21:00");
        assertThat(store.getPhone()).isEqualTo("02-9876-5432");
        assertThat(store.getBusinessNumber()).isEqualTo("987-65-43210");
    }

    @Test
    @DisplayName("Store 엔티티의 활성화 상태를 변경할 수 있다")
    void changeActiveStatus() {
        // given
        Store store = Store.builder()
                .storeName("상태 변경 테스트 마트")
                .isActive(true)
                .build();

        // when
        store.setIsActive(false);

        // then
        assertThat(store.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Store 엔티티의 기본 생성이 정상적으로 동작한다")
    void createStoreSuccessfully() {
        // given & when
        Store store = Store.builder()
                .storeName("기본 생성 테스트 마트")
                .build();

        // then
        assertThat(store.getStoreName()).isEqualTo("기본 생성 테스트 마트");
        assertThat(store.getIsActive()).isTrue();
        assertThat(store.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Store 엔티티의 설명과 프로필 이미지를 설정할 수 있다")
    void setDescriptionAndProfileImage() {
        // given
        Store store = Store.builder()
                .storeName("설명 테스트 마트")
                .build();

        // when
        store.setDescription("신선하고 좋은 품질의 식품을 판매합니다. 매일 아침 일찍 시장에서 직접 구매해온 신선한 재료들로 구성됩니다.");
        store.setStoreProfileImage("store_profile_image.jpg");

        // then
        assertThat(store.getDescription()).contains("신선하고 좋은 품질");
        assertThat(store.getStoreProfileImage()).isEqualTo("store_profile_image.jpg");
    }
}