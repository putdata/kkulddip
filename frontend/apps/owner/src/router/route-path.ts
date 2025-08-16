export const ROUTE_PATH = {
  INDEX: '/',
  LOGIN: '/login',
  AUTH_CALLBACK: '/auth-callback',
  WELCOME: '/welcome',
  ONBOARDING: {
    STORE: '/onboarding/store',
    DDIPBOX: '/onboarding/ddipbox',
    NOTIFICATION: '/onboarding/notification',
    // Mobile step routes
    MOBILE: {
      STORE: {
        BASIC: '/onboarding/store/basic',
        LOCATION: '/onboarding/store/location',
        CONTACT: '/onboarding/store/contact',
        DESCRIPTION: '/onboarding/store/description',
      },
      DDIPBOX: {
        BASIC: '/onboarding/ddipbox/basic',
        PRICING: '/onboarding/ddipbox/pricing',
        QUANTITY: '/onboarding/ddipbox/quantity',
      },
      NOTIFICATION: '/onboarding/notification',
    },
  },
  STORE: {
    INDEX: '/:storeId',
    DASHBOARD: '/:storeId/dashboard',
    MENU: '/:storeId/menu',
    ORDERS: '/:storeId/orders',
    ANALYTICS: '/:storeId/analytics',
    SETTLEMENT: '/:storeId/settlement',
    NOTIFICATIONS: '/:storeId/notifications',
    OWNER_SETTLEMENT: '/:storeId/owner-settlement',
    OWNER_NOTIFICATIONS: '/:storeId/owner-notifications',
    SETTINGS: '/:storeId/settings',
    STREAMING: '/:storeId/streaming',
    STREAMING_LIVE: '/:storeId/streaming/live/:streamId',
  },
  NOT_FOUND: '*',
} as const;
