/**
 * 디버깅을 위한 유틸리티 함수들
 */

export const debugInfo = {
  /**
   * 환경 변수 확인
   */
  checkEnvironment: () => {
    const envInfo = {
      VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
      VITE_MODE: import.meta.env.MODE,
      VITE_DEV: import.meta.env.DEV,
      NODE_ENV: process.env.NODE_ENV,
      userAgent: navigator.userAgent,
      location: {
        href: window.location.href,
        pathname: window.location.pathname,
        search: window.location.search,
        hash: window.location.hash,
      },
      timestamp: new Date().toISOString(),
    };

    console.group('🔧 환경 정보');
    console.table(envInfo);
    console.groupEnd();

    return envInfo;
  },

  /**
   * 네트워크 상태 확인
   */
  checkNetwork: () => {
    const networkInfo = {
      online: navigator.onLine,
      connection:
        (navigator as any).connection ||
        (navigator as any).mozConnection ||
        (navigator as any).webkitConnection,
      serviceWorker: 'serviceWorker' in navigator,
      localStorage: typeof Storage !== 'undefined',
      sessionStorage: typeof sessionStorage !== 'undefined',
      timestamp: new Date().toISOString(),
    };

    console.group('🌐 네트워크 정보');
    console.table(networkInfo);
    if (networkInfo.connection) {
      console.log('Connection details:', networkInfo.connection);
    }
    console.groupEnd();

    return networkInfo;
  },

  /**
   * API 기본 정보 확인
   */
  checkApiConfig: () => {
    const apiInfo = {
      baseURL: import.meta.env.VITE_API_BASE_URL,
      streamsLive: `/v1/streams/live`,
      streamsJoin: (id: number) => `/v1/streams/${id}/join`,
      streamsDetail: (id: number) => `/v1/streams/${id}`,
      currentOrigin: window.location.origin,
      timestamp: new Date().toISOString(),
    };

    console.group('🔌 API 설정');
    console.table(apiInfo);
    console.groupEnd();

    return apiInfo;
  },

  /**
   * 로컬 스토리지 상태 확인
   */
  checkStorage: () => {
    const storageInfo = {
      localStorage: {
        length: localStorage.length,
        keys: Object.keys(localStorage),
        authToken: localStorage.getItem('auth-token') ? '존재함' : '없음',
      },
      sessionStorage: {
        length: sessionStorage.length,
        keys: Object.keys(sessionStorage),
      },
      timestamp: new Date().toISOString(),
    };

    console.group('💾 스토리지 정보');
    console.table(storageInfo);
    console.groupEnd();

    return storageInfo;
  },

  /**
   * 전체 디버그 정보 출력
   */
  fullDiagnostic: () => {
    console.group('🚨 전체 시스템 진단');

    const results = {
      environment: debugInfo.checkEnvironment(),
      network: debugInfo.checkNetwork(),
      api: debugInfo.checkApiConfig(),
      storage: debugInfo.checkStorage(),
    };

    console.log('📊 진단 완료:', results);
    console.groupEnd();

    return results;
  },

  /**
   * 페이지 로드 시간 측정
   */
  measurePageLoad: () => {
    const perfEntries = performance.getEntriesByType('navigation');
    if (perfEntries.length > 0) {
      const navTiming = perfEntries[0] as PerformanceNavigationTiming;
      const loadInfo = {
        domContentLoaded:
          navTiming.domContentLoadedEventEnd -
          navTiming.domContentLoadedEventStart,
        loadComplete: navTiming.loadEventEnd - navTiming.loadEventStart,
        domInteractive: navTiming.domInteractive - navTiming.fetchStart,
        responseTime: navTiming.responseEnd - navTiming.requestStart,
        totalTime: navTiming.loadEventEnd - navTiming.fetchStart,
        timestamp: new Date().toISOString(),
      };

      console.group('⏱️ 페이지 로드 성능');
      console.table(loadInfo);
      console.groupEnd();

      return loadInfo;
    }
    return null;
  },
};

/**
 * 컴포넌트 마운트/언마운트 로깅
 */
export const useDebugLifecycle = (componentName: string) => {
  console.log(`🔄 [${componentName}] 마운트됨`, {
    timestamp: new Date().toISOString(),
    location: window.location.pathname,
  });

  return () => {
    console.log(`🔄 [${componentName}] 언마운트됨`, {
      timestamp: new Date().toISOString(),
    });
  };
};

/**
 * 에러 캐치 및 로깅
 */
export const logError = (
  context: string,
  error: unknown,
  additionalInfo?: object,
) => {
  const errorInfo = {
    context,
    error:
      error instanceof Error
        ? {
            name: error.name,
            message: error.message,
            stack: error.stack,
          }
        : error,
    additionalInfo,
    timestamp: new Date().toISOString(),
    userAgent: navigator.userAgent,
    url: window.location.href,
  };

  console.group(`❌ 에러 발생: ${context}`);
  console.error('Error details:', errorInfo);
  console.groupEnd();

  return errorInfo;
};
