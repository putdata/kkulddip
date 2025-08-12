import {
  STREAM_STATUS_COLORS,
  STREAM_STATUS_TEXT,
  OPENVIDU_PUBLISHER_CONFIG,
} from '@/constants/stream';
import type {
  StreamFlowStatus,
  OpenViduPublisherOptions,
  StreamError,
  StreamStatusValidation,
} from '@/types/stream';
import type { StreamStatus } from 'common';
import type { ApiError } from 'common';

/**
 * 스트림 상태에 따른 색상 클래스를 반환
 */
export const getStatusColor = (status: StreamFlowStatus): string => {
  return STREAM_STATUS_COLORS[status] || STREAM_STATUS_COLORS.ERROR;
};

/**
 * 스트림 상태에 따른 텍스트를 반환
 */
export const getStatusText = (status: StreamFlowStatus): string => {
  return STREAM_STATUS_TEXT[status] || STREAM_STATUS_TEXT.ERROR;
};

/**
 * ISO 날짜 문자열을 한국어 형식으로 포맷팅
 */
export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

/**
 * 스트림 지속 시간을 포맷팅 (초 단위를 MM:SS 또는 HH:MM:SS 형식으로)
 */
export const formatStreamDuration = (seconds: number): string => {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const remainingSeconds = seconds % 60;

  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  }
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
};

/**
 * API StreamStatus를 StreamFlowStatus로 매핑
 */
export const mapApiStatusToFlowStatus = (
  apiStatus: StreamStatus,
): StreamFlowStatus => {
  switch (apiStatus) {
    case 'READY':
      return 'READY';
    case 'LIVE':
      return 'LIVE';
    case 'ENDED':
      return 'ENDED';
    default:
      return 'ERROR';
  }
};

/**
 * StreamFlowStatus를 API StreamStatus로 매핑 (필요시)
 */
export const mapFlowStatusToApiStatus = (
  flowStatus: StreamFlowStatus,
): StreamStatus | null => {
  switch (flowStatus) {
    case 'READY':
    case 'CONNECTING':
    case 'CONNECTED':
    case 'PUBLISHING':
      return 'READY';
    case 'LIVE':
      return 'LIVE';
    case 'ENDING':
    case 'ENDED':
      return 'ENDED';
    default:
      return null;
  }
};

/**
 * 스트림 플로우에서 다음 상태로 진행 가능한지 확인
 */
export const canProgressToNextStatus = (
  currentStatus: StreamFlowStatus,
): boolean => {
  const progressFlow = [
    'IDLE',
    'CREATING',
    'READY',
    'CONNECTING',
    'CONNECTED',
    'PUBLISHING',
    'LIVE',
    'ENDING',
    'ENDED',
  ];

  return (
    progressFlow.includes(currentStatus) &&
    currentStatus !== 'ENDED' &&
    currentStatus !== 'ERROR'
  );
};

/**
 * 스트림 상태에 따라 사용자에게 보여줄 액션 텍스트 반환
 */
export const getStreamActionText = (status: StreamFlowStatus): string => {
  switch (status) {
    case 'IDLE':
      return '스트림 생성';
    case 'READY':
      return '화면 점검';
    case 'CONNECTED':
      return '라이브 시작';
    case 'LIVE':
      return '방송 종료';
    default:
      return '';
  }
};

/**
 * OpenVidu Publisher 설정을 생성
 */
export const createPublisherConfig = (
  type: 'PREVIEW' | 'LIVE',
): OpenViduPublisherOptions => {
  return { ...OPENVIDU_PUBLISHER_CONFIG[type] };
};

/**
 * 에러 객체에서 타입과 메시지를 추출
 */
export const parseStreamError = (error: unknown): StreamError => {
  // ApiError 타입 체크
  if (error && typeof error === 'object' && 'response' in error) {
    const apiError = error as ApiError;
    if (apiError.response?.code && apiError.response?.message) {
      return {
        type: 'api',
        code: apiError.response.code,
        message: apiError.response.message,
        originalError: error,
      };
    }
  }

  // 일반 Error 타입 체크
  if (error instanceof Error) {
    return {
      type: 'openvidu',
      message: error.message,
      originalError: error,
    };
  }

  // 기타 에러
  return {
    type: 'network',
    message:
      typeof error === 'string' ? error : '알 수 없는 오류가 발생했습니다.',
    originalError: error,
  };
};

/**
 * API 에러에서 특정 코드 확인
 */
export const isStreamCannotStartError = (error: unknown): boolean => {
  const parsedError = parseStreamError(error);
  return (
    parsedError.code === 'STREAM_CANNOT_BE_STARTED' ||
    parsedError.message.includes('STREAM_CANNOT_BE_STARTED')
  );
};

/**
 * 스트림 상태 기반 동작 가능 여부 검증
 */
export const validateStreamStatus = (
  currentStatus: StreamFlowStatus,
  isConnected: boolean,
  isPublishing: boolean,
): StreamStatusValidation => {
  const canConnect = currentStatus === 'READY' && !isConnected;
  const canStartStreaming =
    currentStatus === 'CONNECTED' && isConnected && !isPublishing;
  const canEndStreaming = isPublishing && currentStatus === 'LIVE';

  let reason: string | undefined;
  if (!canConnect && !canStartStreaming && !canEndStreaming) {
    switch (currentStatus) {
      case 'IDLE':
        reason = '스트림을 먼저 생성해주세요.';
        break;
      case 'CREATING':
        reason = '스트림 생성 중입니다.';
        break;
      case 'CONNECTING':
        reason = '연결 중입니다.';
        break;
      case 'ENDING':
        reason = '종료 중입니다.';
        break;
      case 'ENDED':
        reason = '이미 종료된 스트림입니다.';
        break;
      case 'ERROR':
        reason = '오류 상태입니다. 페이지를 새로고침해주세요.';
        break;
    }
  }

  return {
    canConnect,
    canStartStreaming,
    canEndStreaming,
    reason,
  };
};

/**
 * 스트림 ID 유효성 검증
 */
export const validateStreamId = (streamId?: number): boolean => {
  return streamId !== undefined && streamId > 0;
};
