export const API_PATH = {
  AUTH_EXCHANGE_TOKEN: 'v1/auth/owner/token',

  OWNER_STORES: '/v1/owners/stores',
  OWNER_STORE_DETAIL: (storeId: number) => `/v1/owners/stores/${storeId}`,
  CREATE_STORE: '/v1/store-management/stores',

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
