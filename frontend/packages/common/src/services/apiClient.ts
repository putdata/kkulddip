import axios, { type AxiosInstance, type AxiosResponse } from 'axios';
import {
  ApiError,
  type ApiResponse,
  type ApiSuccessResponse,
} from '../types/api';
import { useAuthStore } from '../stores/authStore';

/**
 * API 클라이언트 클래스
 *
 * 애플리케이션의 모든 HTTP 요청을 처리하는 중앙화된 API 클라이언트입니다.
 * Axios를 기반으로 하며 인증, 에러 처리, 응답 변환을 자동으로 처리합니다.
 */
class ApiClient {
  private instance: AxiosInstance;

  /**
   * API 클라이언트 생성자
   *
   * @param baseURL - API의 기본 URL (기본값: '/api/v1')
   */
  constructor(baseURL = import.meta.env.VITE_API_BASE_URL) {
    this.instance = axios.create({
      baseURL: `${baseURL}`,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  /**
   * Axios 인터셉터 설정
   *
   * 요청 시 자동으로 인증 토큰을 추가하고,
   * 응답 시 에러 처리 및 데이터 변환을 수행합니다.
   */
  private setupInterceptors() {
    // Request interceptor
    this.instance.interceptors.request.use(
      config => {
        const { accessToken } = useAuthStore.getState();
        if (accessToken) {
          config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
      },
      error => Promise.reject(error),
    );

    // Response interceptor
    this.instance.interceptors.response.use(
      (response: AxiosResponse<{ success: boolean }>) => {
        return response;
      },
      error => {
        // TODO: 401 처리 필요
        if (error.response?.status === 401) {
          useAuthStore.getState().clearAuth();
        }

        if (error.isAxiosError) {
          throw new ApiError(error);
        }
        throw error;
      },
    );
  }

  /**
   * GET 요청 수행
   *
   * @template T - 응답 데이터의 타입
   * @param url - 요청할 URL
   * @param params - 쿼리 파라미터 객체
   * @returns 응답 데이터
   */
  async get<T>(url: string, params?: object): Promise<T> {
    const response = await this.instance.get<ApiResponse<T>>(url, { params });
    return (response.data as ApiSuccessResponse<T>).body;
  }

  /**
   * POST 요청 수행
   *
   * @template T - 응답 데이터의 타입
   * @param url - 요청할 URL
   * @param data - 요청 본문 데이터
   * @returns 응답 데이터
   */
  async post<T>(url: string, data?: object): Promise<T> {
    const response = await this.instance.post<ApiResponse<T>>(url, data);
    return (response.data as ApiSuccessResponse<T>).body;
  }

  /**
   * PUT 요청 수행
   *
   * @template T - 응답 데이터의 타입
   * @param url - 요청할 URL
   * @param data - 요청 본문 데이터
   * @returns 응답 데이터
   */
  async put<T>(url: string, data?: object): Promise<T> {
    const response = await this.instance.put<ApiResponse<T>>(url, data);
    return (response.data as ApiSuccessResponse<T>).body;
  }

  /**
   * DELETE 요청 수행
   *
   * @template T - 응답 데이터의 타입
   * @param url - 요청할 URL
   * @returns 응답 데이터
   */
  async delete<T>(url: string): Promise<T> {
    const response = await this.instance.delete<ApiResponse<T>>(url);
    return (response.data as ApiSuccessResponse<T>).body;
  }
}

/**
 * 전역 API 클라이언트 인스턴스
 *
 * 애플리케이션 전체에서 사용할 수 있는 API 클라이언트 인스턴스입니다.
 */
export const apiClient = new ApiClient();
