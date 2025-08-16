# API Documentation

## Settlement APIs

## 1. 전체 가게 정산 요약 조회

### Endpoint

```
GET /v1/owners/settlement/summary?year={year}&month={month}
```

### Request

**Query Parameters:**

- `year` (Integer): 년도 (2020-2100)
- `month` (Integer): 월 (1-12)

**Headers:**

- `Authorization: Bearer {JWT_TOKEN}` (OWNER 권한 필요)

### Response

**Success (200):**

```json
{
  "success": true,
  "status": 200,
  "data": {
    "period": "2024-01",
    "totalRevenue": 1500000,
    "totalOrderCount": 150,
    "avgOrderAmount": 10000,
    "storeCount": 3,
    "storeSettlements": [
      {
        "storeId": 1,
        "storeName": "string",
        "period": "2024-01",
        "totalRevenue": 500000,
        "orderCount": 50,
        "avgOrderAmount": 10000,
        "previousMonthRevenue": 450000,
        "revenueGrowthRate": 11,
        "previousMonthOrderCount": 45,
        "orderCountGrowthRate": 11
      }
    ]
  },
  "timestamp": "2024-01-01T09:00:00"
}
```

---

## 2. 특정 가게 정산 조회

### Endpoint

```
GET /v1/owners/stores/{storeId}/settlement?year={year}&month={month}
```

### Request

**Path Parameters:**

- `storeId` (Long): 가게 ID

**Query Parameters:**

- `year` (Integer): 년도 (2020-2100)
- `month` (Integer): 월 (1-12)

**Headers:**

- `Authorization: Bearer {JWT_TOKEN}` (OWNER 권한 필요)

### Response

**Success (200):**

```json
{
  "success": true,
  "status": 200,
  "data": {
    "storeId": 1,
    "storeName": "string",
    "period": "2024-01",
    "totalRevenue": 500000,
    "orderCount": 50,
    "avgOrderAmount": 10000,
    "previousMonthRevenue": 450000,
    "revenueGrowthRate": 11,
    "previousMonthOrderCount": 45,
    "orderCountGrowthRate": 11
  },
  "timestamp": "2024-01-01T09:00:00"
}
```

---

## 3. 특정 가게 월별 정산 리스트 조회

### Endpoint

```
GET /v1/owners/stores/{storeId}/settlement/monthly?startYear={startYear}&startMonth={startMonth}&endYear={endYear}&endMonth={endMonth}
```

### Request

**Path Parameters:**

- `storeId` (Long): 가게 ID

**Query Parameters:**

- `startYear` (Integer): 시작 년도 (2020-2100)
- `startMonth` (Integer): 시작 월 (1-12)
- `endYear` (Integer): 종료 년도 (2020-2100)
- `endMonth` (Integer): 종료 월 (1-12)

**Headers:**

- `Authorization: Bearer {JWT_TOKEN}` (OWNER 권한 필요)

### Response

**Success (200):**

```json
{
  "success": true,
  "status": 200,
  "data": {
    "storeId": 1,
    "storeName": "string",
    "monthlyData": [
      {
        "period": "2024-01",
        "totalRevenue": 500000,
        "orderCount": 50,
        "avgOrderAmount": 10000.0,
        "previousMonthRevenue": 450000,
        "previousMonthOrderCount": 45,
        "revenueGrowthRate": 11,
        "orderCountGrowthRate": 11
      }
    ],
    "startPeriod": "2024-01",
    "endPeriod": "2024-03",
    "totalMonths": 3,
    "totalRevenue": 1500000,
    "totalOrderCount": 150,
    "averageMonthlyRevenue": 500000.0,
    "overallGrowthRate": 15.5
  },
  "timestamp": "2024-01-01T09:00:00"
}
```

---

## Analytics APIs

## 4. 매출 분석 데이터 조회

### Endpoint

```
GET /api/analytics/sales-analytics?storeId={storeId}&startDate={startDate}&endDate={endDate}
```

### Request

**Query Parameters:**

- `storeId` (Long): 매장 ID (필수)
- `startDate` (String): 시작 날짜 (YYYY-MM-DD, 선택)
- `endDate` (String): 종료 날짜 (YYYY-MM-DD, 선택)

### Response

**Success (200):**

```json
{
  "success": true,
  "status": 200,
  "data": {
    "topSellingItems": [
      {
        "itemName": "string",
        "totalQuantity": 50,
        "percentage": 25.5
      }
    ],
    "topSellingProducts": [
      {
        "productName": "string",
        "totalQuantity": 30,
        "percentage": 45.2
      }
    ],
    "topDiscountRanges": [
      {
        "discountRange": "string",
        "count": 25,
        "percentage": 62.5
      }
    ],
    "salesPrediction": [
      {
        "date": "2024-02-01",
        "predictedRevenue": 120000.0,
        "confidence": 0.85
      }
    ],
    "inventoryPrediction": [
      {
        "date": "2024-02-01",
        "predictedDailyQuantity": 50,
        "predictedRemainingQuantity": 80,
        "inventoryRatio": 0.6,
        "confidence": 0.75
      }
    ],
    "totalRevenue": 1500000.0,
    "totalOrders": 45,
    "totalWeight": null
  },
  "timestamp": "2024-01-01T09:00:00"
}
```

---

## 5. 일별 매출 분석 데이터 조회

### Endpoint

```
GET /api/analytics/daily-analytics?storeId={storeId}&targetDate={targetDate}
```

### Request

**Query Parameters:**

- `storeId` (Long): 매장 ID (필수)
- `targetDate` (String): 분석 대상 날짜 (YYYY-MM-DD, 선택)

### Response

**Success (200):**

```json
{
  "success": true,
  "status": 200,
  "data": {
    "analysisDate": "2024-01-15",
    "storeId": 1,
    "salesOverview": {
      "totalSales": 150000,
      "totalOrderCount": 15,
      "averageOrderAmount": 10000
    },
    "topSellingDdipBoxes": [
      {
        "ddipBoxId": 1,
        "ddipBoxName": "string",
        "quantitySold": 10,
        "totalSalesAmount": 50000
      }
    ],
    "inventoryStatus": {
      "totalDailyCount": 100,
      "totalRemainingCount": 85,
      "remainingPercentage": 85.0
    },
    "highInventoryDdipBoxes": [
      {
        "ddipBoxId": 1,
        "ddipBoxName": "string",
        "remainingCount": 50,
        "dailyCount": 60
      }
    ],
    "profitMarginAnalysis": {
      "totalRevenue": 150000,
      "totalCost": 100000,
      "totalProfit": 50000,
      "profitMarginPercentage": 33.3,
      "profitByMarginRanges": [
        {
          "marginRange": "높은 수익 (30% 이상)",
          "salesAmount": 50000,
          "productCount": 5
        }
      ]
    }
  },
  "timestamp": "2024-01-01T09:00:00"
}
```

## Common Response Format

모든 API는 다음과 같은 공통 응답 구조를 사용합니다:

```json
{
  "success": boolean,
  "status": number,
  "data": object | array,
  "timestamp": "2024-01-01T10:00:00"
}
```

## Authentication

모든 API는 JWT 토큰을 통한 인증이 필요합니다:

- Header: `Authorization: Bearer {JWT_TOKEN}`
- JWT 토큰에는 `userId`, `role` 정보가 포함됨
- 각 API마다 필요한 권한(OWNER, CUSTOMER)이 다름

## Error Responses

```json
{
  "success": false,
  "status": 400|401|403|500,
  "code": "ERROR_CODE",
  "message": "Error message",
  "timestamp": "2024-01-01T10:00:00"
}
```
