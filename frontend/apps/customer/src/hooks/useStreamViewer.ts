import { useState, useCallback, useEffect, useRef } from 'react';
import { OpenVidu, Session, Subscriber } from 'openvidu-browser';
import { toast } from 'sonner';
import type { StreamPlayerStatus, StreamPlayerError } from '@/types/stream';
import { normalizeOpenViduToken } from '@/utils/tokenUtils';

interface UseStreamViewerProps {
  onStreamConnected?: () => void;
  onStreamDisconnected?: () => void;
  onStreamEnded?: () => void;
  onError?: (error: StreamPlayerError) => void;
}

/**
 * OpenVidu 스트림 시청을 위한 커스텀 훅
 */
export const useStreamViewer = ({
  onStreamConnected,
  onStreamDisconnected,
  onStreamEnded,
  onError,
}: UseStreamViewerProps = {}) => {
  const [connectionStatus, setConnectionStatus] =
    useState<StreamPlayerStatus>('idle');
  const [error, setError] = useState<StreamPlayerError | null>(null);

  const openViduRef = useRef<OpenVidu | null>(null);
  const sessionRef = useRef<Session | null>(null);
  const subscriberRef = useRef<Subscriber | null>(null);
  const isConnectingRef = useRef(false);

  /**
   * OpenVidu 인스턴스 초기화
   */
  const initializeOpenVidu = useCallback(() => {
    if (!openViduRef.current) {
      openViduRef.current = new OpenVidu();
    }
    return openViduRef.current;
  }, []);

  /**
   * 모든 리소스 정리
   */
  const cleanup = useCallback(async () => {
    try {
      if (sessionRef.current) {
        sessionRef.current.disconnect();
        sessionRef.current = null;
      }
      subscriberRef.current = null;
      openViduRef.current = null;
      isConnectingRef.current = false;
      setConnectionStatus('idle');
      setError(null);
    } catch (err) {
      console.error('Cleanup error:', err);
    }
  }, []);

  /**
   * 스트림에 연결하고 구독 시작
   */
  const connectToStream = useCallback(
    async (token: string, videoElementId: string) => {
      // 강력한 중복 방지
      if (isConnectingRef.current) {
        console.warn('[useStreamViewer] 이미 연결 중 - 중복 시도 차단');
        return;
      }

      if (connectionStatus === 'connecting' || connectionStatus === 'connected') {
        console.warn('[useStreamViewer] 이미 연결됨 - 중복 연결 시도 차단:', connectionStatus);
        return;
      }

      // 토큰 검증 (WebSocket URL 형식인지 확인)
      if (!token || token.trim() === '') {
        const errorData: StreamPlayerError = {
          code: 'TOKEN_ERROR',
          message: '유효하지 않은 토큰입니다.',
        };
        setError(errorData);
        setConnectionStatus('error');
        onError?.(errorData);
        return;
      }

      try {
        isConnectingRef.current = true;

        // TODO: Remove debug log
        // console.log('[useStreamViewer] 이전 세션 정리 시작');
        await cleanup();
        
        // TODO: Remove debug log
        // console.log('[useStreamViewer] 연결 상태 변경: connecting');
        setConnectionStatus('connecting');
        setError(null);

        // TODO: Remove debug log
        // console.log('[useStreamViewer] OpenVidu 인스턴스 초기화');
        const openVidu = initializeOpenVidu();
        let session = openVidu.initSession(); // const → let으로 변경
        sessionRef.current = session;

        // TODO: Remove debug log
        // console.log('[useStreamViewer] 세션 이벤트 리스너 등록');

        // 세션 이벤트 리스너 설정
        const setupSessionEventListeners = (session: Session, videoElementId: string) => {
          // 스트림 생성 이벤트 처리
        session.on('streamCreated', (event) => {
          console.log('[useStreamViewer] streamCreated 이벤트 발생:', {
            streamId: event.stream.streamId,
            videoElementId,
            hasAudio: event.stream.hasAudio,
            hasVideo: event.stream.hasVideo,
            timestamp: new Date().toISOString()
          });
          
          try {
            // 구독 옵션 설정 (더 관대한 설정)
            const subscribeOptions = {
              insertMode: 'APPEND' as const,
              subscribeToAudio: true,
              subscribeToVideo: true,
            };
            
            const subscriber = session.subscribe(event.stream, videoElementId, subscribeOptions);
            subscriberRef.current = subscriber;
            
            // streamPlaying 이벤트 대기 (타임아웃 포함)
            let playingTimeout: NodeJS.Timeout;
            
            const handleStreamPlaying = () => {
              clearTimeout(playingTimeout);
              console.log('[useStreamViewer] streamPlaying 이벤트 발생 - 연결 성공');
              setConnectionStatus('connected');
              toast.success('스트림에 연결되었습니다.');
              onStreamConnected?.();
            };
            
            // streamPlaying 이벤트 리스너
            subscriber.on('streamPlaying', handleStreamPlaying);
            
            // 15초 타임아웃 설정 (ICE 연결이 불안정할 수 있음)
            playingTimeout = setTimeout(() => {
              console.log('[useStreamViewer] streamPlaying 이벤트 타임아웃 - 연결 상태로 설정');
              // 타임아웃이 발생해도 연결된 것으로 간주 (스트림이 실제로 재생되고 있을 수 있음)
              setConnectionStatus('connected');
              onStreamConnected?.();
            }, 15000);
            
            console.log('[useStreamViewer] 구독자 생성 성공, streamPlaying 이벤트 대기 중');
          } catch (subscribeError) {
            console.error('[useStreamViewer] 구독 실패:', subscribeError);
            const errorData: StreamPlayerError = {
              code: 'SUBSCRIBE_ERROR',
              message: '스트림 구독에 실패했습니다.',
            };
            setError(errorData);
            setConnectionStatus('error');
            onError?.(errorData);
          }
        });

        // 스트림 종료 이벤트 처리
        session.on('streamDestroyed', (event) => {
          console.log('[useStreamViewer] streamDestroyed 이벤트 발생:', {
            streamId: event.stream?.streamId,
            timestamp: new Date().toISOString()
          });
          setConnectionStatus('ended');
          toast.info('스트림이 종료되었습니다.');
          onStreamEnded?.();
        });

        // 세션 연결 해제 이벤트 처리
        session.on('sessionDisconnected', (event) => {
          console.log('[useStreamViewer] sessionDisconnected 이벤트 발생:', {
            reason: event.reason,
            timestamp: new Date().toISOString()
          });
          setConnectionStatus('idle');
          onStreamDisconnected?.();
        });

        // 예외 발생 이벤트 처리
        session.on('exception', (exception) => {
          console.error('[useStreamViewer] OpenVidu exception 발생:', {
            name: exception.name,
            message: exception.message,
            exception,
            timestamp: new Date().toISOString()
          });
          
          let errorMessage = exception.message || '스트림 연결 중 오류가 발생했습니다.';
          
          // 특정 에러에 대한 친화적인 메시지
          if (exception.message?.includes('401') || exception.message?.includes('not valid')) {
            errorMessage = '사장님이 아직 방송을 시작하지 않았습니다. 잠시 후 다시 시도해주세요.';
          } else if (exception.name === 'ICE_CONNECTION_DISCONNECTED') {
            errorMessage = '네트워크 연결이 불안정합니다. 잠시만 기다려주세요.';
          } else if (exception.name === 'NO_STREAM_PLAYING_EVENT') {
            errorMessage = '스트림 로딩 중입니다. 잠시만 기다려주세요.';
          }
          
          const errorData: StreamPlayerError = {
            code: exception.name,
            message: errorMessage,
          };
          setError(errorData);
          setConnectionStatus('error');
          onError?.(errorData);
        });
        }; // setupSessionEventListeners 함수 종료

        // 초기 세션에 이벤트 리스너 설정
        setupSessionEventListeners(session, videoElementId);

        // 토큰으로 세션 연결 (타임아웃 설정)
        // TODO: Remove debug log
        // console.log('[useStreamViewer] 세션 연결 시도 시작');
        
        // 세션 연결 (단일 시도)
        try {
          console.log('[useStreamViewer] 원본 토큰으로 세션 연결 시도:', {
            tokenType: token.startsWith('wss://') ? 'WebSocket URL' : 'JWT Token',
            tokenPreview: token.substring(0, 70) + '...',
            sessionIdFromUrl: token.includes('sessionId=') ? 'yes' : 'no'
          });
          
          // 서버에서 받은 원본 토큰 그대로 사용 (WebSocket URL 전체)
          await session.connect(token);
        } catch (connectionError) {
          throw new Error(`세션 연결 실패: ${connectionError instanceof Error ? connectionError.message : '알 수 없는 오류'}`);
        }
        // TODO: Remove debug log
        // console.log('[useStreamViewer] 세션 연결 완료');
      } catch (err) {
        console.error('스트림 연결 실패:', err);
        
        // 재시도 없이 바로 에러 처리
        const errorData: StreamPlayerError = {
          code: 'CONNECTION_ERROR',
          message: err instanceof Error ? err.message : '스트림 연결에 실패했습니다.',
        };
        
        await cleanup();
        setError(errorData);
        setConnectionStatus('error');
        onError?.(errorData);
        toast.error(errorData.message);
      } finally {
        isConnectingRef.current = false;
      }
    },
    [
      connectionStatus,
      initializeOpenVidu,
      cleanup,
      onStreamConnected,
      onStreamDisconnected,
      onStreamEnded,
      onError,
    ],
  );

  /**
   * 스트림 연결 해제
   */
  const disconnectFromStream = useCallback(async (showToast = true) => {
    try {
      await cleanup();
      if (showToast) {
        toast.info('스트림 연결이 해제되었습니다.');
      }
      onStreamDisconnected?.();
    } catch (err) {
      console.error('스트림 연결 해제 실패:', err);
      if (showToast) {
        toast.error('연결 해제 중 오류가 발생했습니다.');
      }
    }
  }, [cleanup, onStreamDisconnected]);

  // 컴포넌트 언마운트 시 정리 (토스트 없이)
  useEffect(() => {
    return () => {
      cleanup();
    };
  }, [cleanup]);

  const isConnected = connectionStatus === 'connected';
  const isConnecting = connectionStatus === 'connecting';
  const isError = connectionStatus === 'error';
  const isEnded = connectionStatus === 'ended';

  return {
    connectionStatus,
    error,
    isConnected,
    isConnecting,
    isError,
    isEnded,
    connectToStream,
    disconnectFromStream,
    session: sessionRef.current,
    subscriber: subscriberRef.current,
  };
};