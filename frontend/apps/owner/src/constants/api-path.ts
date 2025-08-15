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

  ORDERS: {
    PENDING: '/v1/orders/pending',
    STORE_HISTORY: '/v1/orders/store-history',
    CONFIRM: (orderId: string) => `/v1/orders/${orderId}/confirm`,
    PICKUP: (orderId: string) => `/v1/orders/${orderId}/pickup`,
  },

  SETTLEMENT: {
    SUMMARY: '/v1/owners/settlement/summary',
    STORE: (storeId: number) => `/v1/owners/stores/${storeId}/settlement`,
    MONTHLY: (storeId: number) =>
      `/v1/owners/stores/${storeId}/settlement/monthly`,
  },

  ANALYTICS: {
    SALES: '/api/analytics/sales-analytics',
    DAILY: '/api/analytics/daily-analytics',
  },

  FCM_TOKENS: {
    REGISTER: '/v1/fcm-tokens',
    DEACTIVATE: (userId: number) => `/v1/fcm-tokens/users/${userId}/deactivate`,
  },

  NOTIFICATIONS: {
    SEND: '/v1/notifications',
    LIST: '/v1/notifications',
  },
} as const;
