# Store Management API Documentation

## Overview
Store Management API는 가게 사장(OWNER)이 자신의 가게와 띱박스를 관리할 수 있는 REST API입니다.

## Base URL
```
/v1/store-management/stores
```

## Authentication & Authorization
- **인증**: JWT Bearer Token 필요
- **권한**: `ROLE_OWNER` 권한 필요 (모든 엔드포인트)
- **소유권 검증**: 사장은 자신이 소유한 가게만 관리 가능

---

## Store Management APIs

### 1. Create Store
가게를 생성합니다.

**Endpoint**: `POST /v1/store-management/stores`

**Request Body**: `CreateStoreRequest`
```json
{
  "storeName": "string",        // 필수, 최대 255자
  "phone": "string",            // 선택, 최대 15자
  "description": "string",      // 선택, 최대 1000자
  "operatingHours": "string",   // 선택, 최대 100자
  "businessNumber": "string",   // 선택, 최대 100자
  "storeAddress": "string",     // 필수, 최대 100자
  "latitude": "double",         // 필수, -90.0 ~ 90.0
  "longitude": "double"         // 필수, -180.0 ~ 180.0
}
```

**Response**: `StoreManagementResponse` (201 Created)

**제약사항**:
- storeName, storeAddress, latitude, longitude는 필수
- 위경도는 유효한 범위 내 값이어야 함
- 문자열 필드는 자동으로 trim 처리

### 2. Create Store with Image
이미지와 함께 가게를 생성합니다.

**Endpoint**: `POST /v1/store-management/stores/with-image`

**Content-Type**: `multipart/form-data`

**Request Parts**:
- `request`: `CreateStoreRequest` (JSON)
- `image`: `MultipartFile` (선택)

**Response**: `StoreManagementResponse` (201 Created)

### 3. Update Store
가게 정보를 수정합니다.

**Endpoint**: `PUT /v1/store-management/stores/{storeId}`

**Request Body**: `UpdateStoreRequest`
```json
{
  "storeName": "string",        // 선택, 최대 255자
  "phone": "string",            // 선택, 최대 15자
  "description": "string",      // 선택, 최대 1000자
  "operatingHours": "string",   // 선택, 최대 100자
  "storeAddress": "string",     // 선택, 최대 100자
  "latitude": "double",         // 선택, -90.0 ~ 90.0
  "longitude": "double"         // 선택, -180.0 ~ 180.0
}
```

**Response**: `StoreManagementResponse`

**제약사항**:
- 모든 필드가 선택사항 (null이면 업데이트하지 않음)
- hasUpdates() 메서드로 업데이트 가능 여부 확인

### 4. Update Store Status
가게 활성화/비활성화 상태를 변경합니다.

**Endpoint**: `PATCH /v1/store-management/stores/{storeId}/status`

**Request Body**: `UpdateStoreStatusRequest`
```json
{
  "isActive": "boolean",        // 필수
  "reason": "string"            // 선택
}
```

**Response**: `StoreManagementResponse`

### 5. Update Store Image
가게 이미지를 업데이트합니다.

**Endpoint**: `PUT /v1/store-management/stores/{storeId}/image`

**Content-Type**: `multipart/form-data`

**Request Parts**:
- `image`: `MultipartFile` (선택, null이면 이미지 삭제)

**Response**: `StoreManagementResponse`

### 6. Delete Store
가게를 삭제합니다.

**Endpoint**: `DELETE /v1/store-management/stores/{storeId}`

**Response**: `204 No Content`

---

## DdipBox Management APIs

### 1. Create DdipBox
띱박스를 생성합니다.

**Endpoint**: `POST /v1/store-management/stores/{storeId}/ddipboxes`

**Request Body**: `CreateDdipBoxRequest`
```json
{
  "ddipboxName": "string",      // 필수, 최대 100자
  "description": "string",      // 선택, 최대 1000자
  "category": "string",         // 필수, 최대 50자
  "originalPrice": "long",      // 필수, 최소 0
  "salePrice": "long",          // 필수, 최소 0
  "dailyQuantity": "long",      // 필수, 최소 1
  "maxPerCustomer": "long"      // 필수, 최소 1
}
```

**Response**: `DdipBoxManagementResponse` (201 Created)

