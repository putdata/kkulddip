import type { ReviewCreateRequest, ReviewCreateResponse } from '@/types/review';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from 'common';

/**
 * 리뷰 관련 API 서비스 클래스
 */
export class ReviewService {
  /**
   * 리뷰 등록 API 호출
   *
   * @param data - 등록할 리뷰 정보
   * @returns 등록된 리뷰 정보
   */
  static async create(
    data: ReviewCreateRequest,
  ): Promise<ReviewCreateResponse> {
    const formData = new FormData();
    formData.append('rating', data.rating.toString());
    formData.append('reviewText', data.reviewText);

    data.images.forEach(image => {
      formData.append('images', image);
    });

    return apiClient.post(`/stores/${data.storeId}/reviews`, formData);
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
