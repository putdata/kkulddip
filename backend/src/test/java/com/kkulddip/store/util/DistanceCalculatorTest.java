package com.kkulddip.store.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DistanceCalculator 테스트")
@ActiveProfiles("citest")
class DistanceCalculatorTest {

    @Test
    @DisplayName("서울역과 강남역 사이의 거리를 정확히 계산한다")
    void calculateDistance_Seoul_Gangnam() {
        // given
        Double seoulStationLat = 37.5546;
        Double seoulStationLng = 126.9706;
        Double gangnamStationLat = 37.4979;
        Double gangnamStationLng = 127.0276;

        // when
        Double distance = DistanceCalculator.calculateDistance(
                seoulStationLat, seoulStationLng, 
                gangnamStationLat, gangnamStationLng
        );

        // then
        assertThat(distance).isNotNull();
        assertThat(distance).isBetween(8.0, 10.0); // 실제 거리는 약 8.7km
    }

    @Test
    @DisplayName("동일한 위치의 거리는 0이다")
    void calculateDistance_SameLocation() {
        // given
        Double lat = 37.5665;
        Double lng = 126.9780;

        // when
        Double distance = DistanceCalculator.calculateDistance(lat, lng, lat, lng);

        // then
        assertThat(distance).isEqualTo(0.0);
    }

    @Test
    @DisplayName("null 좌표가 포함된 경우 null을 반환한다")
    void calculateDistance_WithNullCoordinates() {
        // given
        Double validLat = 37.5665;
        Double validLng = 126.9780;

        // when & then
        assertThat(DistanceCalculator.calculateDistance(null, validLng, validLat, validLng))
                .isNull();
        assertThat(DistanceCalculator.calculateDistance(validLat, null, validLat, validLng))
                .isNull();
        assertThat(DistanceCalculator.calculateDistance(validLat, validLng, null, validLng))
                .isNull();
        assertThat(DistanceCalculator.calculateDistance(validLat, validLng, validLat, null))
                .isNull();
    }

    @Test
    @DisplayName("모든 좌표가 null인 경우 null을 반환한다")
    void calculateDistance_AllNullCoordinates() {
        // when
        Double distance = DistanceCalculator.calculateDistance(null, null, null, null);

        // then
        assertThat(distance).isNull();
    }

    @Test
    @DisplayName("유효한 좌표인지 확인한다")
    void isValidCoordinate_ValidCoordinates() {
        // given & when & then
        assertThat(DistanceCalculator.isValidCoordinate(37.5665, 126.9780)).isTrue();
        assertThat(DistanceCalculator.isValidCoordinate(0.0, 0.0)).isTrue();
        assertThat(DistanceCalculator.isValidCoordinate(90.0, 180.0)).isTrue();
        assertThat(DistanceCalculator.isValidCoordinate(-90.0, -180.0)).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 좌표인지 확인한다")
    void isValidCoordinate_InvalidCoordinates() {
        // given & when & then
        assertThat(DistanceCalculator.isValidCoordinate(null, 126.9780)).isFalse();
        assertThat(DistanceCalculator.isValidCoordinate(37.5665, null)).isFalse();
        assertThat(DistanceCalculator.isValidCoordinate(null, null)).isFalse();
        assertThat(DistanceCalculator.isValidCoordinate(91.0, 126.9780)).isFalse();
        assertThat(DistanceCalculator.isValidCoordinate(-91.0, 126.9780)).isFalse();
        assertThat(DistanceCalculator.isValidCoordinate(37.5665, 181.0)).isFalse();
        assertThat(DistanceCalculator.isValidCoordinate(37.5665, -181.0)).isFalse();
    }

    @Test
    @DisplayName("극지방 좌표도 정확히 계산한다")
    void calculateDistance_PolarCoordinates() {
        // given
        Double northPoleLat = 89.0;
        Double northPoleLng = 0.0;
        Double southPoleLat = -89.0;
        Double southPoleLng = 0.0;

        // when
        Double distance = DistanceCalculator.calculateDistance(
                northPoleLat, northPoleLng, 
                southPoleLat, southPoleLng
        );

        // then
        assertThat(distance).isNotNull();
        assertThat(distance).isGreaterThan(19000.0); // 극지방 간 거리는 매우 멀다
    }

    @Test
    @DisplayName("경계값 좌표를 정확히 처리한다")
    void calculateDistance_BoundaryValues() {
        // given
        Double maxLat = 90.0;
        Double maxLng = 180.0;
        Double minLat = -90.0;
        Double minLng = -180.0;

        // when
        Double distance1 = DistanceCalculator.calculateDistance(maxLat, maxLng, minLat, minLng);
        Double distance2 = DistanceCalculator.calculateDistance(0.0, 0.0, maxLat, maxLng);

        // then
        assertThat(distance1).isNotNull();
        assertThat(distance2).isNotNull();
        assertThat(distance1).isGreaterThan(0.0);
        assertThat(distance2).isGreaterThan(0.0);
    }
}