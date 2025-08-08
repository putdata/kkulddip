package com.kkulddip.store.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Cursor 기반 페이지네이션을 위한 커서 정보 클래스
 * Base64 URL-safe 인코딩을 사용하여 커서를 안전하게 인코딩/디코딩
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CursorInfo {

    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    /**
     * 엔티티 ID (Primary Key)
     */
    private Long id;

    /**
     * 생성일시 (created_at 기준 정렬용)
     */
    private LocalDateTime createdAt;

    /**
     * 평점 (rating 기준 정렬용)
     */
    private Double rating;

    /**
     * 사용자로부터의 거리 (distance 기준 정렬용)
     */
    private Double distance;

    @JsonCreator
    public CursorInfo(@JsonProperty("id") Long id,
                      @JsonProperty("createdAt") LocalDateTime createdAt,
                      @JsonProperty("rating") Double rating,
                      @JsonProperty("distance") Double distance) {
        this.id = id;
        this.createdAt = createdAt;
        this.rating = rating;
        this.distance = distance;
    }

    /**
     * CursorInfo를 Base64 URL-safe 인코딩된 문자열로 변환
     */
    public String encode() {
        try {
            String json = objectMapper.writeValueAsString(this);
            return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            log.error("Failed to encode cursor info: {}", this, e);
            throw new IllegalArgumentException("커서 정보 인코딩에 실패했습니다.", e);
        }
    }

    /**
     * Base64 URL-safe 인코딩된 문자열을 CursorInfo로 디코딩
     */
    public static CursorInfo decode(String encodedCursor) {
        if (encodedCursor == null || encodedCursor.trim().isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedCursor);
            String json = new String(decodedBytes, StandardCharsets.UTF_8);
            return objectMapper.readValue(json, CursorInfo.class);
        } catch (Exception e) {
            log.warn("Failed to decode cursor: {}", encodedCursor, e);
            return null;
        }
    }

    /**
     * ID 기준 커서 생성
     */
    public static CursorInfo ofId(Long id) {
        return CursorInfo.builder()
            .id(id)
            .build();
    }

    /**
     * 생성일시 + ID 기준 커서 생성
     */
    public static CursorInfo ofCreatedAt(Long id, LocalDateTime createdAt) {
        return CursorInfo.builder()
            .id(id)
            .createdAt(createdAt)
            .build();
    }

    /**
     * 평점 + ID 기준 커서 생성
     */
    public static CursorInfo ofRating(Long id, Double rating) {
        return CursorInfo.builder()
            .id(id)
            .rating(rating)
            .build();
    }

    /**
     * 거리 + ID 기준 커서 생성
     */
    public static CursorInfo ofDistance(Long id, Double distance) {
        return CursorInfo.builder()
            .id(id)
            .distance(distance)
            .build();
    }

    /**
     * 커서가 유효한지 확인
     */
    public boolean isValid() {
        return id != null && id > 0;
    }

    /**
     * 생성일시 기준 정렬용 커서인지 확인
     */
    public boolean hasCreatedAt() {
        return createdAt != null;
    }

    /**
     * 평점 기준 정렬용 커서인지 확인
     */
    public boolean hasRating() {
        return rating != null;
    }

    /**
     * 거리 기준 정렬용 커서인지 확인
     */
    public boolean hasDistance() {
        return distance != null;
    }
}