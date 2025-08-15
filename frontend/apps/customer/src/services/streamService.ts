import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type {
  StreamListItem,
  JoinStreamResponse,
  StreamDetail,
} from '@/types/stream';

export const StreamService = {
  /**
   * 라이브 스트림 목록 조회
   */
  getLiveStreams: async () => {
    try {
      const response = await apiClient.get<StreamListItem[]>(API_PATH.STREAMS_LIVE);
      return response;
    } catch (error) {
      console.error('라이브 스트림 목록 요청 실패:', error instanceof Error ? error.message : '알 수 없는 에러');
      throw error;
    }
  },

  /**
   * 스트림 참가 (시청자용 토큰 획득)
   */
  joinStream: async (streamId: number) => {
    console.log('🔴 [StreamService] Spring Boot 서버에 토큰 요청 시작');
    console.log('📤 [StreamService] 요청 정보:', {
      streamId,
      url: API_PATH.STREAMS_JOIN(streamId),
      method: 'POST',
      timestamp: new Date().toISOString(),
    });

    try {
      const startTime = performance.now();
      const response = await apiClient.post<JoinStreamResponse>(
        API_PATH.STREAMS_JOIN(streamId),
        {},
      );
      const endTime = performance.now();
      
      console.log('✅ [StreamService] Spring Boot 서버 응답 성공');
      console.log('📥 [StreamService] 응답 정보:', {
        streamId,
        responseTime: `${(endTime - startTime).toFixed(2)}ms`,
        response,
        token: response.token,
        sessionId: response.sessionId,
        tokenType: typeof response.token,
        tokenLength: response.token?.length || 0,
        isWebSocketUrl: response.token?.startsWith('wss://') || response.token?.startsWith('ws://'),
        tokenPreview: response.token?.substring(0, 100) + '...',
        timestamp: new Date().toISOString(),
      });
      
      return response;
    } catch (error: unknown) {
      const errorMessage = error instanceof Error ? error.message : '알 수 없는 에러';
      const httpStatus = (error as { response?: { status?: number, data?: unknown } })?.response?.status;
      const responseData = (error as { response?: { data?: unknown } })?.response?.data;
      
      console.error('❌ [StreamService] Spring Boot 서버 요청 실패');
      console.error('📥 [StreamService] 에러 정보:', {
        streamId,
        errorMessage,
        httpStatus,
        responseData,
        timestamp: new Date().toISOString(),
      });

      // HTTP 상태 코드별 사용자 친화적 에러 메시지
      if (httpStatus === 400) {
        throw new Error('스트림이 진행 중이 아닙니다. 다시 시도해주세요.');
      } else if (httpStatus === 404) {
        throw new Error('스트림을 찾을 수 없습니다. 스트림이 종료되었을 수 있습니다.');
      } else if (httpStatus === 401) {
        throw new Error('인증이 필요합니다. 로그인 후 다시 시도해주세요.');
      } else if (httpStatus === 403) {
        throw new Error('스트림에 참가할 권한이 없습니다.');
      } else if (httpStatus && httpStatus >= 500) {
        throw new Error('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      }

      throw error;
    }
  },

  /**
   * 스트림 상세 정보 조회
   */
  getStreamDetail: async (streamId: number) => {
    try {
      const response = await apiClient.get<StreamDetail>(API_PATH.STREAMS_DETAIL(streamId));
      return response;
    } catch (error) {
      console.error('스트림 상세 정보 요청 실패:', error instanceof Error ? error.message : '알 수 없는 에러');
      throw error;
    }
  },
};