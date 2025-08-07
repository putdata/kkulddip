/**
 * 리뷰 등록 요청 데이터 타입
 */
export type ReviewCreateRequest = {
  storeId: number;
  rating: number;
  reviewText: string;
  images: File[];
};

/**
 * 리뷰 응답 데이터 타입
 */
export type ReviewCreateResponse = {
  reviewId: number;
  message: string;
  createdAt: string;
};
