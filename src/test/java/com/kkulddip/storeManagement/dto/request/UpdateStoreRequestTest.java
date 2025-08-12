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

public class UpdateStoreRequestTest {

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
        UpdateStoreRequest request = UpdateStoreRequest.builder()
            .storeName("   Test Store   ")
            .phone("   010-1234-5678   ")
            .latitude(37.5665)
            .longitude(126.9780)
            .build();

        // When
        Set<ConstraintViolation<UpdateStoreRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.storeName()).isEqualTo("Test Store");
        assertThat(request.phone()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("위도 범위 초과 실패")
    void latitude_outOfRange_fail() {
        // Given
        UpdateStoreRequest request = UpdateStoreRequest.builder()
            .latitude(91.0)
            .build();

        // When
        Set<ConstraintViolation<UpdateStoreRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("위도는 90.0 이하여야 합니다.");
    }

    @Test
    @DisplayName("업데이트할 필드가 있을 때 hasUpdates()는 true를 반환")
    void hasUpdates_returnsTrue_whenFieldsExist() {
        // Given
        UpdateStoreRequest request = UpdateStoreRequest.builder()
            .storeName("New Store Name")
            .build();

        // Then
        assertThat(request.hasUpdates()).isTrue();
    }

    @Test
    @DisplayName("업데이트할 필드가 없을 때 hasUpdates()는 false를 반환")
    void hasUpdates_returnsFalse_whenNoFieldsExist() {
        // Given
        UpdateStoreRequest request = UpdateStoreRequest.builder().build();

        // Then
        assertThat(request.hasUpdates()).isFalse();
    }
}