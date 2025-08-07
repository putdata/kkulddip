package com.kkulddip.order.presentation.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.order.application.facade.OrderFacade;
import com.kkulddip.order.presentation.rest.dto.request.CreateOrderRequest;
import com.kkulddip.order.presentation.rest.dto.response.CreateOrderResponse;
import com.kkulddip.order.presentation.rest.api.OrderApi;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/orders")
public class OrderController implements OrderApi {

    private final OrderFacade orderFacade;

    @PostMapping
    public ApiResponse<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        CreateOrderResponse response = orderFacade.createOrder(request);
        return ApiResponse.of(201, response);
    }
}
