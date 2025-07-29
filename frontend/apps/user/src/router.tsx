import { createBrowserRouter } from 'react-router-dom';
import GlobalErrorFallback from '@/components/fallback/GlobalErrorFallback';
import MobileLayout from '@/components/layout/MobileLayout';
import Main from '@/pages/Main';
import NotFound from '@/pages/NotFound';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MobileLayout />,
    errorElement: <GlobalErrorFallback />,
    children: [
      {
        index: true,
        element: <Main />,
        errorElement: <GlobalErrorFallback />,
      },
      {
        path: '*',
        element: <NotFound />,
      },
    ],
  },
]);
