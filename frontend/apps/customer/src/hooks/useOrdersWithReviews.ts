import { useMemo } from 'react';
import { useOrders } from '@/hooks/useOrders'; // 실제 훅 이름으로 수정
import { useMyReviews } from '@/hooks/useMyReviews'; // 새로 만들 훅

export const useOrdersWithReviews = () => {
  const {
    data: orders,
    isLoading: ordersLoading,
    isFetching: ordersFetching,
    isSuccess: ordersSuccess,
    error: ordersError,
  } = useOrders();

  const {
    data: myReviewsResponse,
    isLoading: reviewsLoading,
    isFetching: reviewsFetching,
    isSuccess: reviewsSuccess,
    error: reviewsError,
  } = useMyReviews();

  // 주문 목록에 리뷰 작성 여부 추가
  const ordersWithReviewStatus = useMemo(() => {
    if (!orders || !myReviewsResponse?.reviewList) {
      return [];
    }

    const reviewedOrderIds = new Set(
      myReviewsResponse.reviewList.map(review => String(review.orderId)),
    );

    return orders.map(order => ({
      ...order,
      hasReview: reviewedOrderIds.has(String(order.orderId)),
    }));
  }, [orders, myReviewsResponse]);

  return {
    data: ordersWithReviewStatus,
    isLoading: ordersLoading || reviewsLoading,
    isFetching: ordersFetching || reviewsFetching,
    isSuccess: ordersSuccess && reviewsSuccess,
    error: ordersError || reviewsError,
  };
};
