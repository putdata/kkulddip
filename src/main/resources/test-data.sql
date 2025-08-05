-- Test data for Store domain implementation
-- This file contains sample data for testing the Store API endpoints

-- Sample stores
INSERT INTO store (store_id, owner_id, store_name, phone, description, operating_hours, is_active, rating_average, review_count, business_number, store_address, store_profile_image, latitude, longitude, created_at) VALUES
(1, 1001, '친환경 마트', '02-1234-5678', '신선한 유기농 식품과 친환경 생활용품을 판매하는 마트입니다.', '09:00-22:00', true, 4.5, 128, '123-45-67890', '서울특별시 강남구 테헤란로 123', 'profile1.jpg', 37.5665, 126.9780, NOW()),
(2, 1002, '그린 베이커리', '02-2345-6789', '매일 갓 구운 신선한 빵과 케이크를 제공하는 베이커리입니다.', '07:00-20:00', true, 4.2, 89, '234-56-78901', '서울특별시 강남구 역삼로 456', 'profile2.jpg', 37.5651, 126.9895, NOW()),
(3, 1003, '에코 카페', '02-3456-7890', '친환경 원두로 만든 커피와 건강한 디저트를 제공합니다.', '08:00-23:00', true, 4.7, 201, '345-67-89012', '서울특별시 강남구 논현로 789', 'profile3.jpg', 37.5642, 126.9911, NOW());

-- Sample ddip boxes
INSERT INTO ddip_box (ddipbox_id, store_id, ddipbox_name, description, category, original_price, sale_price, daily_quantity, remaining_quantity, max_per_customer, is_active) VALUES
-- 친환경 마트 띱박스
(1, 1, '유기농 세트', '신선한 유기농 채소와 과일 세트', '유기농', 25000, 18000, 20, 15, 2, true),
(2, 1, '친환경 생활용품 세트', '친환경 세제와 생활용품 모음', '생활용품', 30000, 22000, 15, 10, 1, true),

-- 그린 베이커리 띱박스
(3, 2, '베이커리 모닝 세트', '갓 구운 빵과 음료 세트', '베이커리', 15000, 12000, 30, 25, 3, true),
(4, 2, '케이크 할인 세트', '인기 케이크 2개 할인 세트', '디저트', 35000, 25000, 10, 8, 1, true),

-- 에코 카페 띱박스
(5, 3, '커피 원두 세트', '스페셜티 원두 3종 세트', '커피', 40000, 30000, 12, 9, 2, true),
(6, 3, '카페 브런치 세트', '커피와 샌드위치, 디저트 세트', '브런치', 20000, 15000, 25, 20, 2, true);

-- Sample ddip box items
INSERT INTO ddip_box_item (item_id, ddipbox_item_name, original_price, item_quantity, ddipbox_id) VALUES
-- 유기농 세트 구성품
(1, '유기농 토마토 1kg', 8000, 1, 1),
(2, '유기농 상추 300g', 5000, 2, 1),
(3, '유기농 사과 2kg', 12000, 1, 1),

-- 친환경 생활용품 세트 구성품
(4, '친환경 주방세제 500ml', 8000, 2, 2),
(5, '천연 비누 100g', 4000, 3, 2),
(6, '대나무 칫솔', 3000, 4, 2),

-- 베이커리 모닝 세트 구성품
(7, '크루아상', 3000, 2, 3),
(8, '아메리카노', 4500, 1, 3),
(9, '딸기잼', 2500, 1, 3),

-- 케이크 할인 세트 구성품
(10, '초콜릿 케이크', 18000, 1, 4),
(11, '치즈케이크', 17000, 1, 4),

-- 커피 원두 세트 구성품
(12, '에티오피아 원두 200g', 15000, 1, 5),
(13, '콜롬비아 원두 200g', 13000, 1, 5),
(14, '브라질 원두 200g', 12000, 1, 5),

-- 카페 브런치 세트 구성품
(15, '아메리카노', 4500, 1, 6),
(16, '클럽 샌드위치', 12000, 1, 6),
(17, '마카롱 2개', 3500, 1, 6);