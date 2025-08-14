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
    // TODO: Remove debug logs
    // console.log('[StreamService] 라이브 스트림 목록 요청 시작');
    // console.log('[StreamService] API 경로:', API_PATH.STREAMS_LIVE);
    
    try {
      const startTime = Date.now();
      const response = await apiClient.get<StreamListItem[]>(API_PATH.STREAMS_LIVE);
      const endTime = Date.now();
      
      // TODO: Remove debug log (keep basic success info)
      // console.log('[StreamService] 라이브 스트림 목록 요청 성공:', {
      //   responseTime: `${endTime - startTime}ms`,
      //   dataLength: response?.length || 0
      // });
      
      return response;
    } catch (error) {
      // TODO: Keep error logging but reduce verbosity
      console.error('[StreamService] 라이브 스트림 목록 요청 실패:', error instanceof Error ? error.message : '알 수 없는 에러');
      throw error;
    }
  },

  /**
   * 스트림 참가 (시청자용 토큰 획득)
   */
  joinStream: async (streamId: number) => {
    // TODO: Remove debug logs
    // console.log('[StreamService] 스트림 참가 요청 시작:', { streamId });
    // console.log('[StreamService] API 경로:', API_PATH.STREAMS_JOIN(streamId));
    
    try {
      const startTime = Date.now();
      // Customer API 다시 사용 (OpenVidu URL 형식이 정상임을 확인)
      const response = await apiClient.post<JoinStreamResponse>(
        API_PATH.STREAMS_JOIN(streamId),
        {},
      );
      const endTime = Date.now();
      
      // Spring Server 응답 로그 출력
      console.log('[StreamService] Spring Server 응답:', {
        streamId,
        responseTime: `${endTime - startTime}ms`,
        response: response,
        token: response.token,
        sessionId: response.sessionId,
        tokenType: typeof response.token,
        tokenLength: response.token?.length || 0,
        isTokenUrl: response.token?.startsWith('wss://') || response.token?.startsWith('ws://'),
        timestamp: new Date().toISOString()
      });
      
      return response;
    } catch (error: any) {
      // TODO: Keep error logging but reduce verbosity
      const errorMessage = error instanceof Error ? error.message : '알 수 없는 에러';
      const httpStatus = error?.response?.status;
      
      console.error('[StreamService] 스트림 참가 요청 실패:', {
        streamId,
        errorMessage,
        httpStatus
      });

      // HTTP 상태 코드별 사용자 친화적 에러 메시지
      if (error?.response?.status === 400) {
        throw new Error('스트림이 진행 중이 아닙니다. 다시 시도해주세요.');
      } else if (error?.response?.status === 404) {
        throw new Error('스트림을 찾을 수 없습니다. 스트림이 종료되었을 수 있습니다.');
      } else if (error?.response?.status === 401) {
        throw new Error('인증이 필요합니다. 로그인 후 다시 시도해주세요.');
      } else if (error?.response?.status === 403) {
        throw new Error('스트림에 참가할 권한이 없습니다.');
      } else if (error?.response?.status >= 500) {
        throw new Error('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      }

      throw error;
    }
  },

  /**
   * 스트림 상세 정보 조회
   */
  getStreamDetail: async (streamId: number) => {
    // TODO: Remove debug logs
    // console.log('[StreamService] 스트림 상세 정보 요청 시작:', { streamId });
    // console.log('[StreamService] API 경로:', API_PATH.STREAMS_DETAIL(streamId));
    
    try {
      const startTime = Date.now();
      const response = await apiClient.get<StreamDetail>(API_PATH.STREAMS_DETAIL(streamId));
      const endTime = Date.now();
      
      // TODO: Remove debug log (keep basic success info)
      // console.log('[StreamService] 스트림 상세 정보 요청 성공:', {
      //   streamId,
      //   responseTime: `${endTime - startTime}ms`,
      //   streamStatus: response.status
      // });
      
      return response;
    } catch (error) {
      // TODO: Keep error logging but reduce verbosity
      console.error('[StreamService] 스트림 상세 정보 요청 실패:', error instanceof Error ? error.message : '알 수 없는 에러');
      throw error;
    }
  },
};