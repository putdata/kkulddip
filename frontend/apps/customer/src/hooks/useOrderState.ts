import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import type { OrderData, PaymentData, FunnelState } from '@/types/orderflow';
import { ROUTE_PATH } from '@/router';

export const useOrderState = () => {
  const navigate = useNavigate();

  const [funnelState, setFunnelState] = useState<FunnelState>('active');
  const [completedOrderData, setCompletedOrderData] =
    useState<OrderData | null>(null);

  const [orderData, setOrderData] = useState<OrderData>({
    quantity: 1,
    total: 0,
    productId: 0,
    finalAmount: 0,
    orderNumber: '',
    orderDate: new Date(),
  });

  useEffect(() => {
    const completed = sessionStorage.getItem('orderCompleted');
    const savedOrderData = sessionStorage.getItem('completedOrderData');

    if (completed === 'true') {
      setFunnelState('escaped');

      if (savedOrderData) {
        try {
          setCompletedOrderData(JSON.parse(savedOrderData));
        } catch (error) {
          console.error('저장된 주문 데이터 파싱 오류:', error);
          throw error;
        }
      } else {
        const error = new Error('주문 완료 데이터를 찾을 수 없습니다.');
        console.error('주문 데이터 누락:', error);
        throw error;
      }
    }
  }, []);

  const handleOrderComplete = useCallback(
    (paymentData: PaymentData) => {
      const orderNumber = `ORDER-${Date.now()}`;
      const orderDate = new Date();

      const finalOrderData: OrderData = {
        ...orderData,
        ...paymentData,
        orderNumber,
        orderDate,
      };

      setCompletedOrderData(finalOrderData);
      setFunnelState('completed');

      try {
        sessionStorage.setItem('orderCompleted', 'true');
        sessionStorage.setItem(
          'completedOrderData',
          JSON.stringify(finalOrderData),
        );
      } catch (error) {
        console.error('주문 데이터 저장 실패:', error);
        throw error;
      }

      window.history.replaceState(null, '', window.location.pathname);

      setTimeout(() => setFunnelState('escaped'), 100);
    },
    [orderData],
  );

  const handleNewOrder = useCallback(() => {
    sessionStorage.removeItem('orderCompleted');
    sessionStorage.removeItem('completedOrderData');

    setFunnelState('active');
    setCompletedOrderData(null);
    setOrderData({
      quantity: 1,
      total: 0,
      productId: 0,
      finalAmount: 0,
      orderNumber: '',
      orderDate: new Date(),
    });

    navigate(ROUTE_PATH.HOME);
  }, [navigate]);

  const updateOrderData = useCallback((data: Partial<OrderData>) => {
    setOrderData(prev => ({ ...prev, ...data }));
  }, []);

  return {
    funnelState,
    orderData,
    completedOrderData,

    handleOrderComplete,
    handleNewOrder,
    updateOrderData,
  };
};
