import type { AxiosError } from 'axios';

/**
 * API 성공 응답 인터페이스
 * 
 * @template T - 응답 데이터의 타입
 */
export interface ApiSuccessResponse<T> {
  success: true;
  status: number;
  body: T;
}

/**
 * API 에러 응답 인터페이스
 */
export interface ApiErrorResponse {
  success: false;
  status: number;
  code: string;
  message: string;
  timestamp: string;
  body?: unknown;
}

/**
 * API 응답 유니온 타입
 * 
 * @template T - 성공 시 응답 데이터 타입
 */
export type ApiResponse<T> = ApiSuccessResponse<T> | ApiErrorResponse;

/**
 * API 에러 클래스
 * 
 * 모든 API 요청 에러를 나타내는 클래스입니다.
 * Axios 에러 정보와 서버 응답 정보를 모두 포함합니다.
 */
export class ApiError extends Error {
  public readonly axiosError: AxiosError;
  public readonly response?: ApiErrorResponse;

  /**
   * ApiError 생성자
   * 
   * @param axiosError - 원본 Axios 에러 객체
   */
  constructor(axiosError: AxiosError) {
    super(axiosError.message);
    this.axiosError = axiosError;

    if (axiosError.response?.data) {
      this.response = axiosError.response.data as ApiErrorResponse;
    }

    this.name = 'ApiError';
  }
}
