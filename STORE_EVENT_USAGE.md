# Store 도메인 이벤트 시스템 사용 가이드

## 개요

Store 도메인의 이벤트 시스템을 통해 다른 도메인에서 Store의 DdipBox 데이터를 비동기로 요청하고 응답받을 수 있습니다.

## 아키텍처

```
[요청 도메인] --DdipBoxDataRequestEvent--> [Store EventListener]
                                                    |
                                                    v
                                            [데이터 조회 로직]
                                                    |
                                                    v
[요청 도메인] <--DdipBoxDataResponseEvent-- [Store EventListener]
```

## 구현된 컴포넌트

### 1. 이벤트 클래스
- `DdipBoxDataRequestEvent`: DdipBox 데이터 요청 이벤트
- `DdipBoxDataResponseEvent`: DdipBox 데이터 응답 이벤트

### 2. 이벤트 처리
- `StoreEventListener`: Store 도메인의 이벤트 수신 및 처리
- `StoreEventService`: Store 도메인에서 다른 도메인으로 이벤트 발송 (필요시)

### 3. 설정
- `StoreEventConfig`: 이벤트 처리용 비동기 스레드 풀 설정

## 사용 방법

### 다른 도메인에서 Store 데이터 요청하기

#### 1. 요청 이벤트 발송

```java
@Service
public class OrderService {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public void processOrder(Long storeId, List<Long> ddipBoxIds) {
        // Store 도메인에 DdipBox 데이터 요청
        String txId = "ORDER_REQ_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        
        DdipBoxDataRequestEvent event = DdipBoxDataRequestEvent.builder()
            .txId(txId)                     // 고유한 트랜잭션 ID
            .storeId(storeId)               // 대상 가게 ID
            .ddipBoxIds(ddipBoxIds)         // 요청할 DdipBox ID 목록 (null이면 전체)
            .requestingDomain("ORDER")      // 요청하는 도메인 이름
            .requestTimestamp(System.currentTimeMillis())  // 요청 시각
            .build();
        
        eventPublisher.publishEvent(event);
        
        // 비동기로 처리되므로 응답은 별도 EventListener에서 수신
    }
}
```

#### 2. 응답 이벤트 수신

```java
@Component
public class OrderEventListener {
    
    @EventListener
    @Async("orderEventTaskExecutor") // 각 도메인별 스레드 풀 사용 권장
    public void handleDdipBoxDataResponse(DdipBoxDataResponseEvent event) {
        // 자신의 도메인 응답만 처리
        if (!"ORDER".equals(event.targetDomain())) {
            return;
        }
        
        log.info("DdipBox 데이터 응답 수신 - txId: {}, storeId: {}", 
                event.txId(), event.storeId());
        
        if (event.success()) {
            List<DdipBox> ddipBoxes = event.ddipBoxes();
            
            // 주문 로직에서 DdipBox 데이터 활용
            processOrderWithDdipBoxes(event.storeId(), ddipBoxes);
            
            log.info("DdipBox 데이터 처리 완료 - 조회된 데이터 수: {}", ddipBoxes.size());
        } else {
            log.error("DdipBox 데이터 요청 실패: {}", event.errorMessage());
            handleDataRequestFailure(event.txId(), event.errorMessage());
        }
    }
    
    private void processOrderWithDdipBoxes(Long storeId, List<DdipBox> ddipBoxes) {
        // DdipBox 데이터를 활용한 주문 로직
        for (DdipBox ddipBox : ddipBoxes) {
            log.info("처리 중인 DdipBox: {} - {}", ddipBox.getDdipboxId(), ddipBox.getName());
            // 주문 상품 검증, 가격 계산 등
        }
    }
    
    private void handleDataRequestFailure(String txId, String errorMessage) {
        // 데이터 요청 실패 처리 로직
        log.error("DdipBox 데이터 요청 실패 처리 - txId: {}, error: {}", txId, errorMessage);
    }
}
```

### 각 도메인별 설정

#### 이벤트 처리용 비동기 설정 (권장)

```java
@Configuration
@EnableAsync
public class OrderEventConfig {
    
    @Bean("orderEventTaskExecutor")
    public TaskExecutor orderEventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("order-event-");
        executor.initialize();
        return executor;
    }
}
```

## 이벤트 필드 설명

### DdipBoxDataRequestEvent
- `txId`: 고유한 트랜잭션 ID (응답 매칭용)
- `storeId`: 대상 가게 ID
- `ddipBoxIds`: 요청할 DdipBox ID 목록 (null이면 해당 가게의 모든 활성화된 DdipBox)
- `requestingDomain`: 요청하는 도메인 이름
- `requestTimestamp`: 요청 시각

### DdipBoxDataResponseEvent
- `txId`: 원본 트랜잭션 ID
- `storeId`: 대상 가게 ID
- `ddipBoxes`: 조회된 DdipBox 엔티티 목록
- `targetDomain`: 응답을 받을 도메인
- `success`: 요청 처리 성공 여부
- `errorMessage`: 오류 메시지 (실패 시)
- `responseTimestamp`: 응답 시각

## 주의사항

1. **도메인 필터링**: 응답 이벤트 수신 시 `targetDomain`을 확인하여 자신의 도메인 응답만 처리
2. **비동기 처리**: 모든 이벤트는 비동기로 처리되므로 즉시 응답을 기대하지 말 것
3. **오류 처리**: 응답 이벤트의 `success` 필드를 확인하여 성공/실패 분기 처리
4. **스레드 풀**: 각 도메인별로 별도의 스레드 풀 사용 권장
5. **트랜잭션 ID**: 고유한 트랜잭션 ID를 생성하여 요청-응답 매칭에 활용

## 성능 고려사항

- Store 도메인은 성능상 이유로 @OneToMany 관계를 사용하지 않음
- DdipBox 조회 시 별도 Repository 쿼리로 데이터 조회
- 비동기 처리로 메인 비즈니스 로직에 영향 최소화
- 스레드 풀 크기는 시스템 부하에 따라 조정 가능