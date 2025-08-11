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

public class UpdateStoreStatusRequestTest {

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
        UpdateStoreStatusRequest request = new UpdateStoreStatusRequest(true, "   Valid Reason   ");

        // When
        Set<ConstraintViolation<UpdateStoreStatusRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.isActive()).isTrue();
        assertThat(request.reason()).isEqualTo("Valid Reason");
    }

    @Test
    @DisplayName("isActive 필드가 null일 때 실패")
    void isActive_isNull_fail() {
        // Given
        UpdateStoreStatusRequest request = new UpdateStoreStatusRequest(null, "Test Reason");

        // When
        Set<ConstraintViolation<UpdateStoreStatusRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("활성화 상태는 필수입니다.");
    }
}
