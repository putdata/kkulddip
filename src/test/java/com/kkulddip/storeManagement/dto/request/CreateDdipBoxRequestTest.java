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

class CreateDdipBoxRequestTest {

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
        CreateDdipBoxRequest request = CreateDdipBoxRequest.builder()
            .ddipboxName("   테스트 띱박스   ")
            .description("   설명   ")
            .category("   음식   ")
            .originalPrice(10000L)
            .salePrice(5000L)
            .dailyQuantity(10L)
            .maxPerCustomer(3L)
            .build();

        // When
        Set<ConstraintViolation<CreateDdipBoxRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.ddipboxName()).isEqualTo("테스트 띱박스");
        assertThat(request.description()).isEqualTo("설명");
        assertThat(request.category()).isEqualTo("음식");
    }

    @Test
    @DisplayName("필수 필드가 누락되면 실패")
    void requiredFields_null_fail() {
        // Given
        CreateDdipBoxRequest request = CreateDdipBoxRequest.builder().build();

        // When
        Set<ConstraintViolation<CreateDdipBoxRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(6);
        violations.forEach(v -> assertThat(v.getMessage()).isIn(
            "띱박스명은 필수입니다.",
            "카테고리는 필수입니다.",
            "정가는 필수입니다.",
            "판매가는 필수입니다.",
            "일일 수량은 필수입니다.",
            "고객당 최대 구매 수량은 필수입니다."
        ));
    }

    @Test
    @DisplayName("판매가가 0원 미만이면 실패")
    void salePrice_lessThanZero_fail() {
        // Given
        CreateDdipBoxRequest request = CreateDdipBoxRequest.builder()
            .ddipboxName("Test")
            .category("FOOD")
            .originalPrice(10000L)
            .salePrice(-1L)
            .dailyQuantity(1L)
            .maxPerCustomer(1L)
            .build();

        // When
        Set<ConstraintViolation<CreateDdipBoxRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("판매가는 0원 이상이어야 합니다.");
    }
}