/**
 * 스트림 플로우 상태 타입 (OpenVidu 기반 라이브 스트리밍 플로우 관리용)
 * - IDLE: 대기 상태 (초기 상태)
 * - CREATING: 스트림 생성 중 -> stream 생성 API 호출 + OpenVidu 세션 생성
 * - READY: 방송 준비 완료 (OpenVidu 세션 생성됨)
 * - CONNECTING: OpenVidu 연결 중 -> OpenVidu 세션에 연결
 * - CONNECTED: OpenVidu 연결 완료
 * - PUBLISHING: 방송 시작 중 -> stream 시작 API 호출 + OpenVidu 퍼블리셔 설정
 * - LIVE: 방송 중
 * - ENDING: 방송 종료 중 -> stream 종료 API 호출 + OpenVidu 세션 종료
 * - ENDED: 방송 종료됨
 * - ERROR: 오류 발생
 */
export type StreamFlowStatus =
  | 'IDLE'
  | 'CREATING'
  | 'READY'
  | 'CONNECTING'
  | 'CONNECTED'
  | 'PUBLISHING'
  | 'LIVE'
  | 'ENDING'
  | 'ENDED'
  | 'ERROR';

/**
 * OpenVidu 연결 상태
 */
export type OpenViduConnectionStatus = 
  | 'disconnected'
  | 'connecting' 
  | 'connected'
  | 'error';

/**
 * OpenVidu 퍼블리셔 상태
 */
export type OpenViduPublisherStatus =
  | 'ready'
  | 'publishing'
  | 'stopped'
  | 'error';

/**
 * OpenVidu 세션 이벤트
 */
export interface OpenViduSessionEvent {
  type: 'connectionCreated' | 'connectionDestroyed' | 'streamCreated' | 'streamDestroyed';
  connection?: unknown;
  stream?: unknown;
}

/**
 * 프론트엔드 스트림 플로우 관리용 상태
 */
export interface StreamFlow {
  id?: number;
  title: string;
  description?: string;
  status: StreamFlowStatus;
  error?: string;
}
