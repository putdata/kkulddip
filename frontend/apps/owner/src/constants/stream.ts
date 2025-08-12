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

/**
 * OpenVidu 퍼블리셔 기본 설정
 */
export const OPENVIDU_PUBLISHER_CONFIG = {
  PREVIEW: {
    audioSource: undefined,
    videoSource: undefined,
    publishAudio: false,
    publishVideo: true,
    resolution: '640x480',
    frameRate: 30,
    insertMode: 'REPLACE',
  },
  LIVE: {
    audioSource: undefined,
    videoSource: undefined,
    publishAudio: true,
    publishVideo: true,
    resolution: '640x480',
    frameRate: 30,
    insertMode: 'REPLACE',
  },
} as const;

/**
 * 에러 메시지 상수
 */
export const ERROR_MESSAGES = {
  STREAM_ID_MISSING: '스트림 ID가 없습니다.',
  STREAM_ENDED_NO_CONNECTION: '종료된 스트림에는 연결할 수 없습니다.',
  SESSION_NOT_CONNECTED: '세션이 연결되지 않았습니다.',
  PUBLISHER_NOT_ACTIVE: '퍼블리셔가 활성화되지 않았습니다.',
  MEDIA_ACCESS_DENIED:
    '카메라나 마이크에 접근할 수 없습니다. 권한을 확인해주세요.',
  MEDIA_DEVICE_ACCESS_FAILED:
    '미디어 장치에 접근할 수 없습니다. 카메라와 마이크 권한을 확인해주세요.',
  PUBLISHER_STREAM_INVALID: 'Publisher 스트림이 유효하지 않습니다.',
  MEDIA_STREAM_PROBLEM:
    '미디어 스트림에 문제가 있습니다. 페이지를 새로고침해주세요.',
  SESSION_CONNECTION_FAILED: '세션 연결에 실패했습니다.',
  STREAM_CREATE_FAILED: '스트림 생성에 실패했습니다.',
  STREAM_CONNECTION_FAILED: '스트림 연결에 실패했습니다.',
  BROADCAST_START_FAILED: '방송 시작에 실패했습니다.',
  BROADCAST_END_FAILED: '방송 종료에 실패했습니다.',
  SESSION_DISCONNECT_FAILED: '세션 종료에 실패했습니다.',
  STREAM_CANNOT_START: '스트림이 이미 라이브 상태입니다. 스트림을 종료합니다.',
  STREAM_FORCE_END_FAILED: '스트림 종료에 실패했습니다.',
} as const;

/**
 * 성공 메시지 상수
 */
export const SUCCESS_MESSAGES = {
  SESSION_CONNECTED: '스트림 세션에 연결되었습니다.',
  BROADCAST_STARTED: '방송을 시작했습니다.',
  BROADCAST_ENDED: '방송을 종료했습니다.',
} as const;

/**
 * 타임아웃 및 지연시간 설정 (밀리초)
 */
export const TIMING_CONFIG = {
  ERROR_RESET_DELAY: 3000,
  API_REQUEST_TIMEOUT: 10000,
} as const;
