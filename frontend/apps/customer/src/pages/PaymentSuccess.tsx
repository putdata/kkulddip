import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useOrderFlowStore } from '@/store/useOrderFlowStore';
import { PaymentService } from '@/services/paymentService';
import { ROUTE_PATH } from '@/router';

const PaymentSuccess = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { completeOrder } = useOrderFlowStore();
  const [isProcessing, setIsProcessing] = useState(true);

  useEffect(() => {
    const processPayment = async () => {
      const paymentKey = searchParams.get('paymentKey');
      const orderId = searchParams.get('orderId');
      const amount = searchParams.get('amount');

      // 디버깅용 로그 추가
      console.log('URL 파라미터들:', {
        paymentKey,
        orderId,
        amount: amount,
        amountParsed: parseInt(amount || '0'),
      });

      if (!paymentKey || !orderId || !amount) {
        throw new Error('결제 정보가 올바르지 않습니다.');
      }

      try {
        // 토스페이먼츠 결제 승인 API 호출
        await PaymentService.confirmPayment(
          paymentKey,
          orderId,
          parseInt(amount),
        );

        // 주문 완료 처리
        completeOrder({
          appliedCouponId: undefined,
          discountAmount: 0,
          finalAmount: parseInt(amount),
        });

        setIsProcessing(false);

        // 3초 후 주문 완료 페이지로 이동
        setTimeout(() => {
          navigate(`${ROUTE_PATH.PAY}`);
        }, 3000);
      } catch (error) {
        console.error('결제 승인 실패:', error);
        setIsProcessing(false);
        // 에러를 상위로 전파하여 GlobalErrorBoundary에서 처리
        throw error;
      }
    };

    processPayment();
  }, [searchParams, completeOrder, navigate]);

  if (isProcessing) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 p-4">
        <div className="rounded-lg bg-white p-8 text-center shadow-lg">
          <div className="mx-auto mb-4 h-16 w-16 animate-spin rounded-full border-4 border-amber-500 border-t-transparent"></div>
          <h2 className="mb-2 text-xl font-semibold">결제 처리 중...</h2>
          <p className="text-gray-600">잠시만 기다려주세요.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 p-4">
      <div className="rounded-lg bg-white p-8 text-center shadow-lg">
        <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-green-100">
          <span className="text-2xl">✅</span>
        </div>
        <h2 className="mb-2 text-xl font-semibold text-green-600">
          결제 완료!
        </h2>
        <p className="text-gray-600">주문 완료 페이지로 이동합니다...</p>
      </div>
    </div>
  );
};

export default PaymentSuccess;
