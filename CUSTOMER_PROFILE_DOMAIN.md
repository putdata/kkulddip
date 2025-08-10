# Customer Profile 도메인 가이드

## 📋 개요

Customer Profile 도메인은 꿀딥(kkulddip) 서비스의 고객 프로필 관리를 담당하는 핵심 도메인입니다. 
고객의 개인정보, 위치 정보, 통계 정보, 그리고 실시간 위치 추적 기능을 제공합니다.

## 🏗️ 아키텍처

### 패키지 구조
```
customerProfile/
├── controller/              # REST API 컨트롤러
│   ├── CustomerProfileApi.java       # API 문서화 인터페이스
│   └── CustomerProfileController.java # 컨트롤러 구현
├── service/                 # 비즈니스 로직
│   └── CustomerProfileService.java
├── mapper/                  # DTO 변환
│   └── CustomerProfileMapper.java
├── dto/                     # 데이터 전송 객체
│   ├── request/
│   │   ├── UpdateProfileRequest.java
│   │   └── UpdateLocationRequest.java
│   └── response/
│       ├── CustomerProfileResponse.java
│       ├── CustomerStatsResponse.java
│       ├── UpdateProfileResponse.java
│       └── UpdateLocationResponse.java
└── location/                # 실시간 위치 관리
    ├── service/
    │   └── CustomerLocationService.java
    ├── cache/
    │   └── CustomerLocationCacheService.java
    └── dto/
        ├── CustomerLocationDto.java
        ├── LocationDistanceResponse.java
        └── UpdateRealtimeLocationRequest.java
```

### 기술 스택
- **Framework**: Spring Boot 3.5.3
- **Cache**: Redis (GeoSpatial 연산)
- **Database**: MySQL (JPA/Hibernate)
- **Authentication**: JWT 토큰
- **API Documentation**: SpringDoc OpenAPI (Swagger)

## 🔐 주요 기능

### 1. 프로필 관리
#### 🔍 프로필 조회 (`GET /v1/customers/profile`)
- 현재 로그인한 고객의 전체 프로필 정보 조회
- 마지막 활동 시간 자동 업데이트
- JWT 토큰을 통한 인증 필요

**응답 예시:**
```json
{
  "success": true,
  "status": 200,
  "body": {
    "customerId": 1,
    "email": "customer@example.com",
    "name": "김철수",
    "profileImageUrl": "https://example.com/profile.jpg",
    "address": "서울특별시 강남구 테헤란로 123",
    "latitude": 37.5665,
    "longitude": 126.9780,
    "level": "SPROUT_BEE",
    "createdAt": "2024-01-01T00:00:00",
    "lastActiveAt": "2024-01-01T12:00:00"
  }
}
```

#### ✏️ 프로필 수정 (`PUT /v1/customers/profile`)
- 고객 이름 및 프로필 이미지 URL 수정
- Bean Validation을 통한 입력값 검증
- 이름: 2~50자 사이 필수

**요청 예시:**
```json
{
  "name": "김철수",
  "profileImageUrl": "https://example.com/new-profile.jpg"
}
```

#### 📍 위치 정보 업데이트 (`PUT /v1/customers/location`)
- 고객의 주소, 위도, 경도 정보 수정
- 좌표 유효성 검증 (위도: -90~90, 경도: -180~180)
- 고객의 "집 주소" 개념으로 실시간 위치와 구분

**요청 예시:**
```json
{
  "address": "서울특별시 송파구 잠실동",
  "latitude": 37.5145,
  "longitude": 127.1058
}
```

### 2. 통계 및 레벨 시스템

#### 📊 고객 통계 조회 (`GET /v1/customers/stats`)
- 총 주문 수, 절약 금액, CO2 절약량
- 현재 레벨 및 다음 레벨 정보
- 레벨업까지 필요한 주문 수 계산

**응답 예시:**
```json
{
  "success": true,
  "status": 200,
  "body": {
    "customerId": 1,
    "level": "SPROUT_BEE",
    "totalOrder": 5,
    "totalMoneySaved": 15000,
    "totalCo2Saved": 2.5,
    "ordersUntilNextLevel": 5,
    "nextLevel": "WORKER_BEE"
  }
}
```

#### 🎯 고객 레벨 시스템
고객의 주문 횟수에 따라 자동으로 레벨이 승급됩니다:

