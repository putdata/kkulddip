package com.kkulddip.order.infrastructure.generator;

import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.service.OrderIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 주문 ID 생성기 구현체
 * Snowflake 알고리즘을 사용하여 유일한 주문 ID를 생성한다.
 */
@Component
@RequiredArgsConstructor
public class OrderIdGeneratorImpl implements OrderIdGenerator {

    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public OrderId generate() {
        return OrderId.of(snowflakeIdGenerator.generate());
    }
}