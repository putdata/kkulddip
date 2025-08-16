import { createBrowserRouter } from 'react-router-dom';
import { ROUTE_PATH } from './route-path';
import { indexLoader } from './loaders';
import Login from '@/pages/Login';
import AuthCallback from '@/pages/AuthCallback';
import Welcome from '@/pages/Welcome';
import AdaptiveOnboardingLayout from '@/pages/onboarding/AdaptiveOnboardingLayout';
import StoreCreation from '@/pages/onboarding/StoreCreation';
import DdipboxCreation from '@/pages/onboarding/DdipboxCreation';
import NotificationPermission from '@/pages/onboarding/NotificationPermission';
import MobileStoreBasic from '@/pages/onboarding/mobile/MobileStoreBasic';
import MobileStoreLocation from '@/pages/onboarding/mobile/MobileStoreLocation';
import MobileStoreContact from '@/pages/onboarding/mobile/MobileStoreContact';
import MobileStoreDescription from '@/pages/onboarding/mobile/MobileStoreDescription';
import MobileDdipboxBasic from '@/pages/onboarding/mobile/MobileDdipboxBasic';
import MobileDdipboxPricing from '@/pages/onboarding/mobile/MobileDdipboxPricing';
import MobileDdipboxQuantity from '@/pages/onboarding/mobile/MobileDdipboxQuantity';
import MobileNotification from '@/pages/onboarding/mobile/MobileNotification';
import Dashboard from '@/pages/main/Dashboard';
import StreamingDashboard from '@/pages/main/StreamingDashboard';
import StreamingLive from '@/pages/main/StreamingLive';
import DdipboxManagement from '@/pages/main/DdipboxManagement';
import OrdersManagement from '@/pages/main/OrdersManagement';
import Analytics from '@/pages/main/Analytics';
import StoreSettlement from '@/pages/main/StoreSettlement';
import StoreNotifications from '@/pages/main/StoreNotifications';
import OwnerSettlement from '@/pages/main/OwnerSettlement';
import Notifications from '@/pages/main/Notifications';
import Settings from '@/pages/main/Settings';
import NotFound from '@/pages/NotFound';
import ProtectedRoute from '@/components/ProtectedRoute';
import OwnerLayout from '@/components/layout/OwnerLayout';
import GlobalErrorFallback from '@/components/fallback/GlobalErrorFallback';
import GlobalSuspenseFallback from '@/components/fallback/GlobalSuspenseFallback';

export const router = createBrowserRouter([
  {
    path: ROUTE_PATH.INDEX,
    errorElement: <GlobalErrorFallback />,
    children: [
      {
        index: true,
        loader: indexLoader,
        element: <GlobalSuspenseFallback />,
        hydrateFallbackElement: <GlobalSuspenseFallback />,
      },
      {
        path: ROUTE_PATH.LOGIN,
        element: <Login />,
      },
      {
        path: ROUTE_PATH.AUTH_CALLBACK,
        element: <AuthCallback />,
      },
      {
        path: ROUTE_PATH.WELCOME,
        element: <Welcome />,
      },
      {
        path: '/not-found',
        element: <NotFound />,
      },
      {
        path: '/onboarding',
        element: (
          <ProtectedRoute>
            <AdaptiveOnboardingLayout />
          </ProtectedRoute>
        ),
        children: [
          {
            path: ROUTE_PATH.ONBOARDING.STORE,
            element: <StoreCreation />,
          },
          {
            path: ROUTE_PATH.ONBOARDING.DDIPBOX,
            element: <DdipboxCreation />,
          },
          {
            path: ROUTE_PATH.ONBOARDING.NOTIFICATION,
            element: <NotificationPermission />,
          },
          // Mobile step routes
          {
            path: ROUTE_PATH.ONBOARDING.MOBILE.STORE.BASIC,
            element: <MobileStoreBasic />,
          },
          {
            path: ROUTE_PATH.ONBOARDING.MOBILE.STORE.LOCATION,
            element: <MobileStoreLocation />,
          },
          {
            path: ROUTE_PATH.ONBOARDING.MOBILE.STORE.CONTACT,
            element: <MobileStoreContact />,
          },
          {
            path: ROUTE_PATH.ONBOARDING.MOBILE.STORE.DESCRIPTION,
            element: <MobileStoreDescription />,
          },
          {
            path: ROUTE_PATH.ONBOARDING.MOBILE.DDIPBOX.BASIC,
            element: <MobileDdipboxBasic />,
          },
          {
            path: ROUTE_PATH.ONBOARDING.MOBILE.DDIPBOX.PRICING,
            element: <MobileDdipboxPricing />,
          },
          {
            path: ROUTE_PATH.ONBOARDING.MOBILE.DDIPBOX.QUANTITY,
            element: <MobileDdipboxQuantity />,
          },
          {
            path: ROUTE_PATH.ONBOARDING.MOBILE.NOTIFICATION,
            element: <MobileNotification />,
          },
        ],
      },
      {
        path: ROUTE_PATH.STORE.INDEX,
        element: (
          <ProtectedRoute>
            <OwnerLayout />
          </ProtectedRoute>
        ),
        children: [
          {
            path: ROUTE_PATH.STORE.DASHBOARD,
            element: <Dashboard />,
          },
          {
            path: ROUTE_PATH.STORE.MENU,
            element: <DdipboxManagement />,
          },
          {
            path: ROUTE_PATH.STORE.ORDERS,
            element: <OrdersManagement />,
          },
          {
            path: ROUTE_PATH.STORE.ANALYTICS,
            element: <Analytics />,
          },
          {
            path: ROUTE_PATH.STORE.SETTLEMENT,
            element: <StoreSettlement />,
          },
          {
            path: ROUTE_PATH.STORE.NOTIFICATIONS,
            element: <StoreNotifications />,
          },
          {
            path: ROUTE_PATH.STORE.OWNER_SETTLEMENT,
            element: <OwnerSettlement />,
          },
          {
            path: ROUTE_PATH.STORE.OWNER_NOTIFICATIONS,
            element: <Notifications />,
          },
          {
            path: ROUTE_PATH.STORE.SETTINGS,
            element: <Settings />,
          },
          {
            path: ROUTE_PATH.STORE.STREAMING,
            element: <StreamingDashboard />,
          },
          {
            path: ROUTE_PATH.STORE.STREAMING_LIVE,
            element: <StreamingLive />,
          },
        ],
      },
      {
        path: ROUTE_PATH.NOT_FOUND,
        element: <NotFound />,
      },
    ],
  },
]);
