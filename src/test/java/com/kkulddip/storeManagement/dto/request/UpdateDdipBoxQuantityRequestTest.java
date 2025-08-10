package com.kkulddip.storeManagement.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class UpdateDdipBoxQuantityRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 DTO 생성 성공 - 직접 수량 업데이트")
    void validDirectUpdate_success() {
        // Given
        UpdateDdipBoxQuantityRequest request = new UpdateDdipBoxQuantityRequest(5L, null, false);

        // When
        Set<ConstraintViolation<UpdateDdipBoxQuantityRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.isDirectQuantityUpdate()).isTrue();
        assertThat(request.isDailyQuantityReset()).isFalse();
        assertThat(request.hasValidOperation()).isTrue();
    }

    @Test
    @DisplayName("유효한 DTO 생성 성공 - 일일 수량 리셋")
    void validDailyReset_success() {
        // Given
        UpdateDdipBoxQuantityRequest request = new UpdateDdipBoxQuantityRequest(null, 10L, true);

        // When
        Set<ConstraintViolation<UpdateDdipBoxQuantityRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.isDirectQuantityUpdate()).isFalse();
        assertThat(request.isDailyQuantityReset()).isTrue();
        assertThat(request.hasValidOperation()).isTrue();
    }

    @Test
    @DisplayName("잔여 수량이 0 미만이면 실패")
    void remainingQuantity_lessThanZero_fail() {
        // Given
        UpdateDdipBoxQuantityRequest request = new UpdateDdipBoxQuantityRequest(-1L, null, false);

        // When
        Set<ConstraintViolation<UpdateDdipBoxQuantityRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("잔여 수량은 0개 이상이어야 합니다.");
    }
}