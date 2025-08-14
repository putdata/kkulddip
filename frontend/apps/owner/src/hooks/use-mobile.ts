import * as React from 'react';

const MOBILE_BREAKPOINT = 768;

/**
 * 모바일 디바이스 여부를 감지하는 Hook
 * 
 * @description
 * 화면 너비가 768px 미만인지 확인하여 모바일 환경을 감지합니다.
 * 화면 크기 변화를 실시간으로 감지하여 상태를 업데이트합니다.
 * 
 * @returns {boolean} 모바일 환경 여부
 */
export function useIsMobile() {
  const [isMobile, setIsMobile] = React.useState<boolean | undefined>(
    undefined,
  );

  React.useEffect(() => {
    const mql = window.matchMedia(`(max-width: ${MOBILE_BREAKPOINT - 1}px)`);
    const onChange = () => {
      setIsMobile(window.innerWidth < MOBILE_BREAKPOINT);
    };
    mql.addEventListener('change', onChange);
    setIsMobile(window.innerWidth < MOBILE_BREAKPOINT);
    return () => mql.removeEventListener('change', onChange);
  }, []);

  return Boolean(isMobile);
}
