package com.kkulddip.analytics.service;

import com.kkulddip.analytics.client.PythonAnalyticsClient;
import com.kkulddip.analytics.dto.request.AnalyticsDdipBoxItemDto;
import com.kkulddip.analytics.dto.request.AnalyticsOrderDataDto;
import com.kkulddip.analytics.dto.request.AnalyticsOrderItemDataDto;
import com.kkulddip.analytics.dto.request.AnalyticsRequestDto;
import com.kkulddip.analytics.dto.request.DailyInventoryDataDto;
import com.kkulddip.analytics.dto.response.*;
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
import java.util.*;
import java.util.stream.Collectors;

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

        // endDate가 없으면 오늘 날짜로
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // startDate가 없으면 endDate 기준 3개월 전으로
        if (startDate == null) {
            startDate = endDate.minusMonths(3);
        }

        log.info("Generating analytics for store: {}, period: {} to {}", storeId, startDate, endDate);

        // 1. Domain Repository를 통한 성공한 주문 조회
        List<Order> successfulOrders = getSuccessfulOrders(storeId, startDate, endDate);
        
        log.info("Found {} successful orders for store {}", successfulOrders.size(), storeId);

        // 2. Domain Aggregate를 DTO로 변환
        List<AnalyticsOrderDataDto> orderData = convertToOrderDataDto(successfulOrders);

        // 3. 날짜별 재고 데이터 수집
        List<DailyInventoryDataDto> inventoryData = collectDailyInventoryData(storeId, startDate, endDate);

        // 4. Python API 요청 데이터 생성
        AnalyticsRequestDto request = AnalyticsRequestDto.builder()
            .storeId(storeId)
            .startDate(startDate)
            .endDate(endDate)
            .orders(orderData)
            .dailyInventoryData(inventoryData)
            .build();

        // 5. Python FastAPI 호출
        AnalyticsResponseDto response = pythonAnalyticsClient.analyzeData(request);

        log.info("Analytics generated successfully for store: {}", storeId);
        return response;
    }

    @Override
    public DailyAnalyticsResponseDto generateDailyAnalytics(Long storeId, LocalDate targetDate) {
        if (targetDate == null) {
            targetDate = LocalDate.now();
        }

        log.info("Generating daily analytics for store: {}, date: {}", storeId, targetDate);

        StoreId storeIdVo = StoreId.of(storeId);

        // 1. 당일 CONFIRMED 주문 조회
        List<Order> dailyOrders = getDailyConfirmedOrders(storeIdVo, targetDate);

        // 2. 매출 개요 계산
        DailySalesOverviewDto salesOverview = calculateDailySalesOverview(dailyOrders);

        // 3. 인기 띱박스 TOP 5 계산
        List<TopSellingDdipBoxDto> topSellingDdipBoxes = calculateTopSellingDdipBoxes(dailyOrders);

        // 4. 전체 재고 현황 계산
        DailyInventoryStatusDto inventoryStatus = calculateInventoryStatus(storeId);

        // 5. 재고 많이 남은 띱박스 TOP 5
        List<HighInventoryDdipBoxDto> highInventoryDdipBoxes = calculateHighInventoryDdipBoxes(storeId);

        // 6. 수익성 분석
        ProfitMarginAnalysisDto profitMarginAnalysis = calculateProfitMarginAnalysis(dailyOrders);

        return DailyAnalyticsResponseDto.builder()
            .analysisDate(targetDate)
            .storeId(storeId)
            .salesOverview(salesOverview)
            .topSellingDdipBoxes(topSellingDdipBoxes)
            .inventoryStatus(inventoryStatus)
            .highInventoryDdipBoxes(highInventoryDdipBoxes)
            .profitMarginAnalysis(profitMarginAnalysis)
            .build();
    }

    /**
     * Domain Repository를 통해 성공한 주문들을 조회
     * 기존 Repository 메서드를 활용하여 비즈니스 로직 구현
     */
    private List<Order> getSuccessfulOrders(Long storeId, LocalDate startDate, LocalDate endDate) {
        StoreId storeIdVo = StoreId.of(storeId);

        log.info("Searching for CONFIRMED orders for store: {}", storeId);

        // CONFIRMED 상태의 주문들만 조회
        List<Order> confirmedOrders = orderRepository.findByStoreIdAndOrderStatus(storeIdVo, OrderStatus.CONFIRMED);

        log.info("Found {} CONFIRMED orders for store {}", confirmedOrders.size(), storeId);

        // 모든 상태의 주문도 확인해보기
        List<Order> allOrders = orderRepository.findByStoreId(storeIdVo);
        log.info("Total orders for store {}: {}", storeId, allOrders.size());

        // 각 상태별 주문 수 확인
        allOrders.stream()
            .collect(Collectors.groupingBy(Order::getOrderStatus, Collectors.counting()))
            .forEach((status, count) -> {
                log.info("Order status {}: {} orders", status, count);
            });


        // 날짜 필터링
        List<Order> filteredOrders = confirmedOrders.stream()
            .filter(order -> isWithinDateRange(order, startDate, endDate))
            .collect(Collectors.toList());

        log.info("After date filtering ({} to {}): {} orders", startDate, endDate, filteredOrders.size());

        return filteredOrders;
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

        // 원가 정보 조회
        Long unitCostPrice = getUnitCostPrice(productId);
        Long totalCostPrice = unitCostPrice * orderItem.getQuantity();

        return new AnalyticsOrderItemDataDto(
            orderItem.getOrderItemId().value(),
            productId,
            productName,
            orderItem.getQuantity(),
            orderItem.getUnitPrice().amount().longValue(),
            totalPrice,
            unitCostPrice,
            totalCostPrice,
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

    /**
     * ProductId(DdipBoxId)로 단위 원가 조회
     */
    private Long getUnitCostPrice(Long productId) {
        Optional<DdipBox> ddipBox = ddipBoxRepository.findById(productId);
        return ddipBox.map(DdipBox::getOriginalPrice).orElse(0L);
    }

    /**
     * 날짜 범위 내 각 날짜별 재고 데이터 수집 (가게 전체 총합)
     * 성능 최적화: 한 번만 DB 조회하여 모든 날짜에 동일한 값 적용
     */
    private List<DailyInventoryDataDto> collectDailyInventoryData(Long storeId, LocalDate startDate, LocalDate endDate) {
        // 한 번만 DB 조회
        List<DdipBox> ddipBoxes = ddipBoxRepository.findByStore_StoreId(storeId);

        long totalDailyQuantity = ddipBoxes.stream()
            .mapToLong(DdipBox::getDailyQuantity)
            .sum();

        long totalRemainingQuantity = ddipBoxes.stream()
            .mapToLong(DdipBox::getRemainingQuantity)
            .sum();

        List<DailyInventoryDataDto> dailyInventoryList = new ArrayList<>();

        // 각 날짜에 동일한 재고 총합 적용 (현재는 일별 히스토리가 없으므로)
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dailyInventoryList.add(DailyInventoryDataDto.builder()
                .date(currentDate)
                .totalDailyQuantity(totalDailyQuantity)
                .totalRemainingQuantity(totalRemainingQuantity)
                .build());
            currentDate = currentDate.plusDays(1);
        }

        log.info("Collected inventory data for {} days (optimized single query)", dailyInventoryList.size());
        return dailyInventoryList;
    }

    private List<Order> getDailyConfirmedOrders(StoreId storeId, LocalDate targetDate) {
        List<Order> confirmedOrders = orderRepository.findByStoreIdAndOrderStatus(storeId, OrderStatus.CONFIRMED);

        return confirmedOrders.stream()
            .filter(order -> order.getOrderDate().toLocalDate().equals(targetDate))
            .collect(Collectors.toList());
    }

    private DailySalesOverviewDto calculateDailySalesOverview(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("why");
            return new DailySalesOverviewDto(0L, 0, 0L);
        }

        long totalSales = orders.stream()
            .mapToLong(order -> order.getFinalPrice().amount().longValue())
            .sum();

        int totalOrderCount = orders.size();
        long averageOrderAmount = totalSales / totalOrderCount;

        return new DailySalesOverviewDto(totalSales, totalOrderCount, averageOrderAmount);
    }

    private List<TopSellingDdipBoxDto> calculateTopSellingDdipBoxes(List<Order> orders) {
        Map<Long, TopSellingDdipBoxData> ddipBoxSalesMap = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Long ddipBoxId = orderItem.getProductId().value();
                int quantity = orderItem.getQuantity();
                long salesAmount = orderItem.calcDiscountPrice().amount().longValue();

                ddipBoxSalesMap.merge(ddipBoxId, 
                    new TopSellingDdipBoxData(ddipBoxId, quantity, salesAmount),
                    (existing, newData) -> new TopSellingDdipBoxData(
                        ddipBoxId,
                        existing.quantitySold + newData.quantitySold,
                        existing.totalSalesAmount + newData.totalSalesAmount
                    )
                );
            }
        }

        return ddipBoxSalesMap.values().stream()
            .sorted((a, b) -> Long.compare(b.totalSalesAmount, a.totalSalesAmount))
            .limit(5)
            .map(data -> new TopSellingDdipBoxDto(
                data.ddipBoxId,
                getProductName(data.ddipBoxId),
                data.quantitySold,
                data.totalSalesAmount
            ))
            .collect(Collectors.toList());
    }

    private DailyInventoryStatusDto calculateInventoryStatus(Long storeId) {
        List<DdipBox> ddipBoxes = ddipBoxRepository.findByStore_StoreId(storeId);

        long totalDailyCount = ddipBoxes.stream()
            .mapToLong(DdipBox::getDailyQuantity)
            .sum();

        long totalRemainingCount = ddipBoxes.stream()
            .mapToLong(DdipBox::getRemainingQuantity)
            .sum();

        double remainingPercentage = totalDailyCount > 0 
            ? (double) totalRemainingCount / totalDailyCount * 100 
            : 0.0;

        return new DailyInventoryStatusDto(totalDailyCount, totalRemainingCount, remainingPercentage);
    }

    private List<HighInventoryDdipBoxDto> calculateHighInventoryDdipBoxes(Long storeId) {
        return ddipBoxRepository.findByStore_StoreId(storeId).stream()
            .sorted((a, b) -> Long.compare(b.getRemainingQuantity(), a.getRemainingQuantity()))
            .limit(5)
            .map(ddipBox -> new HighInventoryDdipBoxDto(
                ddipBox.getDdipboxId(),
                ddipBox.getDdipboxName(),
                ddipBox.getRemainingQuantity(),
                ddipBox.getDailyQuantity()
            ))
            .collect(Collectors.toList());
    }

    private ProfitMarginAnalysisDto calculateProfitMarginAnalysis(List<Order> orders) {
        long totalRevenue = 0L;
        long totalCost = 0L;
        Map<String, ProfitRangeData> profitRanges = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Long ddipBoxId = orderItem.getProductId().value();
                Optional<DdipBox> ddipBoxOpt = ddipBoxRepository.findById(ddipBoxId);
                
                if (ddipBoxOpt.isPresent()) {
                    DdipBox ddipBox = ddipBoxOpt.get();
                    long salesPrice = orderItem.calcDiscountPrice().amount().longValue();
                    long costPrice = ddipBox.getOriginalPrice().longValue() * orderItem.getQuantity();
                    long profit = salesPrice - costPrice;
                    double profitMargin = salesPrice > 0 ? (double) profit / salesPrice * 100 : 0;

                    totalRevenue += salesPrice;
                    totalCost += costPrice;

                    String marginRange = categorizeMargin(profitMargin);
                    profitRanges.merge(marginRange,
                        new ProfitRangeData(salesPrice, 1),
                        (existing, newData) -> new ProfitRangeData(
                            existing.salesAmount + newData.salesAmount,
                            existing.productCount + newData.productCount
                        )
                    );
                }
            }
        }

        long totalProfit = totalRevenue - totalCost;
        double profitMarginPercentage = totalRevenue > 0 ? (double) totalProfit / totalRevenue * 100 : 0;

        List<ProfitMarginByRangeDto> profitByRanges = profitRanges.entrySet().stream()
            .map(entry -> new ProfitMarginByRangeDto(
                entry.getKey(),
                entry.getValue().salesAmount,
                entry.getValue().productCount
            ))
            .collect(Collectors.toList());

        return new ProfitMarginAnalysisDto(
            totalRevenue,
            totalCost,
            totalProfit,
            profitMarginPercentage,
            profitByRanges
        );
    }

    private String categorizeMargin(double profitMargin) {
        if (profitMargin < 10) return "0-10%";
        if (profitMargin < 20) return "10-20%";
        if (profitMargin < 30) return "20-30%";
        if (profitMargin < 50) return "30-50%";
        return "50%+";
    }

    private static class TopSellingDdipBoxData {
        final Long ddipBoxId;
        final int quantitySold;
        final long totalSalesAmount;

        TopSellingDdipBoxData(Long ddipBoxId, int quantitySold, long totalSalesAmount) {
            this.ddipBoxId = ddipBoxId;
            this.quantitySold = quantitySold;
            this.totalSalesAmount = totalSalesAmount;
        }
    }

    private static class ProfitRangeData {
        final long salesAmount;
        final int productCount;

        ProfitRangeData(long salesAmount, int productCount) {
            this.salesAmount = salesAmount;
            this.productCount = productCount;
        }
    }
}

