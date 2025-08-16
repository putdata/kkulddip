import { useState, useEffect } from 'react';

/**
 * 온라인/오프라인 상태 관리 Hook
 */
export const useOnlineStatus = () => {
  const [isOnline, setIsOnline] = useState(navigator.onLine);
  const [wasOffline, setWasOffline] = useState(false);

  useEffect(() => {
    const handleOnline = () => {
      setIsOnline(true);
      console.log('Back online');
    };

    const handleOffline = () => {
      setIsOnline(false);
      setWasOffline(true);
      console.log('Gone offline');
    };

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []); // dependency array 비움으로써 무한 리렌더링 방지

  return {
    isOnline,
    isOffline: !isOnline,
    wasOffline,
  };
};
