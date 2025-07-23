package com.kkulddip.order.presentation.rest;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.order.application.service.OrderApplicationService;
import com.kkulddip.order.presentation.rest.dto.request.OrderCreateRequest;
import com.kkulddip.order.presentation.rest.dto.request.OrderItemRequest;
import com.kkulddip.order.presentation.rest.dto.response.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderApi {

    private final OrderApplicationService orderApplicationService;

    @Override
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse orderResponse = orderApplicationService.createOrder(request);
        return ApiResponse.of(201, orderResponse);
    }

    @Override
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderApplicationService.getOrderById(orderId);
        return ApiResponse.of(orderResponse);
    }

    @Override
    public ApiResponse<?> getOrders(
        @RequestParam(required = false) String customerEmail,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        if (customerEmail != null && !customerEmail.trim().isEmpty()) {
            Page<OrderResponse> orders = orderApplicationService.getOrdersByCustomerEmail(customerEmail, pageable);
            return ApiResponse.of(orders);
        } else {
            Page<OrderResponse> orders = orderApplicationService.getAllOrders(pageable);
            return ApiResponse.of(orders);
        }
    }

    @Override
    public ApiResponse<OrderResponse> confirmOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderApplicationService.confirmOrder(orderId);
        return ApiResponse.of(orderResponse);
    }

    @Override
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderApplicationService.cancelOrder(orderId);
        return ApiResponse.of(orderResponse);
    }

    @Override
    public ApiResponse<OrderResponse> deliverOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderApplicationService.deliverOrder(orderId);
        return ApiResponse.of(orderResponse);
    }

    @Override
    public ApiResponse<Void> deleteOrder(@PathVariable Long orderId) {
        orderApplicationService.deleteOrder(orderId);
        return ApiResponse.of(204, null);
    }

    @Override
    public ApiResponse<OrderResponse> addOrderItem(
        @PathVariable Long orderId,
        @Valid @RequestBody OrderItemRequest itemRequest
    ) {
        OrderResponse orderResponse = orderApplicationService.addOrderItem(orderId, itemRequest);
        return ApiResponse.of(orderResponse);
    }

    @Override
    public ApiResponse<OrderResponse> removeOrderItem(
        @PathVariable Long orderId,
        @PathVariable int itemIndex
    ) {
        OrderResponse orderResponse = orderApplicationService.removeOrderItem(orderId, itemIndex);
        return ApiResponse.of(orderResponse);
    }
} 