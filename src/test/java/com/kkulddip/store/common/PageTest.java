package com.kkulddip.store.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Page 테스트")
class PageTest {

    @Test
    @DisplayName("빈 페이지를 생성한다")
    void empty() {
        // when
        Page<String> emptyPage = Page.empty(10);

        // then
        assertThat(emptyPage.getContent()).isEmpty();
        assertThat(emptyPage.getHasNext()).isFalse();
        assertThat(emptyPage.getSize()).isEqualTo(10);
        assertThat(emptyPage.getActualSize()).isEqualTo(0);
        assertThat(emptyPage.getIsFirst()).isTrue();
        assertThat(emptyPage.getIsLast()).isTrue();
        assertThat(emptyPage.getCursor()).isNull();
    }

    @Test
    @DisplayName("첫 번째 페이지를 생성한다")
    void of_FirstPage() {
        // given
        List<String> content = Arrays.asList("item1", "item2", "item3");
        boolean hasMore = true;
        String nextCursor = "next-cursor";
        boolean isFirstPage = true;

        // when
        Page<String> page = Page.of(content, 5, hasMore, nextCursor, isFirstPage);

        // then
        assertThat(page.getContent()).containsExactly("item1", "item2", "item3");
        assertThat(page.getHasNext()).isTrue();
        assertThat(page.getSize()).isEqualTo(5);
        assertThat(page.getActualSize()).isEqualTo(3);
        assertThat(page.getIsFirst()).isTrue();
        assertThat(page.getIsLast()).isFalse();
        assertThat(page.getCursor()).isEqualTo("next-cursor");
    }

    @Test
    @DisplayName("마지막 페이지를 생성한다")
    void of_LastPage() {
        // given
        List<String> content = Arrays.asList("item1", "item2");
        boolean hasMore = false;
        String nextCursor = null;
        boolean isFirstPage = false;

        // when
        Page<String> page = Page.of(content, 5, hasMore, nextCursor, isFirstPage);

        // then
        assertThat(page.getContent()).containsExactly("item1", "item2");
        assertThat(page.getHasNext()).isFalse();
        assertThat(page.getSize()).isEqualTo(5);
        assertThat(page.getActualSize()).isEqualTo(2);
        assertThat(page.getIsFirst()).isFalse();
        assertThat(page.getIsLast()).isTrue();
        assertThat(page.getCursor()).isNull();
    }

