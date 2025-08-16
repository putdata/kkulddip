import { useMemo, useEffect } from 'react';
import { useNavigate, useLocation, generatePath } from 'react-router-dom';
import { useNumberParam } from 'common';
import { useMyStores } from '@/queries/store';
import { getCurrentPageRoute } from '@/utils/sidebarUtils';
import type { Store } from '@/types/store';

/**
 * 매장 선택 및 라우팅을 관리하는 커스텀 훅
 *
 * @returns 매장 목록, 선택된 매장, 매장 변경 함수, 현재 매장 ID
 */
export const useStoreSelection = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const storeId = useNumberParam('storeId');
  const { data: storeListData } = useMyStores();

  /**
   * 사용 가능한 스토어 목록
   */
  const stores = useMemo(() => {
    return storeListData?.stores || [];
  }, [storeListData?.stores]);

  /**
   * storeId 파라미터 유효성 검증 및 자동 리다이렉트
   */
  useEffect(() => {
    if (storeId === null || isNaN(storeId) || storeId <= 0) {
      navigate('/not-found', { replace: true });
      return;
    }

    // 스토어 목록이 로드되었고, 현재 storeId가 내 가게가 아닌 경우
    if (stores.length > 0 && !stores.find(s => s.storeId === storeId)) {
      const firstStore = stores[0];
      if (firstStore) {
        const currentRoutePath = getCurrentPageRoute(location.pathname);
        const newPath = generatePath(currentRoutePath, {
          storeId: firstStore.storeId.toString(),
        });
        navigate(newPath, { replace: true });
      }
    }
  }, [storeId, stores, navigate, location.pathname]);

  /**
   * 현재 선택된 스토어
   */
  const selectedStore = useMemo(() => {
    return storeId ? stores.find(s => s.storeId === storeId) || null : null;
  }, [storeId, stores]);

  /**
   * 다른 스토어로 이동하면서 현재 페이지 유지
   * @param store - 이동할 스토어 정보
   */
  const selectStore = (store: Store) => {
    const currentRoutePath = getCurrentPageRoute(location.pathname);
    const newPath = generatePath(currentRoutePath, {
      storeId: store.storeId.toString(),
    });
    navigate(newPath);
  };

  return {
    stores,
    selectedStore,
    selectStore,
    storeId: storeId || 0,
  };
};
