import { create } from 'zustand';

// 리뷰 스토어에서 관리할 데이터 타입
interface ReviewStoreData {
  storeId: number;
  orderId: string; // OrderCard에서 string으로 전달됨
  storeName: string;
  orderItems?: Array<{
    productId: number;
    productName: string;
    quantity: number;
  }>;
}

// 스토어 인터페이스
interface ReviewStore {
  reviewData: ReviewStoreData | null;
  setReviewData: (data: ReviewStoreData) => void;
  clearReviewData: () => void;
}

// Zustand 스토어 생성
export const useReviewStore = create<ReviewStore>(set => ({
  reviewData: null,
  setReviewData: (data: ReviewStoreData) => set({ reviewData: data }),
  clearReviewData: () => set({ reviewData: null }),
}));
