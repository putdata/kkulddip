package com.kkulddip.order.presentation.rest.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.order.presentation.rest.dto.request.CreateOrderRequest;
import com.kkulddip.order.presentation.rest.dto.response.CreateOrderResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order API", description = "주문 관리 API")
@RestController
@RequestMapping("/v1/orders")
public interface OrderApi {

    @PostMapping
    public ApiResponse<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request);
}
