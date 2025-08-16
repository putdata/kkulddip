import type { NavigateFunction } from 'react-router-dom';
import { generatePath } from 'react-router-dom';
import { queryClient } from 'common';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from '@/queries/store';
import { ROUTE_PATH } from '@/router/route-path';

/**
 * 온보딩 완료 후 적절한 페이지로 라우팅하는 공통 함수
 * @param navigate - React Router의 navigate 함수
 * @returns Promise<void>
 */
export const handleOnboardingComplete = async (
  navigate: NavigateFunction,
): Promise<void> => {
  try {
    // 스토어 목록 조회 - indexLoader와 동일한 쿼리 키 사용
    const storeList = await queryClient.ensureQueryData({
      queryKey: storeQueryKeys.list(),
      queryFn: storeService.getMyStores,
    });

    if (storeList.stores.length > 0) {
      // 스토어가 있으면 첫 번째 스토어의 대시보드로 이동
      const firstStore = storeList.stores[0]!;
      const dashboardPath = generatePath(ROUTE_PATH.STORE.DASHBOARD, {
        storeId: firstStore.storeId.toString(),
      });
      navigate(dashboardPath, { replace: true });
    } else {
      // 스토어가 없으면 스토어 생성 페이지로 이동
      navigate(ROUTE_PATH.ONBOARDING.STORE, { replace: true });
    }
  } catch (error) {
    console.error('Failed to check store list after onboarding:', error);
    // 에러 발생 시 기본적으로 인덱스 페이지로 이동 (indexLoader가 처리)
    navigate(ROUTE_PATH.INDEX, { replace: true });
  }
};
