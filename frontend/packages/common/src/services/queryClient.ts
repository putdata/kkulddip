import { QueryClient } from '@tanstack/react-query';
import { ApiError } from '../types/api';

/**
 * 에러에서 HTTP 상태 코드 추출 함수
 */
const getErrorStatus = (error: unknown): number | undefined => {
  if (error instanceof ApiError) {
    return error.axiosError.response?.status || error.response?.status;
  }

  return undefined;
};

/**
 * 전역 QueryClient 인스턴스
 *
 * 함수형 throwOnError로 조건부 에러 처리
 * 4xx 클라이언트 에러: 컴포넌트에서 로컬 처리
 * 나머지 에러: ErrorBoundary로 전파
 *
 * @returns QueryClient 인스턴스
 */
const createQueryClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: {
        throwOnError: (error: unknown) => {
          const status = getErrorStatus(error);

          if (status && status >= 400 && status < 500) {
            return false;
          }

          return true;
        },
        staleTime: 1000 * 60 * 5,
        retry: false,
      },

      mutations: {
        throwOnError: (error: unknown) => {
          const status = getErrorStatus(error);
          if (status && status >= 400 && status < 500) {
            return false;
          }
          return true;
        },
        retry: false,
      },
    },
  });

/**
 * 전역 QueryClient 인스턴스
 */
export const queryClient = createQueryClient();
