import { useState, useCallback, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import type { Stream, CreateStreamRequest } from 'common';
import type { StreamFlowStatus, StreamFlow } from '@/types/stream';
import { useCreateStream, useConnectStream, useStartStream, useEndStream } from '@/queries/stream';
import { useOpenVidu } from './useOpenVidu';
import { mapApiStatusToFlowStatus } from '@/utils/streamUtils';
import { toast } from 'sonner';

interface UseStreamFlowManagerProps {
  initialStream?: Stream;
}

/**
 * 스트림 플로우를 관리하는 커스텀 훅
 * OpenVidu와 백엔드 API를 연동하여 스트림 생성부터 종료까지의 전체 플로우를 관리
 */
export const useStreamFlowManager = ({ initialStream }: UseStreamFlowManagerProps = {}) => {
  const navigate = useNavigate();
  const { storeId } = useParams<{ storeId: string }>();
  
  const [streamFlow, setStreamFlow] = useState<StreamFlow>({
    id: initialStream?.id,
    title: initialStream?.title || '',
    description: initialStream?.description || '',
    status: initialStream ? mapApiStatusToFlowStatus(initialStream.status) : 'IDLE',
  });


  const createStreamMutation = useCreateStream();
  const connectStreamMutation = useConnectStream();
  const startStreamMutation = useStartStream();
  const endStreamMutation = useEndStream();

  const openVidu = useOpenVidu({
    onSessionConnected: () => {
      setStreamFlow(prev => ({ ...prev, status: 'CONNECTED' }));
    },
    onSessionDisconnected: () => {
      if (streamFlow.status === 'LIVE') {
        setStreamFlow(prev => ({ ...prev, status: 'ENDED' }));
      }
    },
    onPublishingStarted: () => {
      // 상태 업데이트는 startStream에서 API 호출 후에 처리
    },
    onPublishingStopped: () => {
      setStreamFlow(prev => ({ ...prev, status: 'ENDING' }));
    },
    onError: (error) => {
      console.error('OpenVidu 오류:', error);
      setStreamFlow(prev => ({ ...prev, status: 'ERROR', error: error.message }));
    },
  });

  /** 스트림 플로우 상태를 업데이트 */
  const updateStreamFlow = useCallback((updates: Partial<StreamFlow>) => {
    setStreamFlow(prev => ({ ...prev, ...updates }));
  }, []);

  /** 스트림 상태를 설정 */
  const setStatus = useCallback((status: StreamFlowStatus, error?: string) => {
    updateStreamFlow({ status, error });
  }, [updateStreamFlow]);

  /** 새 스트림을 생성하고 라이브 페이지로 이동 */
  const createStream = useCallback(async (streamData: CreateStreamRequest) => {
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
      setStatus('ERROR', error instanceof Error ? error.message : '스트림 생성에 실패했습니다.');
    }
  }, [createStreamMutation, updateStreamFlow, setStatus, navigate, storeId]);

  /** OpenVidu 세션에 연결 */
  const connectToStream = useCallback(async () => {
    if (!streamFlow.id) {
      toast.error('스트림 ID가 없습니다.');
      return;
    }

    if (streamFlow.status === 'ENDED') {
      toast.error('종료된 스트림에는 연결할 수 없습니다.');
      return;
    }

    setStatus('CONNECTING');

    try {
      const tokenResponse = await connectStreamMutation.mutateAsync(streamFlow.id);
      await openVidu.connectToSession(tokenResponse.token, 'video-container');
    } catch (error) {
      setStatus('ERROR', error instanceof Error ? error.message : '스트림 연결에 실패했습니다.');
    }
  }, [streamFlow.id, connectStreamMutation, setStatus, openVidu]);

  /** 스트림을 종료 */
  const endStream = useCallback(async () => {
    if (!streamFlow.id) {
      toast.error('스트림 ID가 없습니다.');
      return;
    }

    setStatus('ENDING');

    try {
      await openVidu.stopPublishing();
      await openVidu.disconnectSession();
      await endStreamMutation.mutateAsync(streamFlow.id);
      
      setStatus('ENDED');
    } catch (error) {
      setStatus('ERROR', error instanceof Error ? error.message : '방송 종료에 실패했습니다.');
    }
  }, [streamFlow.id, endStreamMutation, setStatus, openVidu]);

  /** 스트림을 시작하여 LIVE 상태로 전환 */
  const startStream = useCallback(async () => {
    if (!streamFlow.id) {
      toast.error('스트림 ID가 없습니다.');
      return;
    }

    try {
      await openVidu.startPublishing('video-container');
      const updatedStream = await startStreamMutation.mutateAsync(streamFlow.id);
      
      updateStreamFlow({
        status: 'LIVE',
      });
    } catch (error) {
      console.error('방송 시작 오류:', error);
      
      // 오류 상세 정보 추출
      let errorMessage = '방송 시작에 실패했습니다.';
      let errorCode = '';
      
      // ApiError인 경우 response에서 정보 추출
      if (error && typeof error === 'object' && 'response' in error) {
        const apiError = error as any;
        if (apiError.response?.message) {
          errorMessage = apiError.response.message;
          errorCode = apiError.response.code || '';
        }
      }
      
      // 일반 Error 처리
      if (error instanceof Error && !errorCode) {
        errorMessage = error.message;
      }
      
      console.log('오류 코드:', errorCode);
      console.log('오류 메시지:', errorMessage);
      
      // STREAM_CANNOT_BE_STARTED 오류면 백엔드에 종료 요청
      if (errorCode === 'STREAM_CANNOT_BE_STARTED' || errorMessage.includes('STREAM_CANNOT_BE_STARTED')) {
        console.log('스트림을 시작할 수 없는 상태이므로 종료 요청을 보냅니다.');
        toast.error('스트림이 이미 라이브 상태입니다. 스트림을 종료합니다.');
        
        try {
          // OpenVidu 세션 정리
          await openVidu.stopPublishing();
          await openVidu.disconnectSession();
          // 백엔드에 종료 요청
          await endStreamMutation.mutateAsync(streamFlow.id);
          setStatus('ENDED');
        } catch (endError) {
          console.error('강제 종료 실패:', endError);
          setStatus('ERROR', '스트림 종료에 실패했습니다.');
        }
        return;
      }
      
      setStatus('ERROR', errorMessage);
      toast.error(errorMessage);
    }
  }, [streamFlow.id, startStreamMutation, setStatus, openVidu, updateStreamFlow, endStream]);

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
        description: initialStream.description || undefined,
        status: mapApiStatusToFlowStatus(initialStream.status),
      });
    }
  }, [initialStream, updateStreamFlow]);

  const isLoading = streamFlow.status === 'LIVE' ? false :
                   createStreamMutation.isPending || 
                   connectStreamMutation.isPending ||
                   endStreamMutation.isPending ||
                   ['CREATING', 'CONNECTING', 'ENDING'].includes(streamFlow.status);

  const canConnect = streamFlow.status === 'READY' && !openVidu.isConnected;
  const canStartStreaming = streamFlow.status === 'CONNECTED' && openVidu.isConnected && !openVidu.isPublishing;
  const canEndStreaming = openVidu.isPublishing && streamFlow.status === 'LIVE';


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