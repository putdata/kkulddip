export interface StreamListItem {
  id: number;
  title: string;
  storeName: string;
  storeId: number;
  viewerCount: number;
  status: 'LIVE';
  thumbnailUrl?: string;
  startedAt: string;
}

export interface JoinStreamRequest {
  nickname?: string; // 고객 닉네임 (선택사항)
  // 추가 필드가 있다면 여기에 정의
}

export interface JoinStreamResponse {
  token: string;
  sessionId: string;
}

export interface StreamDetail {
  id: number;
  sessionId: string | null;
  title: string;
  description: string | null;
  storeId: number;
  storeName: string;
  viewerCount: number;
  status: 'READY' | 'LIVE' | 'ENDED';
  createdAt: string;
  startedAt: string | null;
  endedAt: string | null;
}

export type StreamPlayerStatus =
  | 'idle'
  | 'connecting'
  | 'connected'
  | 'error'
  | 'ended';

export interface StreamPlayerError {
  code: string;
  message: string;
}
