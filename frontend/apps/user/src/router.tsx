import { createBrowserRouter } from 'react-router-dom';
import MobileLayout from './components/layout/MobileLayout';
import Main from './pages/Main';
import NotFound from './pages/NotFound';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MobileLayout />,
    children: [
      {
        index: true,
        element: <Main />,
      },
      {
        path: '*',
        element: <NotFound />,
      },
    ],
  },
]);
