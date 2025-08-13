import { useParams } from 'react-router-dom';

/**
 * URL 파라미터를 안전하게 number로 변환하는 커스텀 훅
 * - 없거나 변환 불가 시 null 반환
 * - NaN 방지 처리 포함
 */
export const useNumberParam = (key: string): number | null => {
  const params = useParams<Record<string, string>>();
  const value = params[key];
  if (!value) {
    return null;
  }

  const num = Number(value);
  return Number.isNaN(num) ? null : num;
};
