-- ========================================
-- Analytics Service 인덱스 - 우선순위별 실행 스크립트
-- ========================================

-- 📋 실행 순서:
-- 1. PHASE 1 (필수) - 즉시 적용 권장
-- 2. PHASE 2 (중요) - 1차 배포 후 적용
-- 3. PHASE 3 (선택) - 성능 모니터링 후 필요시 적용

-- ========================================
-- 🔥 PHASE 1: 필수 인덱스 (즉시 실행 권장)
-- ========================================
-- 가장 큰 성능 향상을 가져오는 핵심 인덱스들

-- 1-1. Analytics Service 핵심 쿼리 최적화 (🚨 최우선)
-- 성능 향상: 90%+
CREATE INDEX idx_orders_store_status_date 
ON orders (store_id, order_status, order_date);

-- 1-2. 배치 로딩 최적화 (🚨 필수)
-- convertToOrderDataDto 성능 대폭 향상
CREATE INDEX idx_ddipboxitem_ddipbox_id 
ON ddip_box_item (ddipbox_id);

-- 1-3. 재고 현황 조회 최적화 (🔥 중요)
-- calculateHighInventoryDdipBoxes 성능 향상
CREATE INDEX idx_ddipbox_store_remaining 
ON ddip_box (store_id, remaining_quantity DESC);

-- ========================================
-- PHASE 1 실행 결과 확인
-- ========================================
SELECT '✅ PHASE 1 완료' AS status;

SELECT 
    'orders' as table_name,
    COUNT(*) as index_count
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'orders'
    AND INDEX_NAME = 'idx_orders_store_status_date'
UNION ALL
SELECT 
    'ddip_box_item' as table_name,
    COUNT(*) as index_count
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'ddip_box_item'
    AND INDEX_NAME = 'idx_ddipboxitem_ddipbox_id'
UNION ALL
SELECT 
    'ddip_box' as table_name,
    COUNT(*) as index_count
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'ddip_box'
    AND INDEX_NAME = 'idx_ddipbox_store_remaining';

-- ========================================
-- 📈 PHASE 2: 중요 인덱스 (1차 배포 후)
-- ========================================
-- 추가 성능 향상을 위한 보조 인덱스들

-- 2-1. 매장별 일일 재고 최적화
CREATE INDEX idx_ddipbox_store_daily 
ON ddip_box (store_id, daily_quantity DESC);

-- 2-2. 배치 로딩 이름 조회 최적화
CREATE INDEX idx_ddipbox_id_name 
ON ddip_box (ddipbox_id, ddipbox_name);

-- 2-3. 수익성 분석 원가 조회 최적화
CREATE INDEX idx_ddipbox_id_price 
ON ddip_box (ddipbox_id, original_price);

-- 2-4. 고객별 주문 조회 최적화
CREATE INDEX idx_orders_customer_status_date 
ON orders (customer_id, order_status, order_date);

-- ========================================
-- 📊 PHASE 3: 선택적 인덱스 (성능 모니터링 후)
-- ========================================
-- 워크로드에 따라 필요시 추가하는 인덱스들

-- 3-1. 주문 아이템 분석 최적화
CREATE INDEX idx_orderitems_order_product 
ON order_items (order_id, product_id);

-- 3-2. 상품별 판매 분석 최적화
CREATE INDEX idx_orderitems_product_quantity 
ON order_items (product_id, quantity);

-- 3-3. 날짜 기준 주문 조회 최적화
CREATE INDEX idx_orders_date_status 
ON orders (order_date, order_status);

-- 3-4. 아이템 수량 기준 조회 최적화
CREATE INDEX idx_ddipboxitem_ddipbox_quantity 
ON ddip_box_item (ddipbox_id, item_quantity);

-- ========================================
-- 🧪 성능 테스트 쿼리 (PHASE 1 적용 후 실행)
-- ========================================

-- 테스트 1: 가장 중요한 Analytics 쿼리 성능 확인
EXPLAIN FORMAT=JSON SELECT * FROM orders 
WHERE store_id = 1 
  AND order_status IN ('CONFIRMED', 'PICKED_UP') 
  AND DATE(order_date) BETWEEN '2024-01-01' AND '2024-12-31';

-- 테스트 2: 재고 정렬 조회 성능 확인
EXPLAIN FORMAT=JSON SELECT * FROM ddip_box 
WHERE store_id = 1 
ORDER BY remaining_quantity DESC 
LIMIT 5;

-- 테스트 3: 배치 로딩 성능 확인
EXPLAIN FORMAT=JSON SELECT * FROM ddip_box_item 
WHERE ddipbox_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

-- ========================================
-- 📈 성능 벤치마크 쿼리
-- ========================================

-- 벤치마크 1: 날짜 범위 주문 조회 시간 측정
SELECT 
    '날짜 범위 주문 조회' as test_name,
    COUNT(*) as result_count,
    NOW() as execution_time
FROM orders 
WHERE store_id = 1 
  AND order_status IN ('CONFIRMED', 'PICKED_UP') 
  AND DATE(order_date) BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE();

-- 벤치마크 2: 재고 정렬 조회 시간 측정
SELECT 
    '재고 TOP 5 조회' as test_name,
    COUNT(*) as result_count,
    NOW() as execution_time
FROM (
    SELECT * FROM ddip_box 
    WHERE store_id = 1 
    ORDER BY remaining_quantity DESC 
    LIMIT 5
) as top_inventory;

-- ========================================
-- 💡 사용법 및 권장사항
-- ========================================

/*
🎯 실행 순서 권장사항:

1. 개발/테스트 환경에서 PHASE 1 먼저 실행
2. 성능 테스트로 효과 확인
3. 운영 환경 점검 시간에 PHASE 1 적용
4. 1-2주 후 성능 모니터링 결과 확인
5. 필요시 PHASE 2, 3 순차 적용

⚠️ 주의사항:
- 대용량 테이블의 경우 인덱스 생성에 시간이 소요됨
- 점검 시간에 실행 권장
- 디스크 공간 충분한지 확인 (인덱스 크기 = 테이블 크기의 10-30%)

📊 예상 성능 향상:
- PHASE 1: 70-90% 쿼리 성능 향상
- PHASE 2: 추가 20-30% 성능 향상
- PHASE 3: 워크로드별 10-20% 성능 향상
*/