import type { StreamStatus } from 'common';

/**
 * 스트림 상태별 UI 색상 매핑
 */
export const STREAM_STATUS_COLORS: Record<StreamStatus, string> = {
  LIVE: 'bg-red-500 text-white',
  READY: 'bg-blue-500 text-white',
  ENDED: 'bg-gray-500 text-white',
} as const;

/**
 * 스트림 상태별 텍스트 매핑
 */
export const STREAM_STATUS_TEXT: Record<StreamStatus, string> = {
  LIVE: '라이브',
  READY: '준비됨',
  ENDED: '종료됨',
} as const;
