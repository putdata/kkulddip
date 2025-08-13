import { API_PATH } from '@/constants/api-path';
import type { ReviewCreateRequest, ReviewResponse } from '@/types/review';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from 'common';

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
  ): Promise<ReviewResponse[]> {
    return apiClient.get(API_PATH.STORE_REVIEWS(storeId), params);
  }

  /**
   * 리뷰 등록 API 호출
   *
   * @param data - 등록할 리뷰 정보
   * @returns 등록된 리뷰 정보
   */
  static async create(data: ReviewCreateRequest): Promise<ReviewResponse> {
    const formData = new FormData();
    formData.append('rating', data.rating.toString());
    formData.append('reviewText', data.reviewText);

    data.images.forEach(image => {
      formData.append('images', image);
    });

    return apiClient.post(`/stores/${data.storeId}/reviews`, formData);
  }

  /**
   * 리뷰에 도움돼요 추가
   */
  static async addHelpful(reviewId: number): Promise<void> {
    return apiClient.post(`/v1/reviews/${reviewId}/helpful`);
  }

  /**
   * 리뷰에서 도움돼요 제거
   */
  static async removeHelpful(reviewId: number): Promise<void> {
    return apiClient.delete(`/v1/reviews/${reviewId}/helpful`);
  }

  /**
   * 사용자가 해당 리뷰에 도움돼요를 눌렀는지 확인
   */
  static async checkHelpful(reviewId: number): Promise<boolean> {
    return apiClient.get(`/v1/reviews/${reviewId}/helpful/check`);
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
    mutationFn: ReviewService.create,
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