**제약사항**:
- salePrice ≤ originalPrice (판매가는 정가보다 높을 수 없음)
- maxPerCustomer ≤ dailyQuantity (고객당 최대 구매 수량은 일일 수량보다 많을 수 없음)

### 2. Update DdipBox
띱박스 정보를 수정합니다.

**Endpoint**: `PUT /v1/store-management/stores/{storeId}/ddipboxes/{ddipboxId}`

**Request Body**: `UpdateDdipBoxRequest`
```json
{
  "ddipboxName": "string",      // 선택, 최대 100자
  "description": "string",      // 선택, 최대 1000자
  "category": "string",         // 선택, 최대 50자
  "originalPrice": "long",      // 선택, 최소 0
  "salePrice": "long",          // 선택, 최소 0
  "dailyQuantity": "long",      // 선택, 최소 1
  "maxPerCustomer": "long"      // 선택, 최소 1
}
```

**Response**: `DdipBoxManagementResponse`

**제약사항**:
- hasUpdates() 메서드로 업데이트 가능 여부 확인

### 3. Update DdipBox Quantity
띱박스 재고 수량을 업데이트합니다.

**Endpoint**: `PATCH /v1/store-management/stores/{storeId}/ddipboxes/{ddipboxId}/quantity`

**Request Body**: `UpdateDdipBoxQuantityRequest`
```json
{
  "remainingQuantity": "long",  // 선택, 최소 0
  "dailyQuantity": "long",      // 선택, 최소 1
  "resetRemaining": "boolean"   // 선택, 기본값 false
}
```

**Response**: `DdipBoxManagementResponse`

**제약사항**:
- **직접 재고 수정**: remainingQuantity 지정 + resetRemaining=false
- **일일 수량으로 재고 리셋**: dailyQuantity 지정 + resetRemaining=true
- hasValidOperation() 메서드로 유효한 작업 확인

### 4. Update DdipBox Status
띱박스 활성화/비활성화 상태를 변경합니다.

**Endpoint**: `PATCH /v1/store-management/stores/{storeId}/ddipboxes/{ddipboxId}/status`

**Request Body**: `UpdateStoreStatusRequest`
```json
{
  "isActive": "boolean",        // 필수
  "reason": "string"            // 선택
}
```

**Response**: `DdipBoxManagementResponse`

### 5. Delete DdipBox
띱박스를 삭제합니다.

**Endpoint**: `DELETE /v1/store-management/stores/{storeId}/ddipboxes/{ddipboxId}`

**Response**: `204 No Content`

### 6. Get DdipBoxes by Store
가게의 모든 띱박스 목록을 조회합니다.

**Endpoint**: `GET /v1/store-management/stores/{storeId}/ddipboxes`

**Response**: `List<DdipBoxManagementResponse>`

---

## Response DTOs

### StoreManagementResponse
```json
{
  "storeId": "long",
  "ownerId": "long",
  "storeName": "string",
  "phone": "string",
  "description": "string",
  "operatingHours": "string",
  "isActive": "boolean",
  "ratingAverage": "double",
  "reviewCount": "long",
  "businessNumber": "string",
  "storeAddress": "string",
  "storeProfileImage": "string",
  "latitude": "double",
  "longitude": "double",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### DdipBoxManagementResponse
```json
{
  "ddipboxId": "long",
  "storeId": "long",
  "ddipboxName": "string",
  "description": "string",
  "category": "string",
  "originalPrice": "long",
  "salePrice": "long",
  "dailyQuantity": "long",
  "remainingQuantity": "long",
  "maxPerCustomer": "long",
  "isActive": "boolean"
}
```

---

## Error Handling

### Common HTTP Status Codes
- `401 Unauthorized`: JWT 토큰 없음 또는 유효하지 않음
- `403 Forbidden`: OWNER 권한 없음 또는 소유권 없음
- `404 Not Found`: 가게 또는 띱박스를 찾을 수 없음
- `400 Bad Request`: 유효성 검증 실패
- `500 Internal Server Error`: 서버 내부 오류

### Validation Rules Summary
- **문자열 필드**: 자동 trim 처리
- **가격 필드**: 0원 이상
- **수량 필드**: 1개 이상
- **좌표 필드**: 유효한 위경도 범위
- **비즈니스 로직**: 판매가 ≤ 정가, 고객당 최대 구매량 ≤ 일일 수량