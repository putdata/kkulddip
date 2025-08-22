package com.kkulddip.favorite.enums;

/**
 * 즐겨찾기 정렬 방식 열거형
 */
public enum FavoriteSortType {
    NAME_ASC("이름순 오름차순"),
    CREATED_DESC("등록순 내림차순"),
    DISTANCE_ASC("거리순 오름차순"),
    RATING_DESC("평점순 내림차순"),
    CATEGORY("카테고리순");

    private final String description;

    FavoriteSortType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}