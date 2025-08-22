package com.kkulddip.fcmToken.dto;

import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import com.kkulddip.domain.userToken.enums.DeviceType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FcmTokenRequest 테스트")
class FcmTokenRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("정상적인 요청 테스트")
    class ValidRequestTest {

        @Test
        @DisplayName("모든 필드가 유효한 경우 - 성공")
        void validRequest_Success() {
            // given
            FcmTokenRequest request = new FcmTokenRequest("valid-fcm-token", DeviceType.ANDROID);

            // when
            Set<ConstraintViolation<FcmTokenRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("다양한 디바이스 타입으로 요청 - 성공")
        void validRequestWithDifferentDeviceTypes_Success() {
            // given
            DeviceType[] deviceTypes = {DeviceType.WEB, DeviceType.ANDROID, DeviceType.IOS};

            for (DeviceType deviceType : deviceTypes) {
                // when
                FcmTokenRequest request = new FcmTokenRequest("valid-fcm-token", deviceType);
                Set<ConstraintViolation<FcmTokenRequest>> violations = validator.validate(request);

                // then
                assertThat(violations).isEmpty();
                assertThat(request.deviceType()).isEqualTo(deviceType);
            }
        }

        @Test
        @DisplayName("긴 FCM 토큰으로 요청 - 성공")
        void validRequestWithLongFcmToken_Success() {
            // given
            String longFcmToken = "very-long-fcm-token-".repeat(50);
            FcmTokenRequest request = new FcmTokenRequest(longFcmToken, DeviceType.IOS);

            // when
            Set<ConstraintViolation<FcmTokenRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
            assertThat(request.fcmToken()).isEqualTo(longFcmToken);
        }
    }

    @Nested
    @DisplayName("검증 실패 테스트")
    class ValidationFailureTest {

        @Test
        @DisplayName("FCM 토큰이 null인 경우 - 검증 실패")
        void fcmTokenNull_ValidationFailure() {
            // given
            FcmTokenRequest request = new FcmTokenRequest(null, DeviceType.ANDROID);

            // when
            Set<ConstraintViolation<FcmTokenRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("FCM 토큰은 필수입니다.");
        }

        @Test
        @DisplayName("FCM 토큰이 빈 문자열인 경우 - 검증 실패")
        void fcmTokenEmpty_ValidationFailure() {
            // given
            FcmTokenRequest request = new FcmTokenRequest("", DeviceType.ANDROID);

            // when
            Set<ConstraintViolation<FcmTokenRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("FCM 토큰은 필수입니다.");
        }

        @Test
        @DisplayName("FCM 토큰이 공백만 있는 경우 - 검증 실패")
        void fcmTokenBlank_ValidationFailure() {
            // given
            FcmTokenRequest request = new FcmTokenRequest("   ", DeviceType.ANDROID);

            // when
            Set<ConstraintViolation<FcmTokenRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("FCM 토큰은 필수입니다.");
        }

        @Test
        @DisplayName("디바이스 타입이 null인 경우 - 검증 실패")
        void deviceTypeNull_ValidationFailure() {
            // given
            FcmTokenRequest request = new FcmTokenRequest("valid-fcm-token", null);

            // when
            Set<ConstraintViolation<FcmTokenRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("디바이스 타입은 필수입니다.");
        }

        @Test
        @DisplayName("모든 필드가 null인 경우 - 검증 실패")
        void allFieldsNull_ValidationFailure() {
            // given
            FcmTokenRequest request = new FcmTokenRequest(null, null);

            // when
            Set<ConstraintViolation<FcmTokenRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(2);
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                    "FCM 토큰은 필수입니다.",
                    "디바이스 타입은 필수입니다."
                );
        }
    }

    @Nested
    @DisplayName("Record 특성 테스트")
    class RecordPropertiesTest {

        @Test
        @DisplayName("Record 불변성 테스트")
        void recordImmutability_Test() {
            // given
            String fcmToken = "test-fcm-token";
            DeviceType deviceType = DeviceType.ANDROID;
            FcmTokenRequest request = new FcmTokenRequest(fcmToken, deviceType);

            // when & then
            assertThat(request.fcmToken()).isEqualTo(fcmToken);
            assertThat(request.deviceType()).isEqualTo(deviceType);
        }

        @Test
        @DisplayName("Record equals와 hashCode 테스트")
        void recordEqualsAndHashCode_Test() {
            // given
            FcmTokenRequest request1 = new FcmTokenRequest("test-token", DeviceType.ANDROID);
            FcmTokenRequest request2 = new FcmTokenRequest("test-token", DeviceType.ANDROID);
            FcmTokenRequest request3 = new FcmTokenRequest("different-token", DeviceType.ANDROID);

            // when & then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1).isNotEqualTo(request3);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
            assertThat(request1.hashCode()).isNotEqualTo(request3.hashCode());
        }

        @Test
        @DisplayName("Record toString 테스트")
        void recordToString_Test() {
            // given
            FcmTokenRequest request = new FcmTokenRequest("test-token", DeviceType.ANDROID);

            // when
            String toString = request.toString();

            // then
            assertThat(toString).contains("test-token");
            assertThat(toString).contains("ANDROID");
            assertThat(toString).contains("FcmTokenRequest");
        }
    }
}