import { redirect, generatePath } from 'react-router-dom';
import { useAuthStore, queryClient } from 'common';
import { ROUTE_PATH } from './route-path';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from '@/queries/store';

export const indexLoader = async () => {
  const accessToken = useAuthStore.getState().accessToken;

  if (accessToken === null) {
    throw redirect(ROUTE_PATH.WELCOME);
  }

  const storeList = await queryClient
    .ensureQueryData({
      queryKey: storeQueryKeys.lists(),
      queryFn: storeService.getMyStores,
    })
    .catch(() => {
      throw redirect(ROUTE_PATH.LOGIN);
    });

  if (storeList.stores.length === 0) {
    throw redirect(ROUTE_PATH.ONBOARDING.STORE);
  }

  const firstStore = storeList.stores[0]!;
  const dashboardPath = generatePath(ROUTE_PATH.STORE.DASHBOARD, {
    storeId: firstStore.storeId.toString(),
  });

  throw redirect(dashboardPath);
};
