import { STREAM_STATUS_COLORS, STREAM_STATUS_TEXT } from '@/constants/stream';
import type { StreamFlowStatus } from '@/types/stream';
import type { StreamStatus } from 'common';

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
export const mapApiStatusToFlowStatus = (apiStatus: StreamStatus): StreamFlowStatus => {
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
export const mapFlowStatusToApiStatus = (flowStatus: StreamFlowStatus): StreamStatus | null => {
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
export const canProgressToNextStatus = (currentStatus: StreamFlowStatus): boolean => {
  const progressFlow = [
    'IDLE',
    'CREATING',
    'READY',
    'CONNECTING',
    'CONNECTED',
    'PUBLISHING',
    'LIVE',
    'ENDING',
    'ENDED'
  ];
  
  return progressFlow.includes(currentStatus) && currentStatus !== 'ENDED' && currentStatus !== 'ERROR';
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
