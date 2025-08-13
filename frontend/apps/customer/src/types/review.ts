/**
 * 클라 리뷰 등록 요청 데이터 타입
 */
export interface ReviewCreateRequest {
  storeId: string;
  customerId: number;
  content: string;
  orderId: number;
  rating: number;
  images: File[]; // 클라이언트에서는 File 배열로 관리
}

/**
 * 서버 리뷰 등록 요청 데이터 타입
 */
export interface ReviewCreateApiRequest {
  request: {
    customerId: number;
    content: string;
    orderId: number;
    rating: number;
  };
  images: string[]; // 서버에서는 string 배열로 요구
}

/**
 * 리뷰 이미지 데이터 타입
 */
export type ReviewImage = {
  reviewImgId: number;
  imageUrl: string;
  originalName: string;
  fileSize: number;
  uploadOrder: number;
  createdAt: string;
};

/**
 * 리뷰 답글 데이터 타입
 */
export type ReviewReply = {
  replyId: number;
  ownerId: number;
  content: string;
  createdAt: string;
  updatedAt: string;
};

/**
 * 리뷰 응답 데이터 타입
 */
export type ReviewResponse = {
  reviewId: number;
  customerId: number;
  storeId: number;
  orderId: number;
  userName: string;
  profileImage: string;
  content: string;
  rating: number;
  createdAt: string;
  updatedAt: string;
  helpfulCount: number;
  images: ReviewImage[];
  reply: ReviewReply | null; // 답글이 없을 수도 있음
  isHelpful: boolean;
};
