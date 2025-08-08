import { Navigate } from 'react-router-dom';
import { useAuthStore } from 'common';
import type { ReactNode } from 'react';
import { ROUTE_PATH } from '@/router/route-path';

const ProtectedRoute = ({ children }: { children: ReactNode }) => {
  const { accessToken } = useAuthStore();

  if (!accessToken) {
    return <Navigate to={ROUTE_PATH.LOGIN} replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
