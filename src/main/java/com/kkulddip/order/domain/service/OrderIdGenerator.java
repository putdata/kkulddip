package com.kkulddip.order.domain.service;

import com.kkulddip.order.domain.model.vo.OrderId;

/**
 * 주문 ID를 생성하는 서비스 인터페이스
 * 주문 ID는 주문 생성 시 자동으로 생성되어야 한다.
 * 이 인터페이스를 구현하여 주문 ID를 생성할 수 있도록 한다.
 */
public interface OrderIdGenerator {
    OrderId generate();
}
