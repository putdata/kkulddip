import { createBrowserRouter, type RouteObject } from 'react-router-dom';
import { ROUTE_PATH } from './route-path';
import Home from '@/pages/Home';
import Search from '@/pages/Search';
import Likes from '@/pages/Likes';
import Orders from '@/pages/Orders';
import MyPage from '@/pages/MyPage';
import MobileLayout from '@/components/layout/MobileLayout';
import NotFound from '@/pages/NotFound';
import Reviews from '@/pages/review/Reviews';

const routes: RouteObject[] = [
  {
    path: ROUTE_PATH.INDEX,
    element: <MobileLayout />,
    children: [
      {
        path: ROUTE_PATH.HOME,
        element: <Home />,
      },
      {
        path: ROUTE_PATH.MY,
        element: <MyPage />,
      },
      {
        path: ROUTE_PATH.SEARCH,
        element: <Search />,
      },
      {
        path: ROUTE_PATH.LIKE,
        element: <Likes />,
      },
      {
        path: ROUTE_PATH.ORDER,
        element: <Orders />,
      },
      {
        path: ROUTE_PATH.NOT_FOUND,
        element: <NotFound />,
      },
      {
        path: ROUTE_PATH.NOTIFICATIONS,
      },
      // 임시 - 상점 상세 페이지
      {
        path: ROUTE_PATH.REVIEWS,
        element: <Reviews />,
      },
    ],
  },
];

const router = createBrowserRouter(routes);
export default router;
