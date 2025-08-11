export const ROUTE_PATH = {
  INDEX: '/',
  LOGIN: '/login',
  AUTH_CALLBACK: '/auth-callback',
  WELCOME: '/welcome',
  STORE: {
    INDEX: '/:storeId',
    DASHBOARD: '/:storeId/dashboard',
    MENU: '/:storeId/menu',
    ORDERS: '/:storeId/orders',
    ANALYTICS: '/:storeId/analytics',
    SETTINGS: '/:storeId/settings',
    STREAMING: '/:storeId/streaming',
    STREAMING_LIVE: '/:storeId/streaming/live/:streamId',
  },
  NOT_FOUND: '*',
} as const;