    @Test
    @DisplayName("중간 페이지를 생성한다")
    void of_MiddlePage() {
        // given
        List<String> content = Arrays.asList("item4", "item5", "item6");
        boolean hasMore = true;
        String nextCursor = "cursor-123";
        boolean isFirstPage = false;

        // when
        Page<String> page = Page.of(content, 3, hasMore, nextCursor, isFirstPage);

        // then
        assertThat(page.getContent()).containsExactly("item4", "item5", "item6");
        assertThat(page.getHasNext()).isTrue();
        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getActualSize()).isEqualTo(3);
        assertThat(page.getIsFirst()).isFalse();
        assertThat(page.getIsLast()).isFalse();
        assertThat(page.getCursor()).isEqualTo("cursor-123");
    }

    @Test
    @DisplayName("단일 페이지를 생성한다")
    void of_SinglePage() {
        // given
        List<String> content = Arrays.asList("only-item");
        boolean hasMore = false;
        String nextCursor = null;
        boolean isFirstPage = true;

        // when
        Page<String> page = Page.of(content, 10, hasMore, nextCursor, isFirstPage);

        // then
        assertThat(page.getContent()).containsExactly("only-item");
        assertThat(page.getHasNext()).isFalse();
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getActualSize()).isEqualTo(1);
        assertThat(page.getIsFirst()).isTrue();
        assertThat(page.getIsLast()).isTrue();
        assertThat(page.getCursor()).isNull();
    }

    @Test
    @DisplayName("빈 컨텐츠로 페이지를 생성한다")
    void of_EmptyContent() {
        // given
        List<String> content = Collections.emptyList();
        boolean hasMore = false;
        String nextCursor = null;
        boolean isFirstPage = true;

        // when
        Page<String> page = Page.of(content, 5, hasMore, nextCursor, isFirstPage);

        // then
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getHasNext()).isFalse();
        assertThat(page.getSize()).isEqualTo(5);
        assertThat(page.getActualSize()).isEqualTo(0);
        assertThat(page.getIsFirst()).isTrue();
        assertThat(page.getIsLast()).isTrue();
        assertThat(page.getCursor()).isNull();
    }

    @Test
    @DisplayName("메타데이터를 설정할 수 있다")
    void setMetadata() {
        // given
        Page<String> page = Page.empty(10);
        Page.PageMetadata metadata = Page.PageMetadata.builder()
                .sortBy("id")
                .sortDirection("ASC")
                .searchKeyword("test")
                .category("food")
                .totalEstimate(100L)
                .build();

        // when
        page.setMetadata(metadata);

        // then
        assertThat(page.getMetadata()).isNotNull();
        assertThat(page.getMetadata().getSortBy()).isEqualTo("id");
        assertThat(page.getMetadata().getSortDirection()).isEqualTo("ASC");
        assertThat(page.getMetadata().getSearchKeyword()).isEqualTo("test");
        assertThat(page.getMetadata().getCategory()).isEqualTo("food");
        assertThat(page.getMetadata().getTotalEstimate()).isEqualTo(100L);
    }

    @Test
    @DisplayName("PageMetadata를 생성한다")
    void pageMetadata_Creation() {
        // when
        Page.PageMetadata metadata = Page.PageMetadata.builder()
                .sortBy("rating")
                .sortDirection("DESC")
                .build();

        // then
        assertThat(metadata.getSortBy()).isEqualTo("rating");
        assertThat(metadata.getSortDirection()).isEqualTo("DESC");
        assertThat(metadata.getSearchKeyword()).isNull();
        assertThat(metadata.getCategory()).isNull();
        assertThat(metadata.getTotalEstimate()).isNull();
    }

    @Test
    @DisplayName("PageMetadata의 모든 필드를 설정한다")
    void pageMetadata_AllFields() {
        // when
        Page.PageMetadata metadata = Page.PageMetadata.builder()
                .sortBy("distance")
                .sortDirection("ASC")
                .searchKeyword("마트")
                .category("유기농")
                .totalEstimate(250L)
                .build();

        // then
        assertThat(metadata.getSortBy()).isEqualTo("distance");
        assertThat(metadata.getSortDirection()).isEqualTo("ASC");
        assertThat(metadata.getSearchKeyword()).isEqualTo("마트");
        assertThat(metadata.getCategory()).isEqualTo("유기농");
        assertThat(metadata.getTotalEstimate()).isEqualTo(250L);
    }

    @Test
    @DisplayName("actualSize는 content의 실제 크기와 일치한다")
    void actualSize_MatchesContentSize() {
        // given
        List<Integer> content = Arrays.asList(1, 2, 3, 4, 5);

        // when
        Page<Integer> page = Page.of(content, 10, false, null, true);

        // then
        assertThat(page.getActualSize()).isEqualTo(content.size());
        assertThat(page.getActualSize()).isEqualTo(5);
    }

    @Test
    @DisplayName("size와 actualSize가 다를 수 있다")
    void size_DifferentFromActualSize() {
        // given
        List<String> content = Arrays.asList("a", "b"); // 2개 아이템
        int requestedSize = 10; // 요청 크기는 10

        // when
        Page<String> page = Page.of(content, requestedSize, false, null, true);

        // then
        assertThat(page.getSize()).isEqualTo(10); // 요청된 크기
        assertThat(page.getActualSize()).isEqualTo(2); // 실제 아이템 수
    }
}