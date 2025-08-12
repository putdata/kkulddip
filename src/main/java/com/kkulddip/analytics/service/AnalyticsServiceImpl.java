package com.kkulddip.analytics.service;

import com.kkulddip.analytics.client.PythonAnalyticsClient;
import com.kkulddip.analytics.dto.request.AnalyticsDdipBoxItemDto;
import com.kkulddip.analytics.dto.request.AnalyticsOrderDataDto;
import com.kkulddip.analytics.dto.request.AnalyticsOrderItemDataDto;
import com.kkulddip.analytics.dto.request.AnalyticsRequestDto;
import com.kkulddip.analytics.dto.response.AnalyticsResponseDto;
import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.domain.repository.OrderRepository;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.DdipBoxItem;
import com.kkulddip.store.repository.DdipBoxItemRepository;
import com.kkulddip.store.repository.DdipBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DDD 원칙을 준수하는 매출 분석 서비스 구현체
 * Domain Repository Interface를 사용하여 비즈니스 로직에 집중
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    // Domain Repository Interface 주입
    private final OrderRepository orderRepository;
    private final DdipBoxRepository ddipBoxRepository;
    private final DdipBoxItemRepository ddipBoxItemRepository;
    private final PythonAnalyticsClient pythonAnalyticsClient;

    /**
     * 매출 분석을 수행합니다.
     * Domain Repository를 통해 비즈니스 로직 수행
     */
    @Override
    public AnalyticsResponseDto generateSalesAnalytics(Long storeId, LocalDate startDate, LocalDate endDate) {

        log.info("Generating analytics for store: {}, period: {} to {}", storeId, startDate, endDate);

        // 1. Domain Repository를 통한 성공한 주문 조회
        List<Order> successfulOrders = getSuccessfulOrders(storeId, startDate, endDate);

        // 2. Domain Aggregate를 DTO로 변환
        List<AnalyticsOrderDataDto> orderData = convertToOrderDataDto(successfulOrders);

        // 3. Python API 요청 데이터 생성
        AnalyticsRequestDto request = new AnalyticsRequestDto(
            storeId,
            startDate,
            endDate,
            orderData
        );

        // 4. Python FastAPI 호출
        AnalyticsResponseDto response = pythonAnalyticsClient.analyzeData(request);

        log.info("Analytics generated successfully for store: {}", storeId);
        return response;
    }

    /**
     * Domain Repository를 통해 성공한 주문들을 조회
     * 기존 Repository 메서드를 활용하여 비즈니스 로직 구현
     */
    private List<Order> getSuccessfulOrders(Long storeId, LocalDate startDate, LocalDate endDate) {
        StoreId storeIdVo = StoreId.of(storeId);

        // CONFIRMED 상태의 주문들만 조회
        List<Order> confirmedOrders = orderRepository.findByStoreIdAndOrderStatus(storeIdVo, OrderStatus.CONFIRMED);

        // 날짜 필터링
        return confirmedOrders.stream()
            .filter(order -> isWithinDateRange(order, startDate, endDate))
            .collect(Collectors.toList());
    }

    /**
     * 주문이 지정된 날짜 범위 내에 있는지 확인
     */
    private boolean isWithinDateRange(Order order, LocalDate startDate, LocalDate endDate) {
        LocalDate orderDate = order.getOrderDate().toLocalDate();
        return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
    }

    /**
     * Domain Aggregate 리스트를 DTO 리스트로 변환
     */
    private List<AnalyticsOrderDataDto> convertToOrderDataDto(List<Order> orders) {
        return orders.stream()
            .map(this::convertSingleOrderToDto)
            .collect(Collectors.toList());
    }

    /**
     * 단일 Domain Aggregate를 DTO로 변환
     */
    private AnalyticsOrderDataDto convertSingleOrderToDto(Order order) {
        List<AnalyticsOrderItemDataDto> orderItems = order.getOrderItems().stream()
            .map(this::convertOrderItemToDto)
            .collect(Collectors.toList());

        return new AnalyticsOrderDataDto(
            order.getOrderId().value(),
            order.getOrderDate().toLocalDate(),
            order.getFinalPrice().amount().longValue(), // Domain의 Money 객체 사용
            orderItems
        );
    }

    /**
     * Domain Entity를 DTO로 변환
     */
    private AnalyticsOrderItemDataDto convertOrderItemToDto(OrderItem orderItem) {
        // ProductId Value Object에서 실제 값 추출
        Long productId = orderItem.getProductId().value();

        // DdipBoxItem 데이터 조회
        List<AnalyticsDdipBoxItemDto> ddipBoxItems = getDdipBoxItems(productId);

        // 상품명 조회
        String productName = getProductName(productId);

        // Domain 비즈니스 로직 활용 (할인이 적용된 최종 가격)
        Long totalPrice = orderItem.calcDiscountPrice().amount().longValue();

        return new AnalyticsOrderItemDataDto(
            orderItem.getOrderItemId().value(),
            productId,
            productName,
            orderItem.getQuantity(),
            orderItem.getUnitPrice().amount().longValue(),
            totalPrice,
            ddipBoxItems
        );
    }

    /**
     * 특정 띱박스의 아이템 리스트를 조회하여 DTO로 변환
     */
    private List<AnalyticsDdipBoxItemDto> getDdipBoxItems(Long ddipboxId) {
        List<DdipBoxItem> items = ddipBoxItemRepository.findByDdipBoxId(ddipboxId);

        return items.stream()
            .map(this::convertDdipBoxItemToDto)
            .collect(Collectors.toList());
    }

    /**
     * DdipBoxItem 엔티티를 DTO로 변환 (실제 필드 구조 반영)
     */
    private AnalyticsDdipBoxItemDto convertDdipBoxItemToDto(DdipBoxItem item) {
        return new AnalyticsDdipBoxItemDto(
            item.getItemId(), // 실제 필드: itemId
            item.getDdipboxItemName(),
            item.getOriginalPrice(),
            item.getItemQuantity(),
            null // weight 필드 없음
        );
    }

    /**
     * ProductId(DdipBoxId)로 상품명 조회
     */
    private String getProductName(Long productId) {
        Optional<DdipBox> ddipBox = ddipBoxRepository.findById(productId);
        return ddipBox.map(DdipBox::getDdipboxName).orElse("띱박스");
    }
}

