package com.kkulddip.store.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CursorInfo 테스트")
class CursorInfoTest {

    @Test
    @DisplayName("ID만 있는 CursorInfo를 인코딩/디코딩한다")
    void encodeAndDecode_IdOnly() {
        // given
        CursorInfo original = CursorInfo.ofId(123L);

        // when
        String encoded = original.encode();
        CursorInfo decoded = CursorInfo.decode(encoded);

        // then
        assertThat(decoded).isNotNull();
        assertThat(decoded.getId()).isEqualTo(123L);
        assertThat(decoded.getCreatedAt()).isNull();
        assertThat(decoded.getRating()).isNull();
        assertThat(decoded.getDistance()).isNull();
    }

    @Test
    @DisplayName("ID와 생성일시가 있는 CursorInfo를 인코딩/디코딩한다")
    void encodeAndDecode_WithCreatedAt() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        CursorInfo original = CursorInfo.ofCreatedAt(456L, createdAt);

        // when
        String encoded = original.encode();
        CursorInfo decoded = CursorInfo.decode(encoded);

        // then
        assertThat(decoded).isNotNull();
        assertThat(decoded.getId()).isEqualTo(456L);
        assertThat(decoded.getCreatedAt()).isEqualTo(createdAt);
        assertThat(decoded.getRating()).isNull();
        assertThat(decoded.getDistance()).isNull();
    }

    @Test
    @DisplayName("ID와 평점이 있는 CursorInfo를 인코딩/디코딩한다")
    void encodeAndDecode_WithRating() {
        // given
        CursorInfo original = CursorInfo.ofRating(789L, 4.5);

        // when
        String encoded = original.encode();
        CursorInfo decoded = CursorInfo.decode(encoded);

        // then
        assertThat(decoded).isNotNull();
        assertThat(decoded.getId()).isEqualTo(789L);
        assertThat(decoded.getRating()).isEqualTo(4.5);
        assertThat(decoded.getCreatedAt()).isNull();
        assertThat(decoded.getDistance()).isNull();
    }

    @Test
    @DisplayName("ID와 거리가 있는 CursorInfo를 인코딩/디코딩한다")
    void encodeAndDecode_WithDistance() {
        // given
        CursorInfo original = CursorInfo.ofDistance(101L, 12.34);

        // when
        String encoded = original.encode();
        CursorInfo decoded = CursorInfo.decode(encoded);

        // then
        assertThat(decoded).isNotNull();
        assertThat(decoded.getId()).isEqualTo(101L);
        assertThat(decoded.getDistance()).isEqualTo(12.34);
        assertThat(decoded.getCreatedAt()).isNull();
        assertThat(decoded.getRating()).isNull();
    }

    @Test
    @DisplayName("모든 필드가 있는 CursorInfo를 인코딩/디코딩한다")
    void encodeAndDecode_AllFields() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2024, 6, 15, 14, 30, 45);
        CursorInfo original = CursorInfo.builder()
                .id(999L)
                .createdAt(createdAt)
                .rating(3.8)
                .distance(5.67)
                .build();

        // when
        String encoded = original.encode();
        CursorInfo decoded = CursorInfo.decode(encoded);

        // then
        assertThat(decoded).isNotNull();
        assertThat(decoded.getId()).isEqualTo(999L);
        assertThat(decoded.getCreatedAt()).isEqualTo(createdAt);
        assertThat(decoded.getRating()).isEqualTo(3.8);
        assertThat(decoded.getDistance()).isEqualTo(5.67);
    }

    @Test
    @DisplayName("null 커서를 디코딩하면 null을 반환한다")
    void decode_NullCursor() {
        // when
        CursorInfo result = CursorInfo.decode(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("빈 문자열 커서를 디코딩하면 null을 반환한다")
    void decode_EmptyCursor() {
        // when
        CursorInfo result = CursorInfo.decode("");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("공백만 있는 커서를 디코딩하면 null을 반환한다")
    void decode_WhitespaceCursor() {
        // when
        CursorInfo result = CursorInfo.decode("   ");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("잘못된 형식의 커서를 디코딩하면 null을 반환한다")
    void decode_InvalidCursor() {
        // when
        CursorInfo result = CursorInfo.decode("invalid-cursor-string");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Base64가 아닌 커서를 디코딩하면 null을 반환한다")
    void decode_NonBase64Cursor() {
        // when
        CursorInfo result = CursorInfo.decode("not-base64-string!!!");

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("유효한 CursorInfo는 valid하다고 판단한다")
    void isValid_ValidCursor() {
        // given
        CursorInfo cursor = CursorInfo.ofId(1L);

        // when & then
        assertThat(cursor.isValid()).isTrue();
    }

    @Test
    @DisplayName("ID가 null인 CursorInfo는 invalid하다고 판단한다")
    void isValid_NullId() {
        // given
        CursorInfo cursor = CursorInfo.builder().id(null).build();

        // when & then
        assertThat(cursor.isValid()).isFalse();
    }

    @Test
    @DisplayName("ID가 0 이하인 CursorInfo는 invalid하다고 판단한다")
    void isValid_ZeroOrNegativeId() {
        // given
        CursorInfo cursor1 = CursorInfo.builder().id(0L).build();
        CursorInfo cursor2 = CursorInfo.builder().id(-1L).build();

        // when & then
        assertThat(cursor1.isValid()).isFalse();
        assertThat(cursor2.isValid()).isFalse();
    }

    @Test
    @DisplayName("생성일시가 있는지 확인한다")
    void hasCreatedAt() {
        // given
        CursorInfo withCreatedAt = CursorInfo.ofCreatedAt(1L, LocalDateTime.now());
        CursorInfo withoutCreatedAt = CursorInfo.ofId(1L);

        // when & then
        assertThat(withCreatedAt.hasCreatedAt()).isTrue();
        assertThat(withoutCreatedAt.hasCreatedAt()).isFalse();
    }

    @Test
    @DisplayName("평점이 있는지 확인한다")
    void hasRating() {
        // given
        CursorInfo withRating = CursorInfo.ofRating(1L, 4.5);
        CursorInfo withoutRating = CursorInfo.ofId(1L);

        // when & then
        assertThat(withRating.hasRating()).isTrue();
        assertThat(withoutRating.hasRating()).isFalse();
    }

    @Test
    @DisplayName("거리가 있는지 확인한다")
    void hasDistance() {
        // given
        CursorInfo withDistance = CursorInfo.ofDistance(1L, 10.0);
        CursorInfo withoutDistance = CursorInfo.ofId(1L);

        // when & then
        assertThat(withDistance.hasDistance()).isTrue();
        assertThat(withoutDistance.hasDistance()).isFalse();
    }

    @Test
    @DisplayName("인코딩된 커서는 Base64 URL-safe 형식이다")
    void encode_Base64UrlSafeFormat() {
        // given
        CursorInfo cursor = CursorInfo.ofId(123L);

        // when
        String encoded = cursor.encode();

        // then
        assertThat(encoded).isNotEmpty();
        assertThat(encoded).doesNotContain("+", "/", "="); // URL-safe Base64는 이 문자들이 없어야 함
        assertThat(encoded).matches("[A-Za-z0-9_-]+"); // URL-safe Base64 문자만 포함
    }

    @Test
    @DisplayName("동일한 CursorInfo는 동일한 인코딩 결과를 가진다")
    void encode_Consistency() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        CursorInfo cursor1 = CursorInfo.builder()
                .id(123L)
                .createdAt(createdAt)
                .rating(4.5)
                .build();
        CursorInfo cursor2 = CursorInfo.builder()
                .id(123L)
                .createdAt(createdAt)
                .rating(4.5)
                .build();

        // when
        String encoded1 = cursor1.encode();
        String encoded2 = cursor2.encode();

        // then
        assertThat(encoded1).isEqualTo(encoded2);
    }
}