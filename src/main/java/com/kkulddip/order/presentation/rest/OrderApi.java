package com.kkulddip.order.presentation.rest;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.order.presentation.rest.dto.request.OrderCreateRequest;
import com.kkulddip.order.presentation.rest.dto.request.OrderItemRequest;
import com.kkulddip.order.presentation.rest.dto.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Order", description = "주문 관리 API")
public interface OrderApi {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "주문 생성 성공",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request);

    @Operation(summary = "주문 단건 조회", description = "주문 ID로 특정 주문을 조회합니다.")
    @GetMapping("/{orderId}")
    ApiResponse<OrderResponse> getOrderById(
        @Parameter(description = "주문 ID", required = true) 
        @PathVariable Long orderId
    );

    @Operation(summary = "주문 목록 조회", description = "주문 목록을 조회합니다.")
    @GetMapping
    ApiResponse<?> getOrders(
        @Parameter(description = "고객 이메일") 
        @RequestParam(required = false) String customerEmail,
        @Parameter(description = "페이지 정보")
        @PageableDefault(size = 20) Pageable pageable
    );

    @Operation(summary = "주문 확인", description = "주문을 확인 상태로 변경합니다.")
    @PutMapping("/{orderId}/confirm")
    ApiResponse<OrderResponse> confirmOrder(
        @Parameter(description = "주문 ID", required = true) 
        @PathVariable Long orderId
    );

    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @PutMapping("/{orderId}/cancel")
    ApiResponse<OrderResponse> cancelOrder(
        @Parameter(description = "주문 ID", required = true) 
        @PathVariable Long orderId
    );

    @Operation(summary = "주문 배송 완료", description = "주문을 배송 완료 상태로 변경합니다.")
    @PutMapping("/{orderId}/deliver")
    ApiResponse<OrderResponse> deliverOrder(
        @Parameter(description = "주문 ID", required = true) 
        @PathVariable Long orderId
    );

    @Operation(summary = "주문 삭제", description = "주문을 삭제합니다. (대기중 상태만 가능)")
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ApiResponse<Void> deleteOrder(
        @Parameter(description = "주문 ID", required = true) 
        @PathVariable Long orderId
    );

    @Operation(summary = "주문 항목 추가", description = "주문에 새로운 항목을 추가합니다.")
    @PostMapping("/{orderId}/items")
    ApiResponse<OrderResponse> addOrderItem(
        @Parameter(description = "주문 ID", required = true) 
        @PathVariable Long orderId,
        @Valid @RequestBody OrderItemRequest itemRequest
    );

    @Operation(summary = "주문 항목 제거", description = "주문에서 특정 항목을 제거합니다.")
    @DeleteMapping("/{orderId}/items/{itemIndex}")
    ApiResponse<OrderResponse> removeOrderItem(
        @Parameter(description = "주문 ID", required = true) 
        @PathVariable Long orderId,
        @Parameter(description = "항목 인덱스", required = true) 
        @PathVariable int itemIndex
    );
} 