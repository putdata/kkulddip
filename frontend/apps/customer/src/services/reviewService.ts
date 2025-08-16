import { API_PATH } from '@/constants/api-path';
import type {
  ReviewCreateRequest,
  ReviewListResponse,
  ReviewResponse,
} from '@/types/review';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from 'common';

export interface ReviewListParams {
  size?: number;
  cursor?: string;
  sort?: string;
}

/**
 * 리뷰 관련 API 서비스 클래스
 */
export class ReviewService {
  /**
   * 리뷰 목록 조회
   */
  static async getReviews(
    storeId: string,
    params?: {
      page?: number;
      size?: number;
      sort?: string;
    },
  ): Promise<ReviewListResponse> {
    return apiClient.get(API_PATH.STORE_REVIEWS(storeId), params);
  }

  /**
   * 리뷰 목록 조회 (커서 기반 페이지네이션)
   */
  static async getReviewsWithPagination(
    storeId: string,
    params: ReviewListParams,
  ): Promise<ReviewListResponse> {
    const searchParams = new URLSearchParams();

    if (params.size) {
      searchParams.append('size', params.size.toString());
    }

    if (params.cursor) {
      searchParams.append('cursor', params.cursor);
    }

    if (params.sort) {
      searchParams.append('sort', params.sort);
    }

    const url = searchParams.toString()
      ? `${API_PATH.STORE_REVIEWS(storeId)}?${searchParams.toString()}`
      : API_PATH.STORE_REVIEWS(storeId);

    return apiClient.get(url);
  }

  /**
   * 리뷰 등록 API 호출
   *
   * @param data - 등록할 리뷰 정보
   * @returns 등록된 리뷰 정보
   */
  static async createReview(
    data: ReviewCreateRequest,
  ): Promise<ReviewResponse> {
    const formData = new FormData();

    // request 객체를 JSON 문자열로 변환하여 추가
    const request = {
      customerId: data.customerId,
      content: data.content,
      orderId: data.orderId,
      rating: data.rating,
    };

    formData.append(
      'request',
      new Blob([JSON.stringify(request)], {
        type: 'application/json',
      }),
    );

    // 이미지 파일을 추가
    data.images.forEach(image => {
      formData.append('images', image);
    });

    formData.forEach((value, key) => {
      console.log(`${key} =>`, value);
    });

    return apiClient.post(API_PATH.STORE_REVIEWS(data.storeId), formData);
  }

  /**
   * 리뷰에 도움돼요 추가
   */
  static async addHelpful(reviewId: string): Promise<void> {
    return apiClient.post(API_PATH.REVIEW_HELPFUL(reviewId));
  }

  /**
   * 리뷰에서 도움돼요 제거
   */
  static async removeHelpful(reviewId: string): Promise<void> {
    return apiClient.delete(API_PATH.REVIEW_HELPFUL(reviewId));
  }

  /**
   * 리뷰에 도움돼요 체크
   */
  static async checkHelpful(reviewId: number): Promise<boolean> {
    return apiClient.get(`/v1/reviews/${reviewId}/helpful/check`);
  }

  /**
   * 내 리뷰 불러오기
   */
  static async getMyReviews(): Promise<ReviewListResponse> {
    return await apiClient.get(API_PATH.MY_REVIEWS);
  }
}

/**
 * 리뷰 등록을 처리합니다.
 *
 * @returns 리뷰 등록 뮤테이션 객체
 */
export const useCreateReviewMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ReviewService.createReview,
    onSuccess: data => {
      // 사용자 관련 쿼리들을 무효화하여 최신 데이터로 업데이트
      queryClient.invalidateQueries({ queryKey: ['reviews'] });
      queryClient.invalidateQueries({ queryKey: ['review', data.reviewId] });

      console.log('리뷰 등록 성공:', data);
    },
    onError: error => {
      console.error('리뷰 등록 실패:', error);
    },
  });
};

/**
 * 리뷰 Helpful POST 뮤테이션
 *
 * @returns 리뷰 Helpful POST 뮤테이션 객체
 */
export const useAddHelpfulMutation = (reviewId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => ReviewService.addHelpful(reviewId),
    onSuccess: () => {
      // 요청 성공 시, 최신 리뷰 목록 다시 불러오기
      queryClient.invalidateQueries({ queryKey: ['reviews'] });
    },
  });
};

/**
 * 리뷰 Helpful DELETE 뮤테이션
 *
 * @returns 리뷰 Helpful DELETE 뮤테이션 객체
 */
export const useRemoveHelpfulMutation = (reviewId: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => ReviewService.removeHelpful(reviewId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews'] });
    },
  });
};
