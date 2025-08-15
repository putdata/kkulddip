import { useState, useCallback } from 'react';
import { StreamService } from '@/services/streamService';
import type { JoinStreamResponse, StreamPlayerError } from '@/types/stream';

interface UseStreamConnectionProps {
  onSuccess?: (data: JoinStreamResponse, streamId: number) => void;
  onError?: (error: StreamPlayerError) => void;
}

export type ConnectionState = 'idle' | 'requesting-token' | 'success' | 'error';

export const useStreamConnection = ({
  onSuccess,
  onError,
}: UseStreamConnectionProps = {}) => {
  const [connectionState, setConnectionState] = useState<ConnectionState>('idle');
  const [error, setError] = useState<StreamPlayerError | null>(null);
  const [streamData, setStreamData] = useState<JoinStreamResponse | null>(null);

  /**
   * 토큰 유효성 검증
   */
  const validateToken = useCallback((token: string): boolean => {
    if (!token || token.trim() === '') {
      return false;
    }
    
    // WebSocket URL 형식 검증
    if (token.startsWith('wss://') || token.startsWith('ws://')) {
      try {
        new URL(token);
        return true;
      } catch {
        return false;
      }
    }
    
    // JWT 토큰 형식 검증
    if (token.includes('.')) {
      const parts = token.split('.');
      return parts.length === 3;
    }
    
    return false;
  }, []);


  /**
   * 스트림 연결 시도 (토큰 요청 + 연결 검증)
   */
  const connectToStream = useCallback(async (streamId: number) => {
    if (connectionState !== 'idle') {
      console.warn('이미 연결 시도 중입니다.');
      return;
    }

    try {
      setConnectionState('requesting-token');
      setError(null);
      setStreamData(null);

      // 1단계: Spring Boot 서버에서 토큰 요청
      const response = await StreamService.joinStream(streamId);
      
      // 토큰 검증
      if (!validateToken(response.token)) {
        throw new Error('서버에서 받은 토큰이 유효하지 않습니다.');
      }

      // 2단계: 토큰 유효성만 검증 (OpenVidu 연결 테스트 제거)
      // 실제 연결은 StreamPlayer에서 안정적으로 처리됨

      // 3단계: 연결 성공
      setConnectionState('success');
      setStreamData(response);
      onSuccess?.(response, streamId);

    } catch (error) {
      const errorData: StreamPlayerError = {
        code: 'CONNECTION_ERROR',
        message: error instanceof Error ? error.message : '스트림 연결에 실패했습니다.',
      };
      
      setError(errorData);
      setConnectionState('error');
      onError?.(errorData);
    }
  }, [connectionState, validateToken, onSuccess, onError]);

  /**
   * 연결 상태 초기화
   */
  const resetConnection = useCallback(() => {
    setConnectionState('idle');
    setError(null);
    setStreamData(null);
  }, []);

  return {
    connectionState,
    error,
    streamData,
    connectToStream,
    resetConnection,
    isConnecting: connectionState === 'requesting-token',
    isSuccess: connectionState === 'success',
    isError: connectionState === 'error',
  };
};