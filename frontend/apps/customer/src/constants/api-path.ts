export const API_PATH = {
  ORDERS: '/v1/orders',
  NOTIFICATIONS: '/v1/notifications',
  PAYMENT_ORDER_ID: '/v1/payments/payment-order-id',
  PAYMENT_CONFIRM: '/v1/payments/confirm',
  AUTH_EXCHANGE_TOKEN: '/v1/auth/customer/token',
  ORDERS_MY_HISTORY: '/v1/orders/my-history',
  FAVORITES: '/v1/favorites',
  FAVORITES_DELETE: '/v1/favorites/by-customer-store',
  FAVORITES_CHECK: '/v1/favorites/check',
  PROFILE: '/v1/customers/profile',
  STATS: '/v1/customers/stats',

  STORES: '/v1/stores',
  STORE_DETAIL: (storeId: string) => `/v1/stores/${storeId}`,
  STORE_DDIPBOXES: (storeId: string) => `/v1/stores/${storeId}/ddipboxes`,
  STORE_REVIEWS: (storeId: string) => `/v1/review/${storeId}`,

  REVIEWS: (storeId: string) => `/v1/review/${storeId}`,
  REVIEW_HELPFUL: (reviewId: string) => `/v1/review/helpful/${reviewId}`,
};
