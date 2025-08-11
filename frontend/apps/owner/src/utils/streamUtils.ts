import { STREAM_STATUS_COLORS, STREAM_STATUS_TEXT } from '@/constants/stream';
import type { StreamFlowStatus } from '@/types/stream';

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
