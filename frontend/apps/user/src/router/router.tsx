import { createBrowserRouter, type RouteObject } from 'react-router-dom';
import { ROUTE_PATH } from './route-path';

const routes: RouteObject[] = [
  {
    path: ROUTE_PATH.INDEX,
    // element: <Layout />, // 루트 레이아웃 컴포넌트
    children: [
      {
        path: ROUTE_PATH.HOME,
      },
      {
        path: ROUTE_PATH.MY,
      },
      {
        path: ROUTE_PATH.SEARCH,
      },
      {
        path: ROUTE_PATH.LIKE,
      },
      {
        path: ROUTE_PATH.ORDER,
      },
      {
        path: ROUTE_PATH.NOT_FOUND,
      },
      {
        path: ROUTE_PATH.NOTIFICATIONS,
      },
    ],
  },
];

const router = createBrowserRouter(routes);
export default router;
