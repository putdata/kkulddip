package com.kkulddip.order.presentation.rest.api;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.order.presentation.rest.dto.request.CreateOrderRequest;
import com.kkulddip.order.presentation.rest.dto.request.OrderConfirmationRequest;
import com.kkulddip.order.presentation.rest.dto.response.CreateOrderResponse;
import com.kkulddip.order.presentation.rest.dto.response.OrderConfirmationResponse;
import com.kkulddip.order.presentation.rest.dto.response.PendingOrderResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order API", description = "주문 관리 API")
public interface OrderApi {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다")
    @PostMapping
    public ApiResponse<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request);

    @Operation(summary = "대기 중인 주문 조회", description = "사장님이 확인해야 할 주문들을 조회합니다 (AWAITING_CONFIRMATION)")
    @GetMapping("/pending")
    public ApiResponse<List<PendingOrderResponse>> getPendingOrders(
        @RequestParam Long storeId,
        @AuthenticationPrincipal JwtUserInfo userInfo
    );

    @Operation(summary = "주문 확정/거절", description = "사장님이 주문을 확정하거나 거절합니다")
    @PostMapping("/{orderId}/confirm")
    public ApiResponse<OrderConfirmationResponse> confirmOrder(
        @PathVariable String orderId,
        @RequestBody @Valid OrderConfirmationRequest request,
        @AuthenticationPrincipal JwtUserInfo userInfo
    );
}
