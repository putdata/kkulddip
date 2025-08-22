package com.kkulddip.order.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.order.domain.model.aggregate.Order;
import com.kkulddip.order.domain.model.entity.OrderItem;
import com.kkulddip.order.domain.model.enums.OrderStatus;
import com.kkulddip.order.domain.model.vo.CustomerId;
import com.kkulddip.order.domain.model.vo.Money;
import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.model.vo.OrderItemId;
import com.kkulddip.order.domain.model.vo.ProductId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.presentation.rest.dto.response.CustomerOrderHistoryResponse;
import com.kkulddip.review.repository.ReviewRepository;
import com.kkulddip.store.repository.DdipBoxRepository;
import com.kkulddip.store.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
class OrderResponseMapperTest {

    @Mock
    private StoreRepository storeRepository;
    
    @Mock
    private DdipBoxRepository ddipBoxRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private ReviewRepository reviewRepository;
    
    @InjectMocks
    private OrderResponseMapper orderResponseMapper;
    
    private Order testOrder;
    private final Long TEST_ORDER_ID = 123456789L;
    private final Long TEST_CUSTOMER_ID = 1L;
    private final Long TEST_STORE_ID = 100L;
    private final Long TEST_PRODUCT_ID = 10L;
    
    @BeforeEach
    void setUp() {
        // 테스트용 OrderItem 리스트 생성
        OrderItem orderItem = OrderItem.builder()
            .orderItemId(OrderItemId.of(1L))
            .order(null)  // restore에서는 order 참조가 필요없음
            .productId(ProductId.of(TEST_PRODUCT_ID))
            .quantity(2)
            .unitPrice(Money.of(15000L))
            .build();
        
        List<OrderItem> orderItems = List.of(orderItem);
        
        // 테스트용 Order 생성 (restore 메서드 사용)
        testOrder = Order.restore(
            OrderId.of(TEST_ORDER_ID),
            CustomerId.of(TEST_CUSTOMER_ID),
            StoreId.of(TEST_STORE_ID),
            orderItems,
            Money.of(30000L),
            Money.of(25000L),
            Money.of(5000L),
            100.0,
            OrderStatus.PICKED_UP,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(1)
        );
    }
    
    @Test
    @DisplayName("고객이 리뷰를 작성한 경우 hasReview가 true를 반환한다")
    void toCustomerOrderHistoryResponse_WhenReviewExists_ShouldReturnHasReviewTrue() {
        // given
        when(storeRepository.findStoreNameByStoreId(TEST_STORE_ID))
            .thenReturn(Optional.of("테스트 가게"));
        when(ddipBoxRepository.findDdipBoxNameById(TEST_PRODUCT_ID))
            .thenReturn(Optional.of("테스트 상품"));
        when(reviewRepository.existsByCustomerIdAndOrderId(TEST_CUSTOMER_ID, TEST_ORDER_ID))
            .thenReturn(true);
        
        // when
        CustomerOrderHistoryResponse response = orderResponseMapper.toCustomerOrderHistoryResponse(testOrder);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.hasReview()).isTrue();
        assertThat(response.orderId()).isEqualTo(String.valueOf(TEST_ORDER_ID));
        assertThat(response.storeId()).isEqualTo(TEST_STORE_ID);
        assertThat(response.storeName()).isEqualTo("테스트 가게");
        
