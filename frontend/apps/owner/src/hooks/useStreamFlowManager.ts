import { useState, useCallback, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'sonner';

import type { Stream, CreateStreamRequest } from 'common';
import type { StreamFlowStatus, StreamFlow } from '@/types/stream';
import {
  useCreateStream,
  useConnectStream,
  useStartStream,
  useEndStream,
} from '@/queries/stream';
import { useOpenVidu } from './useOpenVidu';
import {
  mapApiStatusToFlowStatus,
  parseStreamError,
  isStreamCannotStartError,
  validateStreamId,
  validateStreamStatus,
} from '@/utils/streamUtils';
import { ERROR_MESSAGES } from '@/constants/stream';

interface UseStreamFlowManagerProps {
  initialStream?: Stream;
  onStreamEnded?: () => void;
}

/**
 * 스트림 플로우를 관리하는 커스텀 훅
 * OpenVidu와 백엔드 API를 연동하여 스트림 생성부터 종료까지의 전체 플로우를 관리
 */
export const useStreamFlowManager = ({
  initialStream,
  onStreamEnded,
}: UseStreamFlowManagerProps = {}) => {
  const navigate = useNavigate();
  const { storeId } = useParams<{ storeId: string }>();

  const [streamFlow, setStreamFlow] = useState<StreamFlow>({
    id: initialStream?.id,
    title: initialStream?.title || '',
    description: initialStream?.description || '',
    status: initialStream
      ? mapApiStatusToFlowStatus(initialStream.status)
      : 'IDLE',
  });

  const createStreamMutation = useCreateStream();
  const connectStreamMutation = useConnectStream();
  const startStreamMutation = useStartStream();
  const endStreamMutation = useEndStream();

  /** OpenVidu 세션 연결 이벤트 핸들러 */
  const handleSessionConnected = useCallback(() => {
    setStreamFlow(prev => ({ ...prev, status: 'CONNECTED' }));
  }, []);

  /** OpenVidu 세션 연결 해제 이벤트 핸들러 */
  const handleSessionDisconnected = useCallback(() => {
    if (streamFlow.status === 'LIVE') {
      setStreamFlow(prev => ({ ...prev, status: 'ENDED' }));
    }
  }, [streamFlow.status]);

  /** OpenVidu 퍼블리싱 시작 이벤트 핸들러 */
  const handlePublishingStarted = useCallback(() => {
    // 상태 업데이트는 startStream에서 API 호출 후에 처리
  }, []);

  /** OpenVidu 퍼블리싱 중지 이벤트 핸들러 */
  const handlePublishingStopped = useCallback(() => {
    setStreamFlow(prev => ({ ...prev, status: 'ENDING' }));
  }, []);

  /** OpenVidu 에러 이벤트 핸들러 */
  const handleOpenViduError = useCallback((error: Error) => {
    console.error('OpenVidu 오류:', error);
    setStreamFlow(prev => ({ ...prev, status: 'ERROR', error: error.message }));
  }, []);

  const openVidu = useOpenVidu({
    onSessionConnected: handleSessionConnected,
    onSessionDisconnected: handleSessionDisconnected,
    onPublishingStarted: handlePublishingStarted,
    onPublishingStopped: handlePublishingStopped,
    onError: handleOpenViduError,
  });

  /** 스트림 플로우 상태를 업데이트 */
  const updateStreamFlow = useCallback((updates: Partial<StreamFlow>) => {
    setStreamFlow(prev => ({ ...prev, ...updates }));
  }, []);

  /** 스트림 상태를 설정 */
  const setStatus = useCallback(
    (status: StreamFlowStatus, error?: string) => {
      updateStreamFlow({ status, error });
    },
    [updateStreamFlow],
  );

  /** 새 스트림을 생성하고 라이브 페이지로 이동 */
  const createStream = useCallback(
    async (streamData: CreateStreamRequest) => {
      setStatus('CREATING');

      try {
        const newStream = await createStreamMutation.mutateAsync(streamData);
        updateStreamFlow({
          id: newStream.id,
          title: newStream.title,
          description: newStream.description,
          status: mapApiStatusToFlowStatus(newStream.status),
        });

        navigate(`/${storeId}/streaming/live/${newStream.id}`);
      } catch (error) {
        const parsedError = parseStreamError(error);
        setStatus('ERROR', parsedError.message);
      }
    },
    [createStreamMutation, updateStreamFlow, setStatus, navigate, storeId],
  );

  /** OpenVidu 세션에 연결 */
  const connectToStream = useCallback(async () => {
    if (!validateStreamId(streamFlow.id)) {
      toast.error(ERROR_MESSAGES.STREAM_ID_MISSING);
      return;
    }

    if (streamFlow.status === 'ENDED') {
      toast.error(ERROR_MESSAGES.STREAM_ENDED_NO_CONNECTION);
      return;
    }

    setStatus('CONNECTING');

    try {
      const tokenResponse = await connectStreamMutation.mutateAsync(
        streamFlow.id!,
      );
      await openVidu.connectToSession(tokenResponse.token, 'video-container');
    } catch (error) {
      const parsedError = parseStreamError(error);
      setStatus('ERROR', parsedError.message);
    }
  }, [
    streamFlow.id,
    streamFlow.status,
    connectStreamMutation,
    setStatus,
    openVidu,
  ]);

  /** 스트림을 종료 */
  const endStream = useCallback(async () => {
    if (!validateStreamId(streamFlow.id)) {
      toast.error(ERROR_MESSAGES.STREAM_ID_MISSING);
      return;
    }

    setStatus('ENDING');

    try {
      await openVidu.stopPublishing();
      await openVidu.disconnectSession();
      await endStreamMutation.mutateAsync(streamFlow.id!);

      setStatus('ENDED');
      onStreamEnded?.();
    } catch (error) {
      const parsedError = parseStreamError(error);
      setStatus('ERROR', parsedError.message);
    }
  }, [streamFlow.id, endStreamMutation, setStatus, openVidu, onStreamEnded]);

  /** STREAM_CANNOT_BE_STARTED 에러 처리 */
  const handleStreamCannotStartError = useCallback(async () => {
    console.log('스트림을 시작할 수 없는 상태이므로 종료 요청을 보냅니다.');
    toast.error(ERROR_MESSAGES.STREAM_CANNOT_START);

    try {
      await openVidu.stopPublishing();
      await openVidu.disconnectSession();
      await endStreamMutation.mutateAsync(streamFlow.id!);
      setStatus('ENDED');
      onStreamEnded?.();
    } catch (endError) {
      console.error('강제 종료 실패:', endError);
      setStatus('ERROR', ERROR_MESSAGES.STREAM_FORCE_END_FAILED);
    }
  }, [streamFlow.id, endStreamMutation, setStatus, openVidu, onStreamEnded]);

  /** 스트림을 시작하여 LIVE 상태로 전환 */
  const startStream = useCallback(async () => {
    if (!validateStreamId(streamFlow.id)) {
      toast.error(ERROR_MESSAGES.STREAM_ID_MISSING);
      return;
    }

    try {
      await openVidu.startPublishing('video-container');
      await startStreamMutation.mutateAsync(streamFlow.id!);

      updateStreamFlow({ status: 'LIVE' });
    } catch (error) {
      console.error('방송 시작 오류:', error);

      if (isStreamCannotStartError(error)) {
        await handleStreamCannotStartError();
        return;
      }

      const parsedError = parseStreamError(error);
      setStatus('ERROR', parsedError.message);
      toast.error(parsedError.message);
    }
  }, [
    streamFlow.id,
    startStreamMutation,
    setStatus,
    openVidu,
    updateStreamFlow,
    handleStreamCannotStartError,
  ]);

  /** 스트림 플로우를 초기 상태로 리셋 */
  const resetFlow = useCallback(async () => {
    await openVidu.cleanup();
    setStreamFlow({
      title: '',
      description: '',
      status: 'IDLE',
    });
  }, [openVidu]);

  useEffect(() => {
    if (initialStream) {
      updateStreamFlow({
        id: initialStream.id,
        title: initialStream.title,
        description: initialStream.description || '',
        status: mapApiStatusToFlowStatus(initialStream.status),
      });
    }
  }, [initialStream, updateStreamFlow]);

  const isLoading =
    streamFlow.status === 'LIVE'
      ? false
      : createStreamMutation.isPending ||
        connectStreamMutation.isPending ||
        endStreamMutation.isPending ||
        ['CREATING', 'CONNECTING', 'ENDING'].includes(streamFlow.status);

  const statusValidation = validateStreamStatus(
    streamFlow.status,
    openVidu.isConnected,
    openVidu.isPublishing,
  );

  const { canConnect, canStartStreaming, canEndStreaming } = statusValidation;

  return {
    streamFlow,
    isLoading,
    canConnect,
    canStartStreaming,
    canEndStreaming,
    createStream,
    connectToStream,
    startStream,
    endStream,
    resetFlow,
    setStatus,
    updateStreamFlow,
    openVidu,
  };
};
