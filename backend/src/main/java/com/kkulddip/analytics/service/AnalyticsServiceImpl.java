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
import com.kkulddip.store.entity.DailyDdipBoxInventory;
import com.kkulddip.store.entity.DdipBox;
import com.kkulddip.store.entity.DdipBoxItem;
import com.kkulddip.store.repository.DailyDdipBoxInventoryRepository;
import com.kkulddip.store.repository.DdipBoxItemRepository;
import com.kkulddip.store.repository.DdipBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;
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
    private final DailyDdipBoxInventoryRepository inventoryRepository;
    private final PythonAnalyticsClient pythonAnalyticsClient;
    
    // Java 21 Virtual Thread Executor for parallel processing
    private final Executor virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();

    // ================== 기본 메서드 =================
    
    /**
     * 매출 분석을 수행합니다.
     * Domain Repository를 통해 비즈니스 로직 수행
     */
    @Override
    public AnalyticsResponseDto generateSalesAnalytics(Long storeId, LocalDate startDate, LocalDate endDate) {
        long startTime = System.currentTimeMillis();

        // endDate가 없으면 오늘 날짜로
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // startDate가 없으면 endDate 기준 3개월 전으로
        if (startDate == null) {
            startDate = endDate.minusMonths(3);
        }

        log.info("Generating analytics for store: {}, period: {} to {}", storeId, startDate, endDate);

        try {
            // 병렬 처리: 주문 데이터와 재고 데이터를 동시에 조회 및 처리
            final LocalDate finalStartDate = startDate;
            final LocalDate finalEndDate = endDate;
            
            CompletableFuture<List<AnalyticsOrderDataDto>> orderDataFuture = 
                CompletableFuture.supplyAsync(() -> {
                    long orderStartTime = System.currentTimeMillis();
                    List<Order> successfulOrders = getSuccessfulOrders(storeId, finalStartDate, finalEndDate);
                    log.debug("Orders fetched in {}ms, count: {}", 
                        System.currentTimeMillis() - orderStartTime, successfulOrders.size());
                    
                    long conversionStartTime = System.currentTimeMillis();
                    List<AnalyticsOrderDataDto> orderData = convertToOrderDataDto(successfulOrders);
                    log.debug("Order data converted in {}ms", 
                        System.currentTimeMillis() - conversionStartTime);
                    
                    return orderData;
                }, virtualExecutor);

            CompletableFuture<List<DailyInventoryDataDto>> inventoryDataFuture = 
                CompletableFuture.supplyAsync(() -> {
                    long inventoryStartTime = System.currentTimeMillis();
                    List<DailyInventoryDataDto> inventoryData = collectDailyInventoryData(storeId, finalStartDate, finalEndDate);
                    log.debug("Inventory data collected in {}ms", 
                        System.currentTimeMillis() - inventoryStartTime);
                    return inventoryData;
                }, virtualExecutor);

            // 두 작업의 완료를 기다림
            CompletableFuture<Void> allDataFuture = CompletableFuture.allOf(orderDataFuture, inventoryDataFuture);
            allDataFuture.join(); // 모든 작업 완료까지 대기

            List<AnalyticsOrderDataDto> orderData = orderDataFuture.join();
            List<DailyInventoryDataDto> inventoryData = inventoryDataFuture.join();
            
            log.info("Data collection completed in {}ms - Orders: {}, Inventory days: {}", 
                System.currentTimeMillis() - startTime, orderData.size(), inventoryData.size());

            // 4. Python API 요청 데이터 생성
            AnalyticsRequestDto request = AnalyticsRequestDto.builder()
                .storeId(storeId)
                .startDate(finalStartDate)
                .endDate(finalEndDate)
                .orders(orderData)
                .dailyInventoryData(inventoryData)
                .build();

            // 5. Python FastAPI 호출
            long apiStartTime = System.currentTimeMillis();
            AnalyticsResponseDto response = pythonAnalyticsClient.analyzeData(request);
            log.debug("Python API call completed in {}ms", System.currentTimeMillis() - apiStartTime);

            long totalTime = System.currentTimeMillis() - startTime;
            log.info("Analytics generated successfully for store: {} in {}ms", storeId, totalTime);
            return response;
            
        } catch (Exception e) {
            log.error("Error generating analytics for store: {}", storeId, e);
            throw new RuntimeException("Analytics generation failed", e);
        }
    }

    @Override
    public DailyAnalyticsResponseDto generateDailyAnalytics(Long storeId, LocalDate targetDate) {
        long startTime = System.currentTimeMillis();
        
        if (targetDate == null) {
            targetDate = LocalDate.now();
        }

        log.info("Generating daily analytics for store: {}, date: {}", storeId, targetDate);

        try {
            StoreId storeIdVo = StoreId.of(storeId);
            final LocalDate finalTargetDate = targetDate;

            // 1. 당일 CONFIRMED 주문 조회 (필수 기반 데이터)
            long ordersStartTime = System.currentTimeMillis();
            List<Order> dailyOrders = getDailyConfirmedOrders(storeIdVo, finalTargetDate);
            log.debug("Daily orders fetched in {}ms, count: {}", 
                System.currentTimeMillis() - ordersStartTime, dailyOrders.size());

            // 2. 병렬 처리: 주문 기반 계산들과 재고 기반 계산들을 동시 실행
            CompletableFuture<DailySalesOverviewDto> salesOverviewFuture = 
                CompletableFuture.supplyAsync(() -> {
                    long calcStartTime = System.currentTimeMillis();
                    DailySalesOverviewDto result = calculateDailySalesOverview(dailyOrders);
                    log.debug("Sales overview calculated in {}ms", System.currentTimeMillis() - calcStartTime);
                    return result;
                }, virtualExecutor);

            CompletableFuture<List<TopSellingDdipBoxDto>> topSellingFuture = 
                CompletableFuture.supplyAsync(() -> {
                    long calcStartTime = System.currentTimeMillis();
                    List<TopSellingDdipBoxDto> result = calculateTopSellingDdipBoxes(dailyOrders);
                    log.debug("Top selling boxes calculated in {}ms", System.currentTimeMillis() - calcStartTime);
                    return result;
                }, virtualExecutor);

            CompletableFuture<ProfitMarginAnalysisDto> profitAnalysisFuture = 
                CompletableFuture.supplyAsync(() -> {
                    long calcStartTime = System.currentTimeMillis();
                    ProfitMarginAnalysisDto result = calculateProfitMarginAnalysis(dailyOrders);
                    log.debug("Profit analysis calculated in {}ms", System.currentTimeMillis() - calcStartTime);
                    return result;
                }, virtualExecutor);

            CompletableFuture<DailyInventoryStatusDto> inventoryStatusFuture = 
                CompletableFuture.supplyAsync(() -> {
                    long calcStartTime = System.currentTimeMillis();
                    DailyInventoryStatusDto result = calculateInventoryStatus(storeId);
                    log.debug("Inventory status calculated in {}ms", System.currentTimeMillis() - calcStartTime);
                    return result;
                }, virtualExecutor);

            CompletableFuture<List<HighInventoryDdipBoxDto>> highInventoryFuture = 
                CompletableFuture.supplyAsync(() -> {
                    long calcStartTime = System.currentTimeMillis();
                    List<HighInventoryDdipBoxDto> result = calculateHighInventoryDdipBoxes(storeId);
                    log.debug("High inventory boxes calculated in {}ms", System.currentTimeMillis() - calcStartTime);
                    return result;
                }, virtualExecutor);

            // 모든 병렬 작업의 완료를 기다림
            CompletableFuture<Void> allCalculationsFuture = CompletableFuture.allOf(
                salesOverviewFuture, topSellingFuture, profitAnalysisFuture, 
                inventoryStatusFuture, highInventoryFuture
            );
            allCalculationsFuture.join();

            // 결과 수집
            DailySalesOverviewDto salesOverview = salesOverviewFuture.join();
            List<TopSellingDdipBoxDto> topSellingDdipBoxes = topSellingFuture.join();
            ProfitMarginAnalysisDto profitMarginAnalysis = profitAnalysisFuture.join();
            DailyInventoryStatusDto inventoryStatus = inventoryStatusFuture.join();
            List<HighInventoryDdipBoxDto> highInventoryDdipBoxes = highInventoryFuture.join();

            long totalTime = System.currentTimeMillis() - startTime;
            log.info("Daily analytics generated successfully for store: {} in {}ms", storeId, totalTime);

            return DailyAnalyticsResponseDto.builder()
                .analysisDate(finalTargetDate)
                .storeId(storeId)
                .salesOverview(salesOverview)
                .topSellingDdipBoxes(topSellingDdipBoxes)
                .inventoryStatus(inventoryStatus)
                .highInventoryDdipBoxes(highInventoryDdipBoxes)
                .profitMarginAnalysis(profitMarginAnalysis)
                .build();
                
        } catch (Exception e) {
            log.error("Error generating daily analytics for store: {}", storeId, e);
            throw new RuntimeException("Daily analytics generation failed", e);
        }
    }

    // ================== 유틸리티 메서드 ==================
    
    /**
     * Domain Repository를 통해 성공한 주문들을 조회 (최적화됨)
     * 데이터베이스 레벨에서 날짜 범위와 상태를 필터링하여 성능 향상
     */
    private List<Order> getSuccessfulOrders(Long storeId, LocalDate startDate, LocalDate endDate) {
        return orderRepository.findByStoreIdAndOrderStatusInAndOrderDateBetween(
            StoreId.of(storeId),
            List.of(OrderStatus.CONFIRMED, OrderStatus.PICKED_UP),
            startDate,
            endDate
        );
    }




    /**
     * 날짜 범위 내 각 날짜별 재고 데이터 수집 (가게 전체 총합)
     * 추후 수정 필요
     */
    private List<DailyInventoryDataDto> collectDailyInventoryData(Long storeId, LocalDate startDate, LocalDate endDate) {

        // 재고 관리 안 될 경우
        List<DdipBox> ddipBoxes = ddipBoxRepository.findByStore_StoreId(storeId);
        if (ddipBoxes.isEmpty()) {
            return new ArrayList<>();
        }
        // 현재 총 판매 가능 양(이걸 이용해서 임의 값 넣어줄 것)
        long totalDailyQuantity = ddipBoxes.stream()
            .mapToLong(DdipBox::getDailyQuantity)
            .sum();

        //------------------------------------------------------------------------

        // 재고 관리 될 경우
        //스토어 아이디에 해당하는 띱박스 아이디들 찾기
        List<Long> ddipboxIds = ddipBoxes.stream()
            .map(DdipBox::getDdipboxId)
            .collect(Collectors.toList());

        // 띱박스 아이디 들에 포함되면서, startDate, endDate 사이에 있는 재고들 찾기
        List<DailyDdipBoxInventory> inventories = inventoryRepository.findByDdipboxIdInAndCreateAtBetween(ddipboxIds, startDate, endDate);

        //같은 날짜 dailyQuantity, remainingQuantity 합산
        Map<LocalDate, Long> dailyQuantitySum = inventories.stream()
            .collect(Collectors.groupingBy(
                DailyDdipBoxInventory::getCreateAt,
                Collectors.summingLong(DailyDdipBoxInventory::getDailyQuantity)
            ));

        Map<LocalDate, Long> remainingQuantitySum = inventories.stream()
            .collect(Collectors.groupingBy(
                DailyDdipBoxInventory::getCreateAt,
                Collectors.summingLong(DailyDdipBoxInventory::getRemainingQuantity)
            ));

        List<DailyInventoryDataDto> dailyInventoryList = new ArrayList<>();

        LocalDate currentDate = startDate;
        Random random = new Random();

        while (!currentDate.isAfter(endDate)) {
            Long dailyQty = dailyQuantitySum.get(currentDate);
            if (!inventories.isEmpty() && dailyQty != null) {
                // 재고 관리 추적 될 때
                dailyInventoryList.add(DailyInventoryDataDto.builder()
                    .date(currentDate)
                    .totalDailyQuantity(dailyQty)
                    .totalRemainingQuantity(remainingQuantitySum.get(currentDate))
                    .build());
            } else if(inventories.isEmpty()){
                // 재고 관리 아예 추적 안 될 때
                // 현재 재고량 기준 ±20% 랜덤
                long randomDailyQty = (long) (totalDailyQuantity * (0.8 + random.nextDouble() * 0.4));

                // 리메이닝은 재고량보다 낮게 (0 ~ randomDailyQty 범위)
                long randomRemainingQty = (long) (random.nextDouble() * randomDailyQty);

                dailyInventoryList.add(DailyInventoryDataDto.builder()
                    .date(currentDate)
                    .totalDailyQuantity(randomDailyQty)
                    .totalRemainingQuantity(randomRemainingQty)
                    .build());
            } else {
                // 해당 날짜에 재고 데이터 없는 경우
                log.debug("Skipping date {} - partial inventory data", currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        log.info("Collected inventory data for {} days (optimized single query)", dailyInventoryList.size());
        return dailyInventoryList;
    }

    private List<Order> getDailyConfirmedOrders(StoreId storeId, LocalDate targetDate) {
        return orderRepository.findByStoreIdAndOrderStatusInAndOrderDate(
            storeId, 
            List.of(OrderStatus.CONFIRMED, OrderStatus.PICKED_UP), 
            targetDate
        );
    }

    /**
     * 일일 매출 개요를 계산
     * @param orders 당일 주문 목록
     * @return 총 매출, 총 주문 수, 평균 주문 금액을 포함한 매출 개요
     */
    private DailySalesOverviewDto calculateDailySalesOverview(List<Order> orders) {
        if (orders.isEmpty()) {
            log.debug("No orders found for daily sales overview calculation");
            return new DailySalesOverviewDto(0L, 0, 0L);
        }

        long totalSales = orders.stream()
            .mapToLong(order -> order.getFinalPrice().amount().longValue())
            .sum();

        int totalOrderCount = orders.size();
        long averageOrderAmount = totalSales / totalOrderCount;

        return new DailySalesOverviewDto(totalSales, totalOrderCount, averageOrderAmount);
    }

    /**
     * 인기 띱박스 TOP 5를 계산 (매출액 기준 정렬) - 가상 스레드 + 병렬 스트림 최적화
     * @param orders 분석할 주문 목록
     * @return 매출액 순으로 정렬된 상위 5개 띱박스 목록
     */
    private List<TopSellingDdipBoxDto> calculateTopSellingDdipBoxes(List<Order> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        long startTime = System.currentTimeMillis();
        
        // 1. 병렬 스트림으로 매출 데이터 집계 (대량 주문 처리 시 효율적)
        Map<Long, TopSellingDdipBoxData> ddipBoxSalesMap = orders.parallelStream()
            .flatMap(order -> order.getOrderItems().stream())
            .collect(Collectors.groupingByConcurrent(
                orderItem -> orderItem.getProductId().value(),
                Collectors.reducing(
                    new TopSellingDdipBoxData(0L, 0, 0L),
                    orderItem -> new TopSellingDdipBoxData(
                        orderItem.getProductId().value(),
                        orderItem.getQuantity(),
                        orderItem.calcDiscountPrice().amount().longValue()
                    ),
                    (existing, newData) -> new TopSellingDdipBoxData(
                        newData.ddipBoxId,
                        existing.quantitySold + newData.quantitySold,
                        existing.totalSalesAmount + newData.totalSalesAmount
                    )
                )
            ));

        // 2. TOP 5 선별 (병렬 스트림 활용)
        CompletableFuture<List<TopSellingDdipBoxData>> top5DataFuture = 
            CompletableFuture.supplyAsync(() -> {
                return ddipBoxSalesMap.values().parallelStream()
                    .sorted((a, b) -> Long.compare(b.totalSalesAmount, a.totalSalesAmount))
                    .limit(5)
                    .collect(Collectors.toList());
            }, virtualExecutor);

        // 3. 상품명 배치 로딩을 병렬로 준비
        CompletableFuture<Map<Long, String>> nameMapFuture = top5DataFuture.thenApplyAsync(top5Data -> {
            Set<Long> top5Ids = top5Data.stream()
                .map(data -> data.ddipBoxId)
                .collect(Collectors.toSet());
            
            if (top5Ids.isEmpty()) {
                return Collections.emptyMap();
            }
            
            return ddipBoxRepository.findAllById(top5Ids)
                .stream()
                .collect(Collectors.toMap(DdipBox::getDdipboxId, DdipBox::getDdipboxName));
        }, virtualExecutor);

        // 4. 두 작업 완료 대기 및 DTO 변환
        List<TopSellingDdipBoxData> top5Data = top5DataFuture.join();
        Map<Long, String> nameMap = nameMapFuture.join();

        List<TopSellingDdipBoxDto> result = top5Data.stream()
            .map(data -> new TopSellingDdipBoxDto(
                data.ddipBoxId,
                nameMap.getOrDefault(data.ddipBoxId, "띱박스"),
                data.quantitySold,
                data.totalSalesAmount
            ))
            .collect(Collectors.toList());

        long totalTime = System.currentTimeMillis() - startTime;
        log.debug("Top selling boxes calculation completed in {}ms, result count: {}", 
            totalTime, result.size());

        return result;
    }

    /**
     * 매장의 전체 재고 현황을 계산
     * @param storeId 매장 ID
     * @return 일일 총 수량, 남은 총 수량, 재고 비율을 포함한 재고 현황
     */
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

    /**
     * 재고가 많이 남은 띱박스 TOP 5를 계산
     * @param storeId 매장 ID
     * @return 남은 재고량 순으로 정렬된 상위 5개 띱박스 목록
     */
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

    /**
     * 수익성 분석을 수행하여 총 수익률과 범위별 수익 분석 결과를 계산 (가상 스레드 + 병렬 스트림 최적화)
     * @param orders 분석할 주문 목록
     * @return 총 매출, 총 원가, 총 이익, 수익률, 범위별 수익 분석을 포함한 수익성 분석 결과
     */
    private ProfitMarginAnalysisDto calculateProfitMarginAnalysis(List<Order> orders) {
        if (orders.isEmpty()) {
            return new ProfitMarginAnalysisDto(0L, 0L, 0L, 0.0, Collections.emptyList());
        }

        long startTime = System.currentTimeMillis();

        // 1. 병렬 스트림으로 DdipBox ID 수집
        Set<Long> ddipBoxIds = orders.parallelStream()
            .flatMap(order -> order.getOrderItems().stream())
            .map(item -> item.getProductId().value())
            .collect(Collectors.toSet());

        // 2. 가상 스레드로 DdipBox 배치 로딩
        CompletableFuture<Map<Long, DdipBox>> ddipBoxMapFuture = 
            CompletableFuture.supplyAsync(() -> {
                long dbStartTime = System.currentTimeMillis();
                Map<Long, DdipBox> result = ddipBoxRepository.findAllById(ddipBoxIds)
                    .stream()
                    .collect(Collectors.toMap(DdipBox::getDdipboxId, Function.identity()));
                log.debug("DdipBox loading for profit analysis completed in {}ms", 
                    System.currentTimeMillis() - dbStartTime);
                return result;
            }, virtualExecutor);

        Map<Long, DdipBox> ddipBoxMap = ddipBoxMapFuture.join();

        // 3. 병렬 스트림으로 수익성 데이터 계산
        List<OrderItemProfitData> profitDataList = orders.parallelStream()
            .flatMap(order -> order.getOrderItems().stream())
            .map(orderItem -> {
                Long ddipBoxId = orderItem.getProductId().value();
                DdipBox ddipBox = ddipBoxMap.get(ddipBoxId);
                
                if (ddipBox != null) {
                    long salesPrice = orderItem.calcDiscountPrice().amount().longValue();
                    long costPrice = ddipBox.getOriginalPrice().longValue() * orderItem.getQuantity();
                    long profit = salesPrice - costPrice;
                    double profitMargin = salesPrice > 0 ? (double) profit / salesPrice * 100 : 0;
                    
                    return new OrderItemProfitData(salesPrice, costPrice, profitMargin);
                } else {
                    log.warn("DdipBox not found for ID: {}", ddipBoxId);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 4. 가상 스레드로 총 수치 계산과 범위별 분석을 병렬 처리
        CompletableFuture<Long> totalRevenueFuture = 
            CompletableFuture.supplyAsync(() -> 
                profitDataList.parallelStream().mapToLong(data -> data.salesPrice).sum(), 
                virtualExecutor);

        CompletableFuture<Long> totalCostFuture = 
            CompletableFuture.supplyAsync(() -> 
                profitDataList.parallelStream().mapToLong(data -> data.costPrice).sum(), 
                virtualExecutor);

        CompletableFuture<Map<String, ProfitRangeData>> profitRangesFuture = 
            CompletableFuture.supplyAsync(() -> 
                profitDataList.parallelStream()
                    .collect(Collectors.groupingByConcurrent(
                        data -> categorizeMargin(data.profitMargin),
                        Collectors.reducing(
                            new ProfitRangeData(0L, 0),
                            data -> new ProfitRangeData(data.salesPrice, 1),
                            (existing, newData) -> new ProfitRangeData(
                                existing.salesAmount + newData.salesAmount,
                                existing.productCount + newData.productCount
                            )
                        )
                    )), 
                virtualExecutor);

        // 5. 모든 계산 완료 대기
        CompletableFuture<Void> allCalculationsFuture = CompletableFuture.allOf(
            totalRevenueFuture, totalCostFuture, profitRangesFuture);
        allCalculationsFuture.join();

        long totalRevenue = totalRevenueFuture.join();
        long totalCost = totalCostFuture.join();
        Map<String, ProfitRangeData> profitRanges = profitRangesFuture.join();

        long totalProfit = totalRevenue - totalCost;
        double profitMarginPercentage = totalRevenue > 0 ? (double) totalProfit / totalRevenue * 100 : 0;

        List<ProfitMarginByRangeDto> profitByRanges = profitRanges.entrySet().stream()
            .map(entry -> new ProfitMarginByRangeDto(
                entry.getKey(),
                entry.getValue().salesAmount,
                entry.getValue().productCount
            ))
            .collect(Collectors.toList());

        long totalTime = System.currentTimeMillis() - startTime;
        log.debug("Profit margin analysis completed in {}ms, processed {} items", 
            totalTime, profitDataList.size());

        return new ProfitMarginAnalysisDto(
            totalRevenue,
            totalCost,
            totalProfit,
            profitMarginPercentage,
            profitByRanges
        );
    }

    /**
     * 수익률을 범위별로 분류
     * @param profitMargin 수익률 (백분율)
     * @return 수익률 범위 문자열 (예: "0-10%", "10-20%", "50%+")
     */
    private String categorizeMargin(double profitMargin) {
        if (profitMargin < 10) return "0-10%";
        if (profitMargin < 20) return "10-20%";
        if (profitMargin < 30) return "20-30%";
        if (profitMargin < 50) return "30-50%";
        return "50%+";
    }

    /**
     * 인기 띱박스 계산을 위한 임시 데이터 클래스
     */
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

    /**
     * 수익률 범위별 데이터 집계를 위한 임시 데이터 클래스
     */
    private static class ProfitRangeData {
        final long salesAmount;
        final int productCount;

        ProfitRangeData(long salesAmount, int productCount) {
            this.salesAmount = salesAmount;
            this.productCount = productCount;
        }
    }

    /**
     * 주문 항목별 수익성 데이터를 위한 임시 데이터 클래스
     */
    private static class OrderItemProfitData {
        final long salesPrice;
        final long costPrice;
        final double profitMargin;

        OrderItemProfitData(long salesPrice, long costPrice, double profitMargin) {
            this.salesPrice = salesPrice;
            this.costPrice = costPrice;
            this.profitMargin = profitMargin;
        }
    }

    // ================== dto 생성 메서드 ==================
    
    /**
     * Domain Aggregate 리스트를 DTO 리스트로 변환 (가상 스레드 + 병렬 스트림으로 최적화)
     */
    private List<AnalyticsOrderDataDto> convertToOrderDataDto(List<Order> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }
        
        long startTime = System.currentTimeMillis();
        
        // 1. 모든 ProductId(DdipBoxId) 수집
        Set<Long> allProductIds = orders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .map(item -> item.getProductId().value())
            .collect(Collectors.toSet());

        log.debug("ProductIds collected: {} items", allProductIds.size());

        // 2. 배치 로딩을 가상 스레드로 병렬 실행
        CompletableFuture<Map<Long, DdipBox>> ddipBoxMapFuture = 
            CompletableFuture.supplyAsync(() -> {
                long dbStartTime = System.currentTimeMillis();
                Map<Long, DdipBox> result = ddipBoxRepository.findAllById(allProductIds)
                    .stream()
                    .collect(Collectors.toMap(DdipBox::getDdipboxId, Function.identity()));
                log.debug("DdipBox batch loading completed in {}ms, count: {}", 
                    System.currentTimeMillis() - dbStartTime, result.size());
                return result;
            }, virtualExecutor);

        CompletableFuture<Map<Long, List<DdipBoxItem>>> ddipBoxItemsMapFuture = 
            CompletableFuture.supplyAsync(() -> {
                long dbStartTime = System.currentTimeMillis();
                Map<Long, List<DdipBoxItem>> result = ddipBoxItemRepository.findByDdipBoxIdIn(allProductIds)
                    .stream()
                    .collect(Collectors.groupingBy(item -> item.getDdipBox().getDdipboxId()));
                log.debug("DdipBoxItem batch loading completed in {}ms, groups: {}", 
                    System.currentTimeMillis() - dbStartTime, result.size());
                return result;
            }, virtualExecutor);

        // 3. 두 배치 로딩 완료 대기
        CompletableFuture<Void> batchLoadingFuture = CompletableFuture.allOf(ddipBoxMapFuture, ddipBoxItemsMapFuture);
        batchLoadingFuture.join();

        Map<Long, DdipBox> ddipBoxMap = ddipBoxMapFuture.join();
        Map<Long, List<DdipBoxItem>> ddipBoxItemsMap = ddipBoxItemsMapFuture.join();

        long batchLoadTime = System.currentTimeMillis() - startTime;
        log.debug("Batch loading completed in {}ms", batchLoadTime);

        // 4. 병렬 스트림으로 DTO 변환 (대량 데이터 처리 시 효율적)
        long conversionStartTime = System.currentTimeMillis();
        List<AnalyticsOrderDataDto> result = orders.parallelStream()
            .map(order -> convertSingleOrderToDto(order, ddipBoxMap, ddipBoxItemsMap))
            .collect(Collectors.toList());

        long conversionTime = System.currentTimeMillis() - conversionStartTime;
        long totalTime = System.currentTimeMillis() - startTime;
        
        log.debug("DTO conversion completed - Orders: {}, Conversion: {}ms, Total: {}ms", 
            result.size(), conversionTime, totalTime);

        return result;
    }

    /**
     * 단일 Domain Aggregate를 DTO로 변환 (메모리 기반 조회로 성능 최적화)
     */
    private AnalyticsOrderDataDto convertSingleOrderToDto(Order order, 
                                                          Map<Long, DdipBox> ddipBoxMap, 
                                                          Map<Long, List<DdipBoxItem>> ddipBoxItemsMap) {
        List<AnalyticsOrderItemDataDto> orderItems = order.getOrderItems().stream()
            .map(item -> convertOrderItemToDto(item, ddipBoxMap, ddipBoxItemsMap))
            .collect(Collectors.toList());

        return new AnalyticsOrderDataDto(
            order.getOrderId().value(),
            order.getOrderDate().toLocalDate(),
            order.getFinalPrice().amount().longValue(), // Domain의 Money 객체 사용
            orderItems
        );
    }

    /**
     * Domain Entity를 DTO로 변환 (메모리 기반 조회로 성능 최적화)
     */
    private AnalyticsOrderItemDataDto convertOrderItemToDto(OrderItem orderItem, 
                                                            Map<Long, DdipBox> ddipBoxMap, 
                                                            Map<Long, List<DdipBoxItem>> ddipBoxItemsMap) {
        // ProductId Value Object에서 실제 값 추출
        Long productId = orderItem.getProductId().value();

        // 메모리에서 DdipBox 정보 조회
        DdipBox ddipBox = ddipBoxMap.get(productId);
        String productName = ddipBox != null ? ddipBox.getDdipboxName() : "띱박스";
        Long unitCostPrice = ddipBox != null ? ddipBox.getOriginalPrice() : 0L;

        // 메모리에서 DdipBoxItem 정보 조회
        List<DdipBoxItem> ddipBoxItems = ddipBoxItemsMap.getOrDefault(productId, Collections.emptyList());
        List<AnalyticsDdipBoxItemDto> ddipBoxItemDtos = ddipBoxItems.stream()
            .map(this::convertDdipBoxItemToDto)
            .collect(Collectors.toList());

        // Domain 비즈니스 로직 활용 (할인이 적용된 최종 가격)
        Long totalPrice = orderItem.calcDiscountPrice().amount().longValue();
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
            ddipBoxItemDtos
        );
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

}

