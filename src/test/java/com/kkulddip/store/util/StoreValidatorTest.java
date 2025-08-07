package com.kkulddip.store.util;

import com.kkulddip.store.exception.StoreValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StoreValidator 테스트")
@ActiveProfiles("citest")
class StoreValidatorTest {

    @Test
    @DisplayName("유효한 가게 ID는 검증을 통과한다")
    void validateStoreId_ValidId() {
        // given & when & then
        StoreValidator.validateStoreId(1L);
        StoreValidator.validateStoreId(100L);
        StoreValidator.validateStoreId(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("null 가게 ID는 예외를 발생시킨다")
    void validateStoreId_NullId() {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validateStoreId(null))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("가게 ID: null");
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    @DisplayName("0 이하의 가게 ID는 예외를 발생시킨다")
    void validateStoreId_InvalidId(Long invalidId) {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validateStoreId(invalidId))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("가게 ID: " + invalidId);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 25, 50})
    @DisplayName("유효한 페이지 크기는 검증을 통과한다")
    void validatePageSize_ValidSize(Integer validSize) {
        // when & then - 예외가 발생하지 않아야 함
        StoreValidator.validatePageSize(validSize);
    }

    @Test
    @DisplayName("null 페이지 크기는 예외를 발생시킨다")
    void validatePageSize_NullSize() {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validatePageSize(null))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("페이지 크기: null");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("1 미만의 페이지 크기는 예외를 발생시킨다")
    void validatePageSize_TooSmall(Integer smallSize) {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validatePageSize(smallSize))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("페이지 크기: " + smallSize);
    }

    @ParameterizedTest
    @ValueSource(ints = {51, 100, 1000})
    @DisplayName("50 초과의 페이지 크기는 예외를 발생시킨다")
    void validatePageSize_TooLarge(Integer largeSize) {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validatePageSize(largeSize))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("페이지 크기: " + largeSize);
    }

    @ParameterizedTest
    @ValueSource(strings = {"마트", "카페", "베이커리", "a", "검색어"})
    @DisplayName("유효한 검색 키워드는 검증을 통과한다")
    void validateSearchKeyword_ValidKeyword(String validKeyword) {
        // when & then - 예외가 발생하지 않아야 함
        StoreValidator.validateSearchKeyword(validKeyword);
    }

    @Test
    @DisplayName("null 검색 키워드는 예외를 발생시킨다")
    void validateSearchKeyword_NullKeyword() {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validateSearchKeyword(null))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("검색 키워드는 필수입니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("빈 검색 키워드는 예외를 발생시킨다")
    void validateSearchKeyword_EmptyKeyword(String emptyKeyword) {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validateSearchKeyword(emptyKeyword))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("검색 키워드는 필수입니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"유기농", "베이커리", "카페", "디저트"})
    @DisplayName("유효한 카테고리는 검증을 통과한다")
    void validateCategory_ValidCategory(String validCategory) {
        // when & then - 예외가 발생하지 않아야 함
        StoreValidator.validateCategory(validCategory);
    }

    @Test
    @DisplayName("null 카테고리는 예외를 발생시킨다")
    void validateCategory_NullCategory() {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validateCategory(null))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("카테고리는 필수입니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("빈 카테고리는 예외를 발생시킨다")
    void validateCategory_EmptyCategory(String emptyCategory) {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validateCategory(emptyCategory))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("카테고리는 필수입니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "created_at", "createdat", "rating", "distance"})
    @DisplayName("유효한 정렬 타입은 정규화되어 반환된다")
    void validateAndNormalizeSortBy_ValidSortType(String validSortBy) {
        // when
        String result = StoreValidator.validateAndNormalizeSortBy(validSortBy);

        // then
        assertThat(result).isEqualTo(validSortBy.toLowerCase());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ID", "CREATED_AT", "Rating", "Distance"})
    @DisplayName("대소문자 구분 없이 정렬 타입을 정규화한다")
    void validateAndNormalizeSortBy_CaseInsensitive(String sortBy) {
        // when
        String result = StoreValidator.validateAndNormalizeSortBy(sortBy);

        // then
        assertThat(result).isEqualTo(sortBy.toLowerCase());
    }

    @Test
    @DisplayName("null 정렬 타입은 기본값 'id'를 반환한다")
    void validateAndNormalizeSortBy_NullSortType() {
        // when
        String result = StoreValidator.validateAndNormalizeSortBy(null);

        // then
        assertThat(result).isEqualTo("id");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    @DisplayName("빈 정렬 타입은 기본값 'id'를 반환한다")
    void validateAndNormalizeSortBy_EmptySortType(String emptySortBy) {
        // when
        String result = StoreValidator.validateAndNormalizeSortBy(emptySortBy);

        // then
        assertThat(result).isEqualTo("id");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "name", "price", "location"})
    @DisplayName("지원하지 않는 정렬 타입은 예외를 발생시킨다")
    void validateAndNormalizeSortBy_UnsupportedSortType(String unsupportedSortBy) {
        // when & then
        assertThatThrownBy(() -> StoreValidator.validateAndNormalizeSortBy(unsupportedSortBy))
                .isInstanceOf(StoreValidationException.class)
                .hasMessageContaining("정렬 타입: " + unsupportedSortBy);
    }

    @Test
    @DisplayName("공백이 포함된 정렬 타입도 정규화된다")
    void validateAndNormalizeSortBy_WithWhitespace() {
        // when
        String result = StoreValidator.validateAndNormalizeSortBy("  id  ");

        // then
        assertThat(result).isEqualTo("id");
    }
}