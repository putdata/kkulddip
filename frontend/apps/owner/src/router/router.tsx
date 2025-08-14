import { createBrowserRouter } from 'react-router-dom';
import { ROUTE_PATH } from './route-path';
import { indexLoader } from './loaders';
import Login from '@/pages/Login';
import AuthCallback from '@/pages/AuthCallback';
import Welcome from '@/pages/Welcome';
import Dashboard from '@/pages/main/Dashboard';
import StreamingDashboard from '@/pages/main/StreamingDashboard';
import StreamingLive from '@/pages/main/StreamingLive';
import DdipboxManagement from '@/pages/main/DdipboxManagement';
import OrdersManagement from '@/pages/main/OrdersManagement';
import Analytics from '@/pages/main/Analytics';
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
