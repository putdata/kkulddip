export type StreamStatus = 'READY' | 'LIVE' | 'ENDED';

export interface Stream {
  id: number;
  sessionId: string | null;
  title: string;
  description: string | null;
  storeId: number;
  storeName: string;
  viewerCount: number;
  status: StreamStatus;
  createdAt: string;
  startedAt: string | null;
  endedAt: string | null;
}

export interface CreateStreamRequest {
  storeId: number;
  title: string;
  description?: string;
}

export interface StreamTokenResponse {
  token: string;
  sessionId: string;
}

export interface OpenViduStreamEvent {
  stream: {
    streamId: string;
    getMediaStream: () => MediaStream;
  };
}

export interface StreamError {
  code: string;
  message: string;
}
