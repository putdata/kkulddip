import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type { Stream, StreamTokenResponse, CreateStreamRequest } from 'common';

/**
 * Owner용 Stream 관련 API 서비스
 * 라이브 스트리밍 기능을 위한 API 호출을 담당합니다.
 */
export const streamService = {
  /**
   * 새로운 스트림 생성
   */
  createStream: (streamData: CreateStreamRequest): Promise<Stream> => {
    return apiClient.post<Stream>(API_PATH.STREAMS.CREATE, streamData);
  },

  /**
   * 스트림 시작 및 퍼블리셔 토큰 발급
   */
  startStream: (streamId: number): Promise<StreamTokenResponse> => {
    return apiClient.post<StreamTokenResponse>(
      API_PATH.STREAMS.START(streamId),
      {},
    );
  },

  /**
   * 스트림 종료
   */
  endStream: (streamId: number): Promise<void> => {
    return apiClient.delete<void>(API_PATH.STREAMS.END(streamId));
  },

  /**
   * 내 스트림 목록 조회
   */
  getMyStreams: (): Promise<Stream[]> => {
    return apiClient.get<Stream[]>(API_PATH.STREAMS.MY);
  },

  /**
   * 가게 스트림 목록 조회
   */
  getStoreStreams: (storeId: number): Promise<Stream[]> => {
    return apiClient.get<Stream[]>(API_PATH.STREAMS.STORE(storeId));
  },

  /**
   * 특정 스트림 상세 정보 조회
   */
  getStreamDetails: (streamId: number): Promise<Stream> => {
    return apiClient.get<Stream>(API_PATH.STREAMS.DETAIL(streamId));
  },
};
