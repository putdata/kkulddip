/**
 * 스트림 플로우 상태 상수
 */
export const STREAM_STATUS = {
  IDLE: 'IDLE',
  CREATING: 'CREATING',
  READY: 'READY',
  CONNECTING: 'CONNECTING',
  CONNECTED: 'CONNECTED',
  PUBLISHING: 'PUBLISHING',
  LIVE: 'LIVE',
  ENDING: 'ENDING',
  ENDED: 'ENDED',
  ERROR: 'ERROR',
} as const;

/**
 * 스트림 상태별 UI 색상 매핑
 */
export const STREAM_STATUS_COLORS = {
  IDLE: 'bg-gray-400 text-white',
  CREATING: 'bg-yellow-500 text-white',
  READY: 'bg-blue-500 text-white',
  CONNECTING: 'bg-orange-500 text-white',
  CONNECTED: 'bg-green-500 text-white',
  PUBLISHING: 'bg-purple-500 text-white',
  LIVE: 'bg-red-500 text-white',
  ENDING: 'bg-orange-600 text-white',
  ENDED: 'bg-gray-600 text-white',
  ERROR: 'bg-red-600 text-white',
} as const;

/**
 * 스트림 상태별 텍스트 매핑
 */
export const STREAM_STATUS_TEXT = {
  IDLE: '대기',
  CREATING: '생성중',
  READY: '준비완료',
  CONNECTING: '연결중',
  CONNECTED: '연결됨',
  PUBLISHING: '송출중',
  LIVE: '라이브',
  ENDING: '종료중',
  ENDED: '종료됨',
  ERROR: '오류',
} as const;
