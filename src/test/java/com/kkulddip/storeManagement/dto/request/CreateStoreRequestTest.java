package com.kkulddip.storeManagement.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class CreateStoreRequestTest {

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
        CreateStoreRequest request = CreateStoreRequest.builder()
            .storeName("   꿀띱 가게   ")
            .phone("010-1234-5678")
            .description("설명")
            .operatingHours("평일 10-20")
            .businessNumber("123-45-67890")
            .storeAddress("   서울시 강남구   ")
            .latitude(37.5)
            .longitude(127.0)
            .build();

        // When
        Set<ConstraintViolation<CreateStoreRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.storeName()).isEqualTo("꿀띱 가게");
        assertThat(request.storeAddress()).isEqualTo("서울시 강남구");
    }

    @Test
    @DisplayName("필수 필드 누락 시 실패")
    void requiredFields_null_fail() {
        // Given
        CreateStoreRequest request = CreateStoreRequest.builder().build();

        // When
        Set<ConstraintViolation<CreateStoreRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(4);
        violations.forEach(v -> assertThat(v.getMessage()).isIn(
            "가게명은 필수입니다.",
            "가게 주소는 필수입니다.",
            "위도는 필수입니다.",
            "경도는 필수입니다."
        ));
    }
}