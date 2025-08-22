package com.kkulddip.storeManagement.dto.request;

import com.kkulddip.storeManagement.dto.request.UpdateDdipBoxRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateDdipBoxRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 요청 DTO 생성 성공")
    void validRequest_success() {
        // Given
        UpdateDdipBoxRequest request = UpdateDdipBoxRequest.builder()
            .ddipboxName("   Valid Name   ")
            .description("   Valid Description   ")
            .category("   FOOD   ")
            .originalPrice(10000L)
            .salePrice(5000L)
            .dailyQuantity(10L)
            .maxPerCustomer(3L)
            .build();

        // When
        Set<ConstraintViolation<UpdateDdipBoxRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.ddipboxName()).isEqualTo("Valid Name");
        assertThat(request.description()).isEqualTo("Valid Description");
        assertThat(request.category()).isEqualTo("FOOD");
    }

    @Test
    @DisplayName("띱박스명 글자수 초과 실패")
    void ddipboxName_tooLong_fail() {
        // Given
        String longName = "a".repeat(101);
        UpdateDdipBoxRequest request = UpdateDdipBoxRequest.builder()
            .ddipboxName(longName)
            .build();

        // When
        Set<ConstraintViolation<UpdateDdipBoxRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("띱박스명은 100자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("판매가 0원 미만 실패")
    void salePrice_lessThanZero_fail() {
        // Given
        UpdateDdipBoxRequest request = UpdateDdipBoxRequest.builder()
            .salePrice(-1L)
            .build();

        // When
        Set<ConstraintViolation<UpdateDdipBoxRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("판매가는 0원 이상이어야 합니다.");
    }

    @Test
    @DisplayName("업데이트할 필드가 있을 때 hasUpdates()는 true를 반환")
    void hasUpdates_returnsTrue_whenFieldsExist() {
        // Given
        UpdateDdipBoxRequest request = UpdateDdipBoxRequest.builder()
            .ddipboxName("New Name")
            .build();

        // Then
        assertThat(request.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("업데이트할 필드가 없을 때 hasUpdates()는 false를 반환")
    void hasUpdates_returnsFalse_whenNoFieldsExist() {
        // Given
        UpdateDdipBoxRequest request = UpdateDdipBoxRequest.builder().build();

        // Then
        assertThat(request.hasUpdates()).isFalse();
    }
}