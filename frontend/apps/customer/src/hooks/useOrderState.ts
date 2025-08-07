import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useFunnel } from '@/hooks/useFunnel';
import { useOrderFlowStore } from '@/store/useOrderFlowStore';
import { ROUTE_PATH } from '@/router';
import type { CartData, PaymentData } from '@/types/orderflow';

const steps = ['cart', 'payment', 'pending', 'complete'] as const;

export const useOrderState = () => {
  const navigate = useNavigate();

  // Funnel 관리
  const { Funnel, Step, nextClickHandler, prevClickHandler, currentStep } =
    useFunnel(steps, 'cart');

  // 주문 상태 관리
  const {
    orderData,
    completedOrderData,
    isCompleted,
    updateOrderData,
    completeOrder,
    createNewOrder,
    initializeFromSession,
  } = useOrderFlowStore();

  // pending → complete 자동 처리용
  const handlePendingToComplete = useCallback(() => {
    nextClickHandler('complete');
  }, [nextClickHandler]);

  // pending → payment 복귀용 (실패시)
  const handlePendingToPayment = useCallback(() => {
    prevClickHandler('payment');
  }, [prevClickHandler]);

  // 장바구니에서 결제로
  const handleNextToPayment = useCallback(
    (cartData: CartData) => {
      updateOrderData(cartData);
      nextClickHandler('payment');
    },
    [updateOrderData, nextClickHandler],
  );

  // 결제에서 대기화면으로 (결제 로직 + pending 이동)
  const handleNextToPending = useCallback(
    (paymentData: PaymentData) => {
      completeOrder(paymentData);
      nextClickHandler('pending');

      // Mock 결제 처리
      // TODO: 실제 토스 API로 교체
      setTimeout(() => {
        handlePendingToComplete();
      }, 3000);
    },
    [completeOrder, nextClickHandler, handlePendingToComplete],
  );

  // 뒤로가기 핸들러들
  const handleBackToCart = useCallback(() => {
    prevClickHandler('cart');
  }, [prevClickHandler]);

  const handleBackToPayment = useCallback(() => {
    prevClickHandler('payment');
  }, [prevClickHandler]);

  const handleBackToHome = useCallback(() => {
    navigate(ROUTE_PATH.HOME);
  }, [navigate]);

  // 새로운 주문 시작
  const handleNewOrder = useCallback(() => {
    createNewOrder();
    navigate(ROUTE_PATH.HOME);
  }, [createNewOrder, navigate]);

  return {
    // Funnel 컴포넌트들
    Funnel,
    Step,
    currentStep,

    // 상태들
    orderData,
    completedOrderData,
    isCompleted,

    // 액션들
    handleNextToPayment,
    handleNextToPending,
    handlePendingToComplete,
    handlePendingToPayment,
    handleBackToCart,
    handleBackToPayment,
    handleBackToHome,
    handleNewOrder,
    initializeFromSession,
  };
};
