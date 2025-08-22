package com.kkulddip.store.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Cursor 기반 페이지네이션을 위한 Page DTO
 * Spring Data의 Page 인터페이스와 유사하지만 Cursor 방식에 맞게 조정
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Page<T> {

    /**
     * 페이지 내용
     */
    private List<T> content;

    /**
     * 다음 페이지 존재 여부
     */
    private Boolean hasNext;

    /**
     * 페이지 크기
     */
    private Integer size;

    /**
     * 실제 반환된 요소 수
     */
    private Integer actualSize;

    /**
     * 첫 번째 페이지 여부
     */
    private Boolean isFirst;

    /**
     * 마지막 페이지 여부
     */
    private Boolean isLast;

    /**
     * 다음 페이지를 위한 커서
     */
    private String cursor;

    /**
     * 추가 메타데이터 (선택적)
     */
    private PageMetadata metadata;

    @Getter
@Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageMetadata {
        private String sortBy;
        private String sortDirection;
        private Long totalEstimate;
        private String searchKeyword;
        private String category;
    }

    /**
     * 빈 페이지 생성
     */
    public static <T> Page<T> empty(Integer size) {
        return Page.<T>builder()
            .content(List.of())
            .hasNext(false)
            .size(size)
            .actualSize(0)
            .isFirst(true)
            .isLast(true)
            .cursor(null)
            .build();
    }

    /**
     * 페이지 생성 헬퍼 메소드
     */
    public static <T> Page<T> of(List<T> content,
                                 Integer requestedSize,
                                 boolean hasMore,
                                 String nextCursor,
                                 boolean isFirstPage) {

        return Page.<T>builder()
            .content(content)
            .hasNext(hasMore)
            .size(requestedSize)
            .actualSize(content.size())
            .isFirst(isFirstPage)
            .isLast(!hasMore)
            .cursor(nextCursor)
            .build();
    }
}