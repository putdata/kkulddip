import { useState, useCallback, useEffect, useRef } from 'react';
import { OpenVidu, Session, Publisher } from 'openvidu-browser';
import { toast } from 'sonner';

import type {
  OpenViduConnectionStatus,
  OpenViduPublisherStatus,
} from '@/types/stream';
import { createPublisherConfig } from '@/utils/streamUtils';
import { ERROR_MESSAGES } from '@/constants/stream';

interface UseOpenViduProps {
  onSessionConnected?: () => void;
  onSessionDisconnected?: () => void;
  onPublishingStarted?: () => void;
  onPublishingStopped?: () => void;
  onError?: (error: Error) => void;
}

/**
 * OpenVidu 비디오 스트리밍 연동을 위한 커스텀 훅
 * 세션 연결, 퍼블리싱, 연결 해제 등의 기능을 제공
 */
export const useOpenVidu = ({
  onSessionConnected,
  onSessionDisconnected,
  onPublishingStarted,
  onPublishingStopped,
  onError,
}: UseOpenViduProps = {}) => {
  const [connectionStatus, setConnectionStatus] =
    useState<OpenViduConnectionStatus>('disconnected');
  const [publisherStatus, setPublisherStatus] =
    useState<OpenViduPublisherStatus>('ready');
  const [error, setError] = useState<Error | null>(null);

  const openViduRef = useRef<OpenVidu | null>(null);
  const sessionRef = useRef<Session | null>(null);
  const publisherRef = useRef<Publisher | null>(null);

  /** OpenVidu 인스턴스를 초기화 */
  const initializeOpenVidu = useCallback(() => {
    if (!openViduRef.current) {
      openViduRef.current = new OpenVidu();
    }
    return openViduRef.current;
  }, []);

  /** 모든 리소스를 정리 */
  const cleanup = useCallback(async () => {
    try {
      // Publisher 정리
      if (publisherRef.current && sessionRef.current) {
        try {
          await sessionRef.current.unpublish(publisherRef.current);
        } catch (e) {
          console.warn('Publisher unpublish 실패:', e);
        }
        publisherRef.current = null;
      }

      // Session 정리
      if (sessionRef.current) {
        try {
          sessionRef.current.disconnect();
        } catch (e) {
          console.warn('Session disconnect 실패:', e);
        }
        sessionRef.current = null;
      }

      // 상태 초기화
      setConnectionStatus('disconnected');
      setPublisherStatus('ready');
      setError(null);
      openViduRef.current = null;

      console.log('OpenVidu 리소스 정리 완료');
    } catch (err) {
      console.error('Cleanup error:', err);
    }
  }, []);

  /** 연결 상태를 초기화 */
  const resetConnection = useCallback(() => {
    setConnectionStatus('disconnected');
    setPublisherStatus('ready');
    setError(null);
  }, []);

  /** 에러 상태 자동 리셋 */
  const scheduleErrorReset = useCallback(() => {
    console.log('에러 상태 즉시 리셋');
    resetConnection();
  }, [resetConnection]);

  /** OpenVidu 세션에 연결하고 미리보기 Publisher 생성 */
  const connectToSession = useCallback(
    async (token: string, videoElementId?: string) => {
      try {
        // 이미 연결 중이거나 연결된 상태면 중복 연결 방지
        if (
          connectionStatus === 'connecting' ||
          connectionStatus === 'connected'
        ) {
          console.warn('이미 연결된 상태입니다.');
          return;
        }

        // 이전 세션이 있으면 완전히 정리
        await cleanup();

        setConnectionStatus('connecting');
        setError(null);

        const openVidu = initializeOpenVidu();
        const session = openVidu.initSession();
        sessionRef.current = session;

        session.on('sessionDisconnected', () => {
          setConnectionStatus('disconnected');
          onSessionDisconnected?.();
        });

        await session.connect(token);

        // 세션 연결 후 미디어 권한 확인 및 미리보기용 Publisher 생성
        if (videoElementId) {
          try {
            const config = createPublisherConfig('PREVIEW');
            const publisher = await openVidu.initPublisherAsync(
              videoElementId,
              config,
            );
            publisherRef.current = publisher;
            console.log('미리보기 Publisher 생성 완료');
          } catch (publisherError) {
            console.error('Publisher 생성 실패:', publisherError);
            throw new Error(ERROR_MESSAGES.MEDIA_ACCESS_DENIED);
          }
        }

        setConnectionStatus('connected');
        onSessionConnected?.();
      } catch (err) {
        const error =
          err instanceof Error
            ? err
            : new Error(ERROR_MESSAGES.SESSION_CONNECTION_FAILED);

        console.error('OpenVidu 연결 실패:', error);
        await cleanup();

        setError(error);
        setConnectionStatus('error');
        onError?.(error);
        toast.error(`연결 실패: ${error.message}`);

        scheduleErrorReset();
      }
    },
    [
      connectionStatus,
      initializeOpenVidu,
      onSessionConnected,
      onSessionDisconnected,
      onError,
      cleanup,
      scheduleErrorReset,
    ],
  );

  /** 라이브용 Publisher 생성 또는 기존 Publisher 업데이트 */
  const getOrCreateLivePublisher = useCallback(
    async (videoElementId?: string): Promise<Publisher> => {
      let publisher = publisherRef.current;

      if (!publisher) {
        const openVidu = initializeOpenVidu();
        try {
          const config = createPublisherConfig('LIVE');
          publisher = await openVidu.initPublisherAsync(videoElementId, config);
          publisherRef.current = publisher;
          console.log('새 Publisher 생성 완료');
        } catch (publisherError) {
          console.error('새 Publisher 생성 실패:', publisherError);
          throw new Error(ERROR_MESSAGES.MEDIA_DEVICE_ACCESS_FAILED);
        }
      } else {
        try {
          if (publisher.stream && publisher.stream.getMediaStream()) {
            publisher.publishAudio(true);
            console.log('기존 Publisher 오디오 활성화');
          } else {
            throw new Error(ERROR_MESSAGES.PUBLISHER_STREAM_INVALID);
          }
        } catch (audioError) {
          console.error('오디오 활성화 실패:', audioError);
          throw new Error(ERROR_MESSAGES.MEDIA_STREAM_PROBLEM);
        }
      }

      return publisher;
    },
    [initializeOpenVidu],
  );

  /** 실제 방송을 시작 (퍼블리싱) */
  const startPublishing = useCallback(
    async (videoElementId?: string) => {
      try {
        if (!sessionRef.current) {
          throw new Error(ERROR_MESSAGES.SESSION_NOT_CONNECTED);
        }

        setPublisherStatus('publishing');
        setError(null);

        const publisher = await getOrCreateLivePublisher(videoElementId);

        publisher.on('streamDestroyed', () => {
          setPublisherStatus('stopped');
          onPublishingStopped?.();
        });

        await sessionRef.current.publish(publisher);

        onPublishingStarted?.();
      } catch (err) {
        const error =
          err instanceof Error
            ? err
            : new Error(ERROR_MESSAGES.BROADCAST_START_FAILED);
        setError(error);
        setPublisherStatus('error');
        onError?.(error);
        toast.error(error.message);
      }
    },
    [
      onPublishingStarted,
      onPublishingStopped,
      onError,
      getOrCreateLivePublisher,
    ],
  );

  /** 방송을 중지 (퍼블리싱 중단) */
  const stopPublishing = useCallback(async () => {
    try {
      if (!sessionRef.current || !publisherRef.current) {
        throw new Error(ERROR_MESSAGES.PUBLISHER_NOT_ACTIVE);
      }

      await sessionRef.current.unpublish(publisherRef.current);
      publisherRef.current = null;
      setPublisherStatus('stopped');

      onPublishingStopped?.();
    } catch (err) {
      const error =
        err instanceof Error
          ? err
          : new Error(ERROR_MESSAGES.BROADCAST_END_FAILED);
      setError(error);
      onError?.(error);
      toast.error(error.message);
    }
  }, [onPublishingStopped, onError]);

  /** 세션 연결을 해제 */
  const disconnectSession = useCallback(async () => {
    try {
      if (publisherRef.current && sessionRef.current) {
        await sessionRef.current.unpublish(publisherRef.current);
        publisherRef.current = null;
      }

      if (sessionRef.current) {
        sessionRef.current.disconnect();
        sessionRef.current = null;
      }

      setConnectionStatus('disconnected');
      setPublisherStatus('ready');
      setError(null);

      onSessionDisconnected?.();
    } catch (err) {
      const error =
        err instanceof Error
          ? err
          : new Error(ERROR_MESSAGES.SESSION_DISCONNECT_FAILED);
      setError(error);
      onError?.(error);
      toast.error(error.message);
    }
  }, [onSessionDisconnected, onError]);

  useEffect(() => {
    return () => {
      cleanup();
    };
  }, [cleanup]);

  const isConnected = connectionStatus === 'connected';
  const isPublishing = publisherStatus === 'publishing';
  const isLoading =
    connectionStatus === 'connecting' || publisherStatus === 'publishing';

  return {
    connectionStatus,
    publisherStatus,
    error,
    isConnected,
    isPublishing,
    isLoading,
    connectToSession,
    startPublishing,
    stopPublishing,
    disconnectSession,
    cleanup,
    resetConnection,
    session: sessionRef.current,
    publisher: publisherRef.current,
  };
};
