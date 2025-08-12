import { useMemo } from 'react';
import { useNavigate, useLocation, generatePath } from 'react-router-dom';
import { useNumberParam } from 'common';
import { useMyStores } from '@/queries/useMyStores';
import { getCurrentPageRoute } from '@/utils/sidebarUtils';
import type { Store } from '@/types/store';

/**
 * 스토어 선택 및 라우팅을 관리하는 훅
 * URL 파라미터 기반 스토어 선택과 스토어 간 이동 기능을 제공합니다
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
  };
};
