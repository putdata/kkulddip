import { createBrowserRouter } from 'react-router-dom';
import { ROUTE_PATH } from './route-path';
import Home from '@/pages/Home';
import Search from '@/pages/Search';
import Likes from '@/pages/Likes';
import Orders from '@/pages/Orders';
import MyPage from '@/pages/MyPage';
import Login from '@/pages/Login';
import MobileLayout from '@/components/layout/MobileLayout';
import NotFound from '@/pages/NotFound';
import GlobalErrorFallback from '@/components/fallback/GlobalErrorFallback';
import MobileLayoutWithNavbar from '@/components/layout/MobileLayoutWithNavbar';
import ReviewCreate from '@/pages/review/ReviewCreate';
import Cart from '@/pages/Cart';
import Payment from '@/pages/Payment';
import OrderDetail from '@/pages/OrderDetail';
import MobileLayoutWithPageNavbar from '@/components/layout/MobileLayoutWithPageNavbar';

export const router = createBrowserRouter([
  {
    path: '/',
    errorElement: <GlobalErrorFallback />, // 전역 에러 핸들링
    children: [
      {
        path: '/',
        element: <MobileLayoutWithNavbar />,
        children: [
          {
            index: true,
            element: <Home />,
          },
        ],
      },
      {
        path: '/',
        element: <MobileLayoutWithPageNavbar />,
        children: [
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
          // 임시 - 리뷰 작성 페이지
          {
            path: ROUTE_PATH.REVIEW_CREATE,
            element: <ReviewCreate />,
          },
          {
            path: ROUTE_PATH.PAYMENT,
            element: <Payment />,
          },
          {
            path: ROUTE_PATH.CART,
            element: <Cart />,
          },
          {
            path: ROUTE_PATH.ORDER_DETAIL,
            element: <OrderDetail />,
          },
        ],
      },
      {
        path: ROUTE_PATH.LOGIN,
        element: <MobileLayout />,
        children: [
          {
            index: true,
            element: <Login />,
          },
        ],
      },
      {
        path: '*',
        element: <NotFound />,
      },
      {
        path: ROUTE_PATH.NOTIFICATIONS,
      },
      // 임시 - 리뷰 작성 페이지
      {
        path: ROUTE_PATH.REVIEW_CREATE,
        element: <ReviewCreate />,
      },
    ],
  },
]);
