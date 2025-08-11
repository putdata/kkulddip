export const API_PATH = {
  AUTH_EXCHANGE_TOKEN: 'v1/auth/owner/token',

  // TODO: 실제 api 주소 필요
  STORE_LIST: '/stores',
  STORE_DETAIL: '/stores/:storeId',

  STREAMS: {
    CREATE: '/v1/streams',
    CONNECT: (streamId: number) => `/v1/streams/${streamId}/connect`,
    START: (streamId: number) => `/v1/streams/${streamId}/start`,
    END: (streamId: number) => `/v1/streams/${streamId}`,
    MY: '/v1/streams/my',
    DETAIL: (streamId: number) => `/v1/streams/${streamId}`,
    STORE: (storeId: number) => `/v1/streams/stores/${storeId}`,
  },
} as const;