        verify(reviewRepository).existsByCustomerIdAndOrderId(TEST_CUSTOMER_ID, TEST_ORDER_ID);
    }
    
    @Test
    @DisplayName("고객이 리뷰를 작성하지 않은 경우 hasReview가 false를 반환한다")
    void toCustomerOrderHistoryResponse_WhenReviewNotExists_ShouldReturnHasReviewFalse() {
        // given
        when(storeRepository.findStoreNameByStoreId(TEST_STORE_ID))
            .thenReturn(Optional.of("테스트 가게"));
        when(ddipBoxRepository.findDdipBoxNameById(TEST_PRODUCT_ID))
            .thenReturn(Optional.of("테스트 상품"));
        when(reviewRepository.existsByCustomerIdAndOrderId(TEST_CUSTOMER_ID, TEST_ORDER_ID))
            .thenReturn(false);
        
        // when
        CustomerOrderHistoryResponse response = orderResponseMapper.toCustomerOrderHistoryResponse(testOrder);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.hasReview()).isFalse();
        assertThat(response.orderId()).isEqualTo(String.valueOf(TEST_ORDER_ID));
        assertThat(response.storeId()).isEqualTo(TEST_STORE_ID);
        assertThat(response.storeName()).isEqualTo("테스트 가게");
        
        verify(reviewRepository).existsByCustomerIdAndOrderId(TEST_CUSTOMER_ID, TEST_ORDER_ID);
    }
    
    @Test
    @DisplayName("여러 주문 목록에 대해 각각의 리뷰 존재 여부를 정확히 반환한다")
    void toCustomerOrderHistoryResponses_ShouldCheckReviewForEachOrder() {
        // given
        Order order1 = createTestOrder(111L, TEST_CUSTOMER_ID, TEST_STORE_ID);
        Order order2 = createTestOrder(222L, TEST_CUSTOMER_ID, TEST_STORE_ID);
        Order order3 = createTestOrder(333L, TEST_CUSTOMER_ID, TEST_STORE_ID);
        
        List<Order> orders = List.of(order1, order2, order3);
        
        when(storeRepository.findStoreNameByStoreId(any()))
            .thenReturn(Optional.of("테스트 가게"));
        when(ddipBoxRepository.findDdipBoxNameById(any()))
            .thenReturn(Optional.of("테스트 상품"));
        
        // 첫 번째와 세 번째 주문은 리뷰 있음, 두 번째는 없음
        when(reviewRepository.existsByCustomerIdAndOrderId(TEST_CUSTOMER_ID, 111L))
            .thenReturn(true);
        when(reviewRepository.existsByCustomerIdAndOrderId(TEST_CUSTOMER_ID, 222L))
            .thenReturn(false);
        when(reviewRepository.existsByCustomerIdAndOrderId(TEST_CUSTOMER_ID, 333L))
            .thenReturn(true);
        
        // when
        List<CustomerOrderHistoryResponse> responses = 
            orderResponseMapper.toCustomerOrderHistoryResponses(orders);
        
        // then
        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).hasReview()).isTrue();
        assertThat(responses.get(0).orderId()).isEqualTo("111");
        assertThat(responses.get(1).hasReview()).isFalse();
        assertThat(responses.get(1).orderId()).isEqualTo("222");
        assertThat(responses.get(2).hasReview()).isTrue();
        assertThat(responses.get(2).orderId()).isEqualTo("333");
        
        // 각 주문에 대해 리뷰 존재 여부를 확인했는지 검증
        verify(reviewRepository, times(3)).existsByCustomerIdAndOrderId(anyLong(), anyLong());
    }
    
    @Test
    @DisplayName("빈 주문 목록에 대해 빈 응답 목록을 반환한다")
    void toCustomerOrderHistoryResponses_WhenEmptyList_ShouldReturnEmptyList() {
        // given
        List<Order> emptyOrders = List.of();
        
        // when
        List<CustomerOrderHistoryResponse> responses = 
            orderResponseMapper.toCustomerOrderHistoryResponses(emptyOrders);
        
        // then
        assertThat(responses).isEmpty();
        
        // 리뷰 repository가 호출되지 않았는지 확인
        verify(reviewRepository, times(0)).existsByCustomerIdAndOrderId(anyLong(), anyLong());
    }
    
    // 테스트용 Order 생성 헬퍼 메서드
    private Order createTestOrder(Long orderId, Long customerId, Long storeId) {
        OrderItem orderItem = OrderItem.builder()
            .orderItemId(OrderItemId.of(1L))
            .order(null)
            .productId(ProductId.of(TEST_PRODUCT_ID))
            .quantity(2)
            .unitPrice(Money.of(15000L))
            .build();
        
        List<OrderItem> orderItems = List.of(orderItem);
        
        return Order.restore(
            OrderId.of(orderId),
            CustomerId.of(customerId),
            StoreId.of(storeId),
            orderItems,
            Money.of(30000L),
            Money.of(25000L),
            Money.of(5000L),
            100.0,
            OrderStatus.PICKED_UP,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(1)
        );
    }
}