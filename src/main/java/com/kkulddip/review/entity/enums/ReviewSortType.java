package com.kkulddip.review.entity.enums;

public enum ReviewSortType {
    LATEST("최신 순", "createdAt", "DESC"),
    OLDEST("오래된 순", "createdAt", "ASC"),
    RATING_HIGH("평점 높은 순", "rating", "DESC"),
    RATING_LOW("평점 낮은 순", "rating", "ASC"),
    HELPFUL_HIGH("좋아요 많은 순", "helpful", "DESC");
    //사진 있는 리뷰 필터링
    private final String description;
    private final String field;
    private final String direction;

    ReviewSortType(String description, String field, String direction) {
        this.description = description;
        this.field = field;
        this.direction = direction;
    }

    public String getDescription() {
        return description;
    }

    public String getField() {
        return field;
    }

    public String getDirection() {
        return direction;
    }
}
