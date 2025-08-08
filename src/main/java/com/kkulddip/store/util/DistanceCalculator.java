package com.kkulddip.store.util;

/**
 * 거리 계산 유틸리티 클래스
 * Haversine 공식을 사용하여 두 지점 간의 거리를 계산
 */
public final class DistanceCalculator {

    private static final int EARTH_RADIUS_KM = 6371;

    private DistanceCalculator() {
        // Utility class - prevent instantiation
    }

    /**
     * 두 지점 간의 거리를 계산합니다 (Haversine 공식 사용)
     *
     * @param userLat 사용자 위도
     * @param userLng 사용자 경도
     * @param storeLat 가게 위도
     * @param storeLng 가게 경도
     * @return 거리 (km), 좌표가 null이면 null 반환
     */
    public static Double calculateDistance(Double userLat, Double userLng, Double storeLat, Double storeLng) {
        if (userLat == null || userLng == null || storeLat == null || storeLng == null) {
            return null;
        }

        double latDistance = Math.toRadians(storeLat - userLat);
        double lngDistance = Math.toRadians(storeLng - userLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(storeLat))
            * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * 좌표가 유효한지 확인합니다
     *
     * @param latitude 위도
     * @param longitude 경도
     * @return 유효하면 true, 아니면 false
     */
    public static boolean isValidCoordinate(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }

        return latitude >= -90.0 && latitude <= 90.0
            && longitude >= -180.0 && longitude <= 180.0;
    }
}