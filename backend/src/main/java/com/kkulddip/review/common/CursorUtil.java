package com.kkulddip.review.common;

import com.kkulddip.review.entity.Review;
import com.kkulddip.review.entity.enums.ReviewSortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
public class CursorUtil {
    private static final DateTimeFormatter CURSOR_DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String DELIMITER = "|";

    /**
     * 커서 생성 - Base64 인코딩
     */
    public String createCursor(Review review, ReviewSortType sortType) {
        // 정렬값 생성
        String sortValue = switch (sortType) {
            case LATEST, OLDEST -> review.getCreatedAt().format(CURSOR_DATE_FORMAT);
            case RATING_HIGH, RATING_LOW -> String.valueOf(review.getRating());
            case HELPFUL_HIGH -> String.valueOf(review.getHelpfulCount());
        };

        // 원본 커서 문자열
        String rawCursor = sortValue + DELIMITER + review.getReviewId();

        // Base64 URL-safe 인코딩 (패딩 제거)
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(rawCursor.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 커서 파싱 - Base64 디코딩
     */
    public CursorData parseCursor(String encodedCursor, ReviewSortType sortType) {
        if (encodedCursor == null || encodedCursor.isEmpty()) {
            return null;
        }

        try {
            // Base64 디코딩
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedCursor);
            String rawCursor = new String(decodedBytes, StandardCharsets.UTF_8);

            // 구분자로 분리
            String[] parts = rawCursor.split("\\" + DELIMITER);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid cursor format");
            }

            Long reviewId = Long.parseLong(parts[1]);

            // 정렬 타입에 따라 파싱
            return switch (sortType) {
                case LATEST, OLDEST -> {
                    LocalDateTime dateTime = LocalDateTime.parse(parts[0], CURSOR_DATE_FORMAT);
                    yield new DateCursorData(dateTime, reviewId);
                }
                case RATING_HIGH, RATING_LOW, HELPFUL_HIGH -> {
                    Integer value = Integer.parseInt(parts[0]);
                    yield new NumericCursorData(value, reviewId);
                }
            };

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid cursor encoding", e);
        }
    }

    // 커서 데이터 클래스들
    public interface CursorData {
        Long getReviewId();
    }

    @Getter
    @AllArgsConstructor
    public static class DateCursorData implements CursorData {
        private LocalDateTime dateTime;
        private Long reviewId;
    }

    @Getter
    @AllArgsConstructor
    public static class NumericCursorData implements CursorData {
        private Integer value;
        private Long reviewId;
    }

    /**
     * 커서 데이터를 Repository 파라미터로 변환
     */
    public CursorParams extractParams(CursorData cursorData) {
        if (cursorData == null) {
            return CursorParams.empty();
        }

        Long reviewId = cursorData.getReviewId();

        if (cursorData instanceof DateCursorData dateCursor) {
            return CursorParams.ofDate(dateCursor.getDateTime(), reviewId);
        } else if (cursorData instanceof NumericCursorData numCursor) {
            return CursorParams.ofNumeric(numCursor.getValue(), reviewId);
        }

        throw new IllegalStateException("Unknown cursor type");
    }

    /**
     * 커서 파라미터 홀더 클래스
     */
    @Getter
    public static class CursorParams {
        private final LocalDateTime dateTime;
        private final Integer numericValue;
        private final Long reviewId;

        private CursorParams(LocalDateTime dateTime, Integer numericValue, Long reviewId) {
            this.dateTime = dateTime;
            this.numericValue = numericValue;
            this.reviewId = reviewId;
        }

        public static CursorParams empty() {
            return new CursorParams(null, null, null);
        }

        public static CursorParams ofDate(LocalDateTime dateTime, Long reviewId) {
            return new CursorParams(dateTime, null, reviewId);
        }

        public static CursorParams ofNumeric(Integer value, Long reviewId) {
            return new CursorParams(null, value, reviewId);
        }
    }
}
