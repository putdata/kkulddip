package com.kkulddip.domain.customer.dto;

/**
 * 고객 이름과 프로필 이미지 DTO
 * - 리뷰 등에서 고객의 이름과 프로필 이미지를 표시하기 위한 DTO
 */
public record CustomerNameAndImage(
    String name,
    String profileImageUrl
) {}