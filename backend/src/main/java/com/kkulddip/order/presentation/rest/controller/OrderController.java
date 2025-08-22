package com.kkulddip.order.presentation.rest.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.order.application.exception.OrderException;
import com.kkulddip.order.application.facade.CustomerOrderFacade;
import com.kkulddip.order.application.facade.OrderProcessFacade;
import com.kkulddip.order.application.facade.OwnerOrderFacade;
import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.model.vo.StoreId;
import com.kkulddip.order.presentation.rest.dto.request.CreateOrderRequest;
import com.kkulddip.order.presentation.rest.dto.request.OrderConfirmationRequest;
import com.kkulddip.order.presentation.rest.dto.request.OrderPickupRequest;
import com.kkulddip.order.presentation.rest.dto.response.CreateOrderResponse;
import com.kkulddip.order.presentation.rest.dto.response.CustomerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.OrderConfirmationResponse;
import com.kkulddip.order.presentation.rest.dto.response.OrderPickupResponse;
import com.kkulddip.order.presentation.rest.dto.response.OwnerOrderHistoryResponse;
import com.kkulddip.order.presentation.rest.dto.response.PendingOrderResponse;
import com.kkulddip.order.presentation.rest.api.OrderApi;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
public class OrderController implements OrderApi {

    private final OrderProcessFacade orderProcessFacade;
    private final CustomerOrderFacade customerOrderFacade;
    private final OwnerOrderFacade ownerOrderFacade;

    @Override
    @PostMapping
    public ApiResponse<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        CreateOrderResponse response = orderProcessFacade.createOrder(request);
        return ApiResponse.of(201, response);
    }

    @Override
    @GetMapping("/pending")
    public ApiResponse<List<PendingOrderResponse>> getPendingOrders(
        @RequestParam Long storeId,
        @AuthenticationPrincipal JwtUserInfo userInfo) {
        
        // JWT에서 ownerId 추출 및 권한 확인
        if (!"OWNER".equals(userInfo.role())) {
            throw OrderException.accessDenied("사장님만 대기 주문을 조회할 수 있습니다.");
        }
        
        Long ownerId = Long.valueOf(userInfo.userId());
        StoreId storeIdVO = StoreId.of(storeId);
        
        List<PendingOrderResponse> pendingOrders = ownerOrderFacade.getPendingOrdersByStore(ownerId, storeIdVO);
        return ApiResponse.of(200, pendingOrders);
    }

    @Override
    @PostMapping("/{orderId}/confirm")
    public ApiResponse<OrderConfirmationResponse> confirmOrder(
        @PathVariable String orderId,
        @RequestBody @Valid OrderConfirmationRequest request,
        @AuthenticationPrincipal JwtUserInfo userInfo) {
        
        // JWT에서 ownerId 추출 및 권한 확인
        if (!"OWNER".equals(userInfo.role())) {
            throw OrderException.accessDenied("사장님만 주문을 확정할 수 있습니다.");
        }
        
        Long ownerId = Long.valueOf(userInfo.userId());
        OrderId orderIdVO = OrderId.of(Long.parseLong(orderId));
        
        OrderConfirmationResponse response = orderProcessFacade.processOrderConfirmation(ownerId, orderIdVO, request);
        return ApiResponse.of(200, response);
    }

    @Override
    @GetMapping("/my-history")
    public ApiResponse<List<CustomerOrderHistoryResponse>> getMyOrderHistory(@AuthenticationPrincipal JwtUserInfo userInfo) {
        
        // JWT에서 customerId 추출 및 권한 확인
        if (!"CUSTOMER".equals(userInfo.role())) {
            throw OrderException.accessDenied("고객만 주문 내역을 조회할 수 있습니다.");
        }
        
        Long customerId = Long.valueOf(userInfo.userId());
        
        List<CustomerOrderHistoryResponse> orderHistory = customerOrderFacade.getMyOrderHistory(customerId);
        return ApiResponse.of(200, orderHistory);
    }

    @Override
    @GetMapping("/store-history")
    public ApiResponse<List<OwnerOrderHistoryResponse>> getStoreOrderHistory(
        @RequestParam Long storeId,
        @AuthenticationPrincipal JwtUserInfo userInfo) {
        
        // JWT에서 ownerId 추출 및 권한 확인
        if (!"OWNER".equals(userInfo.role())) {
            throw OrderException.accessDenied("사장님만 가게 주문 내역을 조회할 수 있습니다.");
        }
        
        Long ownerId = Long.valueOf(userInfo.userId());
        StoreId storeIdVO = StoreId.of(storeId);
        
        List<OwnerOrderHistoryResponse> storeOrderHistory = ownerOrderFacade.getStoreOrderHistory(ownerId, storeIdVO);
        return ApiResponse.of(200, storeOrderHistory);
    }

    @Override
    @PostMapping("/{orderId}/pickup")
    public ApiResponse<OrderPickupResponse> markOrderAsPickedUp(
        @PathVariable String orderId,
        @RequestBody @Valid OrderPickupRequest request,
        @AuthenticationPrincipal JwtUserInfo userInfo) {
        
        // JWT에서 ownerId 추출 및 권한 확인
        if (!"OWNER".equals(userInfo.role())) {
            throw OrderException.accessDenied("사장님만 주문 픽업 완료를 처리할 수 있습니다.");
        }
        
        Long ownerId = Long.valueOf(userInfo.userId());
        OrderId orderIdVO = OrderId.of(Long.parseLong(orderId));
        
        OrderPickupResponse response = orderProcessFacade.markOrderAsPickedUp(ownerId, orderIdVO);
        return ApiResponse.of(200, response);
    }
}
