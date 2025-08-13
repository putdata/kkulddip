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
    let isCancelled = false;
    
    const processPayment = async () => {
      console.log('=== PaymentSuccess 페이지 진입 ===');
      console.log('전체 searchParams:', Array.from(searchParams.entries()));
      console.log('현재 URL:', window.location.href);
      
      const paymentKey = searchParams.get('paymentKey');
      const orderId = searchParams.get('orderId');
      const amount = searchParams.get('amount');

      // 토스페이먼츠에서 반환하는 모든 파라미터 로그
      console.log('=== 토스페이먼츠 반환 파라미터 ===');
      console.log('paymentKey:', paymentKey);
      console.log('orderId:', orderId);
      console.log('amount:', amount);
      console.log('추가 파라미터들:');
      searchParams.forEach((value, key) => {
        if (!['paymentKey', 'orderId', 'amount'].includes(key)) {
          console.log(`${key}:`, value);
        }
      });

      if (!paymentKey || !orderId || !amount) {
        throw new Error('결제 정보가 올바르지 않습니다.');
      }

      // 이미 취소된 경우 실행하지 않음
      if (isCancelled) {
        console.log('결제 처리가 취소되었습니다 (React StrictMode 중복 실행 방지)');
        return;
      }

      try {
        // 토스페이먼츠 결제 승인 API 호출
        console.log('결제 승인 API 호출 시작...');
        await PaymentService.confirmPayment(
          paymentKey,
          orderId,
          parseInt(amount),
        );

        // 이미 취소된 경우 후속 처리하지 않음
        if (isCancelled) {
          console.log('결제 승인 후 처리가 취소되었습니다');
          return;
        }

        // 주문 완료 처리
        completeOrder({
          appliedCouponId: undefined,
          discountAmount: 0,
          finalAmount: parseInt(amount),
        });

        setIsProcessing(false);

        // 3초 후 주문 완료 페이지로 이동
        const timeoutId = setTimeout(() => {
          if (!isCancelled) {
            navigate(`${ROUTE_PATH.PAY}`);
          }
        }, 3000);
        
        // cleanup 함수에서 timeout 취소
        return () => clearTimeout(timeoutId);
      } catch (error) {
        console.error('결제 승인 실패:', error);
        if (!isCancelled) {
          setIsProcessing(false);
        }
        // 에러를 상위로 전파하여 GlobalErrorBoundary에서 처리
        throw error;
      }
    };

    // 약간의 지연을 주어 StrictMode 초기 렌더링 안정화
    const timeoutId = setTimeout(() => {
      if (!isCancelled) {
        processPayment();
      }
    }, 0);

    // cleanup 함수 - 컴포넌트가 언마운트되거나 재실행될 때 호출
    return () => {
      isCancelled = true;
      clearTimeout(timeoutId);
    };
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
