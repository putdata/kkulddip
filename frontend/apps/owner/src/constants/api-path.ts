export const API_PATH = {
  AUTH_EXCHANGE_TOKEN: 'v1/auth/owner/token',

  OWNER_STORES: '/v1/owners/stores',
  OWNER_STORE_DETAIL: (storeId: number) => `/v1/owners/stores/${storeId}`,

  STORE_MANAGEMENT: {
    CREATE: '/v1/store-management/stores',
    UPDATE: (storeId: number) => `/v1/store-management/stores/${storeId}`,
    DELETE: (storeId: number) => `/v1/store-management/stores/${storeId}`,
    TOGGLE_STATUS: (storeId: number) =>
      `/v1/store-management/stores/${storeId}/status`,
  },

  DDIPBOX_MANAGEMENT: {
    LIST: (storeId: number) =>
      `/v1/store-management/stores/${storeId}/ddipboxes`,
    CREATE: (storeId: number) =>
      `/v1/store-management/stores/${storeId}/ddipboxes`,
    UPDATE: (storeId: number, ddipboxId: number) =>
      `/v1/store-management/stores/${storeId}/ddipboxes/${ddipboxId}`,
    DELETE: (storeId: number, ddipboxId: number) =>
      `/v1/store-management/stores/${storeId}/ddipboxes/${ddipboxId}`,
    UPDATE_QUANTITY: (storeId: number, ddipboxId: number) =>
      `/v1/store-management/stores/${storeId}/ddipboxes/${ddipboxId}/quantity`,
    TOGGLE_STATUS: (storeId: number, ddipboxId: number) =>
      `/v1/store-management/stores/${storeId}/ddipboxes/${ddipboxId}/status`,
  },

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
