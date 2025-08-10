# Store Management Domain

가게 및 띱박스 관리를 위한 CUD(Create, Update, Delete) 기능을 제공하는 도메인입니다.

## 📁 패키지 구조

```
com.kkulddip.storeManagement/
├── config/                           # 설정 클래스
│   └── StoreManagementConfig.java
├── controller/                       # REST API 컨트롤러
│   ├── StoreManagementApi.java       # 가게 관리 API 인터페이스
│   ├── StoreManagementController.java # 가게 관리 컨트롤러
│   ├── DdipBoxManagementApi.java     # 띱박스 관리 API 인터페이스
│   └── DdipBoxManagementController.java # 띱박스 관리 컨트롤러
├── dto/                              # 데이터 전송 객체
│   ├── request/                      # 요청 DTO
│   │   ├── CreateStoreRequest.java
│   │   ├── UpdateStoreRequest.java
│   │   ├── UpdateStoreStatusRequest.java
│   │   ├── CreateDdipBoxRequest.java
│   │   ├── UpdateDdipBoxRequest.java
│   │   ├── UpdateDdipBoxQuantityRequest.java
│   │   ├── CreateDdipBoxItemRequest.java
│   │   └── UpdateDdipBoxItemRequest.java
│   └── response/                     # 응답 DTO
│       ├── StoreManagementResponse.java
│       ├── DdipBoxManagementResponse.java
│       └── DdipBoxItemManagementResponse.java
├── exception/                        # 예외 처리
│   ├── StoreManagementException.java
│   └── StoreManagementExceptionHandler.java
├── service/                          # 비즈니스 로직 서비스
│   ├── StoreManagementService.java
│   └── DdipBoxManagementService.java
├── util/                            # 유틸리티 클래스
│   └── OwnerExtractor.java          # 사장님 인증 정보 추출
└── validator/                       # 유효성 검증
    └── StoreManagementValidator.java
```

## 🔧 주요 기능

### Store 관리
- **POST** `/v1/store-management/stores` - 가게 생성
- **PUT** `/v1/store-management/stores/{storeId}` - 가게 정보 수정
- **PATCH** `/v1/store-management/stores/{storeId}/status` - 가게 활성화/비활성화
- **DELETE** `/v1/store-management/stores/{storeId}` - 가게 삭제 (논리적 삭제)

### DdipBox 관리
- **POST** `/v1/store-management/stores/{storeId}/ddipboxes` - 띱박스 생성
- **PUT** `/v1/store-management/stores/{storeId}/ddipboxes/{ddipboxId}` - 띱박스 수정
- **PATCH** `/v1/store-management/stores/{storeId}/ddipboxes/{ddipboxId}/quantity` - 재고 수량 업데이트
- **PATCH** `/v1/store-management/stores/{storeId}/ddipboxes/{ddipboxId}/status` - 띱박스 활성화/비활성화
- **DELETE** `/v1/store-management/stores/{storeId}/ddipboxes/{ddipboxId}` - 띱박스 삭제
- **GET** `/v1/store-management/stores/{storeId}/ddipboxes` - 가게의 모든 띱박스 조회

## 🔐 보안

- **JWT 기반 인증**: 모든 API는 JWT 토큰 인증 필요
- **사장님 권한 필요**: `@PreAuthorize("hasRole('OWNER')")` 적용
- **소유권 검증**: 각 API에서 해당 가게의 실제 소유자인지 확인
- **데이터 검증**: Bean Validation 및 커스텀 Validator 적용

## 📝 사용 예시

### 가게 생성
```bash
POST /v1/store-management/stores
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "storeName": "꿀떨잎 친환경 가게",
  "phone": "02-1234-5678",
  "description": "신선한 친환경 상품을 판매합니다",
  "operatingHours": "09:00-18:00",
  "storeAddress": "서울시 강남구 테헤란로 123",
  "latitude": 37.5665,
  "longitude": 126.9780
}
```

### 띱박스 생성
```bash
POST /v1/store-management/stores/1/ddipboxes
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "ddipboxName": "오늘의 신선 채소박스",
  "description": "당일 수확한 신선한 채소들",
  "category": "채소",
  "originalPrice": 15000,
  "salePrice": 10000,
  "dailyQuantity": 20,
  "maxPerCustomer": 2
}
```

### 재고 수량 업데이트
```bash
PATCH /v1/store-management/stores/1/ddipboxes/1/quantity
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "remainingQuantity": 15
}
```

## ⚠️ 주의사항

1. **Store 완전 삭제 금지**: 데이터 무결성과 법적 요구사항으로 인해 물리적 삭제는 불가능하며, 논리적 삭제(isActive=false)만 지원
2. **활성 주문 확인**: 가게나 띱박스를 비활성화하기 전에 활성 주문 존재 여부 확인
3. **가격 검증**: 판매가는 정가보다 높을 수 없으며, 할인율 90% 초과 불가
4. **수량 제한**: 고객당 최대 구매 수량은 일일 수량을 초과할 수 없음
5. **JWT 토큰**: 실제 운영환경에서는 OwnerExtractor.getCurrentOwnerId() 메서드를 통해 JWT에서 사장님 ID를 추출해야 함

## 🔄 확장 계획

- DdipBoxItem 관리 기능 (현재 DTO만 구현)
- 가게 이미지 업로드 기능
- 배치 작업을 통한 일일 재고 자동 리셋
- 가게 운영 통계 및 분석 기능
- 알림 시스템 연동 (가게 생성/수정 시 알림)