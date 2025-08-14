import { useState, useEffect } from 'react';

/**
 * 미디어 쿼리 매칭 상태를 감지하는 Hook
 *
 * @description
 * 주어진 CSS 미디어 쿼리와 현재 화면 크기가 매칭되는지 확인합니다.
 * 화면 크기 변화를 실시간으로 감지하여 상태를 업데이트합니다.
 */
export function useMediaQuery(query: string) {
  /**
   * 미디어 쿼리 매칭 상태
   */
  const [matches, setMatches] = useState(false);

  useEffect(() => {
    const media = window.matchMedia(query);
    if (media.matches !== matches) {
      setMatches(media.matches);
    }

    const listener = () => setMatches(media.matches);
    media.addEventListener('change', listener);

    return () => media.removeEventListener('change', listener);
  }, [matches, query]);

  return matches;
}
