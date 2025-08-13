import { useNavigate } from 'react-router-dom';
import { useNumberParam } from 'common';

/**
 * URL에서 storeId를 가져오고 유효하지 않으면 리다이렉트하는 훅
 */
export const useStoreIdParam = (): number => {
  const storeId = useNumberParam('storeId');
  const navigate = useNavigate();

  if (storeId === null || isNaN(storeId) || storeId <= 0) {
    navigate('/404', { replace: true });
    throw new Error('올바르지 않은 가게 ID입니다.');
  }

  return storeId;
};
