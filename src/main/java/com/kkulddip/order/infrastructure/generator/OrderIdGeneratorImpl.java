package com.kkulddip.order.infrastructure.generator;

import com.kkulddip.order.domain.model.vo.OrderId;
import com.kkulddip.order.domain.service.OrderIdGenerator;
import org.springframework.stereotype.Component;

/**
 * 주문 ID 생성기 구현체
 * 현재는 persistence 계층에서 자동으로 ID를 생성하도록 null을 반환한다.
 * 향후 UUID, Snowflake ID 등 다른 ID 생성 전략으로 변경 가능하다.
 */
@Component
public class OrderIdGeneratorImpl implements OrderIdGenerator {

    @Override
    public OrderId generate() {
        return OrderId.of(null); // persistence 계층에서 자동 생성
    }
}