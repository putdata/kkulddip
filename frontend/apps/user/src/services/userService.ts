import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { UserRegisterRequest, UserRegisterResponse } from '@/types/user';
import { apiClient } from 'common';

/**
 * 사용자 관련 API 서비스 클래스
 */
export class UserService {
  /**
   * 사용자 등록 API 호출
   *
   * @param data - 등록할 사용자 정보
   * @returns 등록된 사용자 정보
   */
  static async register(
    data: UserRegisterRequest,
  ): Promise<UserRegisterResponse> {
    return apiClient.post<UserRegisterResponse>('/users/register', data);
  }
}

/**
 * 사용자 등록 뮤테이션 훅 (예시)
 *
 * 사용자 등록을 처리합니다.
 *
 * @returns 등록 뮤테이션 객체
 */
export const useRegisterMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: UserService.register,
    onSuccess: data => {
      // 사용자 관련 쿼리들을 무효화하여 최신 데이터로 업데이트
      queryClient.invalidateQueries({ queryKey: ['users'] });
      queryClient.invalidateQueries({ queryKey: ['user', data.phoneNumber] });

      console.log('사용자 등록 성공:', data);
    },
    onError: error => {
      console.error('사용자 등록 실패:', error);
    },
  });
};