| 레벨 | 이름 | 필요 주문 수 | 특징 |
|------|------|-------------|------|
| 🌱 SPROUT_BEE | 새싹벌 | 0-9 | 시작 레벨 |
| 🐝 WORKER_BEE | 일벌 | 10-29 | 적극적인 이용자 |
| 🍯 HONEY_BEE | 꿀벌 | 30-49 | 단골 고객 |
| 👑 QUEEN_BEE | 여왕벌 | 50+ | VIP 고객 |

### 3. 실시간 위치 추적 (Redis 기반)

#### 📍 실시간 위치 업데이트 (`POST /v1/customers/location/realtime`)
- Redis GeoSpatial 데이터 구조 사용
- 5분 TTL (Time To Live) 적용
- 최소 30초 업데이트 간격 제한
- 위치 공유 활성화 시에만 동작

**Redis 데이터 구조:**
- **Hash**: `customer:location:{customerId}` - 상세 정보 (JSON, 5분 TTL)
- **GeoSpatial**: `customer:locations:geo` - 거리 계산용

**요청 예시:**
```json
{
  "latitude": 37.5665,
  "longitude": 126.9780,
  "accuracy": 10.5,
  "deviceInfo": "iPhone 14 Pro"
}
```

#### 🔍 현재 위치 조회 (`GET /v1/customers/location/realtime`)
- 실시간 위치 정보 조회
- 위치 공유 비활성화 시 404 반환

#### 📏 가게까지 거리 계산 (`GET /v1/customers/location/distance/store/{storeId}`)
- Redis GeoSpatial DISTANCE 연산 사용
- 100m 이내 시 근처 도착으로 판정
- 예상 도착 시간: 거리(km) × 3분 (도보 기준)

**응답 예시:**
```json
{
  "success": true,
  "status": 200,
  "body": {
    "customerId": 1,
    "distance": 0.8,
    "estimatedTimeInMinutes": 3,
    "isNearby": false,
    "calculatedAt": "2024-01-01T12:00:00"
  }
}
```

#### 🛑 위치 공유 중지 (`DELETE /v1/customers/location/realtime`)
- 실시간 위치 정보 삭제
- Redis Hash 및 GeoSpatial 데이터 모두 삭제

## 🔧 성능 최적화

### Redis 캐시 전략
- **TTL 관리**: 실시간 위치 데이터 5분 자동 만료
- **업데이트 제한**: 30초 최소 간격으로 과도한 요청 방지
- **GeoSpatial 활용**: 효율적인 거리 계산 및 근처 고객 검색

### 데이터베이스 최적화
- **읽기 전용 트랜잭션**: 조회 성능 향상
- **JPA Auditing**: 자동 생성일/수정일 관리
- **Dynamic Insert**: null 값 제외한 INSERT 쿼리 생성

## 🔒 보안 및 검증

### 인증 및 권한
- **JWT 기반 인증**: Bearer 토큰을 통한 API 접근
- **사용자 격리**: 각 고객은 자신의 정보만 접근 가능
- **SecurityContext 활용**: 현재 로그인 사용자 정보 추출

### 입력 검증
- **Bean Validation**: 요청 DTO 필드별 검증 규칙
- **좌표 유효성**: 위도/경도 범위 검증
- **비즈니스 규칙**: 커스텀 검증 로직

## 📈 모니터링 및 로깅

### 로깅 전략
- **구조화된 로깅**: 주요 이벤트별 상세 로그
- **성능 모니터링**: 응답 시간 및 처리량 추적
- **에러 추적**: 예외 상황별 상세 로그

### 주요 로그 포인트
- 프로필 조회/수정 요청
- 위치 정보 업데이트
- 실시간 위치 추적 이벤트
- 레벨업 이벤트
- Redis 캐시 히트/미스

## 🚀 향후 개선 계획

### 기능 확장
- [ ] 고객 선호도 분석
- [ ] 위치 기반 맞춤 추천
- [ ] 고객 그룹 관리
- [ ] 푸시 알림 연동

### 성능 개선
- [ ] Redis Cluster 도입
- [ ] 캐시 워밍 전략
- [ ] 비동기 처리 확장
- [ ] 메트릭 대시보드

## 📞 문의 및 지원

Customer Profile 도메인 관련 문의사항이나 개선 제안이 있으시면 개발팀에 연락해주세요.

---

**최종 업데이트**: 2024년 8월
**작성자**: kkulddip 개발팀