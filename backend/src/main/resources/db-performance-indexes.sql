-- ========================================
-- Analytics Service Performance Indexes
-- ========================================
-- 이 스크립트는 Analytics Service 최적화를 위한 인덱스들을 생성합니다.
-- 실행 전 반드시 데이터베이스 백업을 권장합니다.

-- 현재 인덱스 확인 (실행 전 참고용)
-- SHOW INDEX FROM orders;
-- SHOW INDEX FROM ddip_box;  
-- SHOW INDEX FROM ddip_box_item;

-- ========================================
-- 1. Orders 테이블 인덱스 (최우선 - 필수)
-- ========================================

-- 🔥 가장 중요한 복합 인덱스 - Analytics Service 핵심 쿼리 최적화
-- findByStoreIdAndOrderStatusInAndOrderDate, findByStoreIdAndOrderStatusInAndOrderDateBetween 쿼리 최적화
-- WHERE store_id = ? AND order_status IN ('CONFIRMED', 'PICKED_UP') AND DATE(order_date) BETWEEN ? AND ?
CREATE INDEX idx_orders_store_status_date 
ON orders (store_id, order_status, order_date);

-- 고객별 주문 상태 조회 최적화 (선택적)
-- findByCustomerIdAndOrderStatus 쿼리 최적화  
CREATE INDEX idx_orders_customer_status_date 
ON orders (customer_id, order_status, order_date);

-- 주문 날짜 범위 조회 성능 향상 (보조 인덱스)
CREATE INDEX idx_orders_date_status 
ON orders (order_date, order_status);

-- ========================================
-- 2. DdipBox 테이블 인덱스 (중요)
-- ========================================

-- 매장별 재고 현황 조회 최적화 (필수)
-- calculateInventoryStatus, calculateHighInventoryDdipBoxes 최적화
-- DdipBox 엔티티에서 store는 @ManyToOne 관계이므로 store_id 컬럼 확인 필요
-- 실제로는 JOIN을 통해 Store.storeId를 참조하므로 다음과 같이 수정
CREATE INDEX idx_ddipbox_store_remaining 
ON ddip_box (store_id, remaining_quantity DESC);

-- 매장별 일일 재고량 조회 최적화
CREATE INDEX idx_ddipbox_store_daily 
ON ddip_box (store_id, daily_quantity DESC);

-- 배치 로딩 최적화 (ID + Name) - findAllById 쿼리 최적화
-- calculateTopSellingDdipBoxes에서 이름 조회 최적화
CREATE INDEX idx_ddipbox_id_name 
ON ddip_box (ddipbox_id, ddipbox_name);

-- 가격 기반 수익성 분석 최적화 - findAllById에서 원가 조회 최적화
-- calculateProfitMarginAnalysis에서 원가 조회 최적화
CREATE INDEX idx_ddipbox_id_price 
ON ddip_box (ddipbox_id, original_price);

-- ========================================
-- 3. DdipBoxItem 테이블 인덱스 (중요)
-- ========================================

-- 🔥 배치 로딩 최적화 (필수) - findByDdipBoxIdIn 쿼리 최적화
-- convertToOrderDataDto에서 대량 아이템 조회 성능 향상
CREATE INDEX idx_ddipboxitem_ddipbox_id 
ON ddip_box_item (ddipbox_id);

-- 아이템 상세 조회 최적화 (복합 인덱스)
CREATE INDEX idx_ddipboxitem_ddipbox_quantity 
ON ddip_box_item (ddipbox_id, item_quantity);

-- ========================================
-- 4. OrderItems 테이블 인덱스 (선택적)
-- ========================================

-- 주문 아이템 기반 분석 최적화
CREATE INDEX idx_orderitems_order_product 
ON order_items (order_id, product_id);

-- 상품별 판매 분석 최적화 - calculateTopSellingDdipBoxes 성능 향상
CREATE INDEX idx_orderitems_product_quantity 
ON order_items (product_id, quantity);

-- ========================================
-- 5. 인덱스 생성 확인
-- ========================================

-- 생성된 인덱스 확인 쿼리
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    NON_UNIQUE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME IN ('orders', 'ddip_box', 'ddip_box_item', 'order_items')
    AND INDEX_NAME LIKE 'idx_%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- ========================================
-- 6. 성능 테스트 쿼리 (실행해서 성능 확인)
-- ========================================

-- 테스트 1: 날짜 범위 주문 조회 (EXPLAIN 결과 확인)
-- EXPLAIN SELECT * FROM orders 
-- WHERE store_id = 1 
--   AND order_status IN ('CONFIRMED', 'PICKED_UP') 
--   AND DATE(order_date) BETWEEN '2024-01-01' AND '2024-12-31';

-- 테스트 2: 재고 정렬 조회
-- EXPLAIN SELECT * FROM ddip_box 
-- WHERE store_id = 1 
-- ORDER BY remaining_quantity DESC 
-- LIMIT 5;

-- 테스트 3: 배치 로딩 성능
-- EXPLAIN SELECT * FROM ddip_box_item 
-- WHERE ddip_box_id IN (1, 2, 3, 4, 5);

-- ========================================
-- 7. 인덱스 삭제 스크립트 (롤백용)
-- ========================================

/*
-- 필요시 인덱스 삭제 (롤백)
DROP INDEX idx_orders_store_status_date ON orders;
DROP INDEX idx_orders_customer_status_date ON orders;
DROP INDEX idx_orders_date_status ON orders;

DROP INDEX idx_ddipbox_store_remaining ON ddip_box;
DROP INDEX idx_ddipbox_store_daily ON ddip_box;
DROP INDEX idx_ddipbox_id_name ON ddip_box;
DROP INDEX idx_ddipbox_id_price ON ddip_box;

DROP INDEX idx_ddipboxitem_ddipbox_id ON ddip_box_item;
DROP INDEX idx_ddipboxitem_ddipbox_quantity ON ddip_box_item;

DROP INDEX idx_orderitems_order_product ON order_items;
DROP INDEX idx_orderitems_product_quantity ON order_items;
*/

-- ========================================
-- 실행 완료
-- ========================================
-- 인덱스 생성이 완료되었습니다.
-- Analytics Service 성능이 대폭 향상될 것입니다!