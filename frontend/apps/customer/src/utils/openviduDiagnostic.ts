/**
 * @deprecated This file contains development-only diagnostic utilities
 * TODO: DELETE THIS ENTIRE FILE - Used only for debugging OpenVidu connections
 * 
 * OpenVidu 연결 진단 유틸리티
 */

export const openviduDiagnostic = {
  /**
   * WebRTC 지원 확인
   */
  checkWebRTCSupport: () => {
    const support = {
      RTCPeerConnection: !!window.RTCPeerConnection,
      getUserMedia: !!(navigator.mediaDevices?.getUserMedia),
      WebSocket: !!window.WebSocket,
      WebRTC: !!(window.RTCPeerConnection && navigator.mediaDevices?.getUserMedia),
      browser: navigator.userAgent,
      timestamp: new Date().toISOString(),
    };

    console.group('📡 WebRTC 지원 현황');
    console.table(support);
    console.groupEnd();

    return support;
  },

  /**
   * 네트워크 연결 품질 테스트
   */
  testNetworkConnection: async (serverUrl: string) => {
    const results = {
      serverUrl,
      pingTest: false,
      websocketTest: false,
      httpTest: false,
      timestamp: new Date().toISOString(),
    };

    console.group('🌐 네트워크 연결 테스트');

    try {
      // HTTP 연결 테스트
      const httpStartTime = Date.now();
      const response = await fetch(serverUrl.replace('wss://', 'https://').replace(':8443', ''));
      results.httpTest = response.ok;
      const httpEndTime = Date.now();
      console.log(`HTTP 응답시간: ${httpEndTime - httpStartTime}ms`);
    } catch (error) {
      console.warn('HTTP 연결 실패:', error);
    }

    try {
      // WebSocket 연결 테스트
      const wsStartTime = Date.now();
      const testWs = new WebSocket(serverUrl?.split('?')[0] || '');
      
      const wsPromise = new Promise<boolean>((resolve) => {
        const timeout = setTimeout(() => {
          testWs.close();
          resolve(false);
        }, 5000);

        testWs.onopen = () => {
          clearTimeout(timeout);
          const wsEndTime = Date.now();
          console.log(`WebSocket 연결시간: ${wsEndTime - wsStartTime}ms`);
          testWs.close();
          resolve(true);
        };

        testWs.onerror = () => {
          clearTimeout(timeout);
          resolve(false);
        };
      });

      results.websocketTest = await wsPromise;
    } catch (error) {
      console.warn('WebSocket 연결 실패:', error);
    }

    console.table(results);
    console.groupEnd();

    return results;
  },

  /**
   * OpenVidu 서버 상태 확인
   */
  checkOpenViduServerStatus: async (baseUrl: string) => {
    const serverInfo = {
      baseUrl,
      configEndpoint: `${baseUrl}/openvidu/api/config`,
      healthCheck: false,
      version: null,
      timestamp: new Date().toISOString(),
    };

    console.group('🎥 OpenVidu 서버 상태');

    try {
      // OpenVidu 설정 확인 (인증 없이 접근 가능한 경우)
      const configResponse = await fetch(serverInfo.configEndpoint, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (configResponse.ok) {
        const config = await configResponse.json();
        serverInfo.healthCheck = true;
        serverInfo.version = config.version;
        console.log('OpenVidu 서버 설정:', config);
      }
    } catch (error) {
      console.warn('OpenVidu 서버 상태 확인 실패:', error);
    }

    console.table(serverInfo);
    console.groupEnd();

    return serverInfo;
  },

  /**
   * 미디어 권한 확인
   */
  checkMediaPermissions: async () => {
    const permissions = {
      camera: false,
      microphone: false,
      screen: false,
      timestamp: new Date().toISOString(),
    };

    console.group('🎤 미디어 권한 상태');

    try {
      // 카메라 권한 확인
      const videoStream = await navigator.mediaDevices.getUserMedia({ 
        video: true, 
        audio: false 
      });
      permissions.camera = true;
      videoStream.getTracks().forEach(track => track.stop());
    } catch (error) {
      console.warn('카메라 접근 실패:', error);
    }

    try {
      // 마이크 권한 확인
      const audioStream = await navigator.mediaDevices.getUserMedia({ 
        video: false, 
        audio: true 
      });
      permissions.microphone = true;
      audioStream.getTracks().forEach(track => track.stop());
    } catch (error) {
      console.warn('마이크 접근 실패:', error);
    }

    try {
      // 화면 공유 권한 확인 (선택사항)
      if (navigator.mediaDevices?.getDisplayMedia) {
        permissions.screen = true;
      }
    } catch (error) {
      console.warn('화면 공유 지원 확인 실패:', error);
    }

    console.table(permissions);
    console.groupEnd();

    return permissions;
  },

  /**
   * 전체 OpenVidu 진단 실행
   */
  fullDiagnostic: async (serverUrl?: string) => {
    console.group('🔍 OpenVidu 전체 진단');

    const results = {
      webrtc: openviduDiagnostic.checkWebRTCSupport(),
      media: await openviduDiagnostic.checkMediaPermissions(),
      network: serverUrl ? await openviduDiagnostic.testNetworkConnection(serverUrl) : null,
      server: serverUrl ? await openviduDiagnostic.checkOpenViduServerStatus(
        serverUrl.replace('wss://', 'https://').split('?')[0] || ''
      ) : null,
      timestamp: new Date().toISOString(),
    };

    console.log('📊 진단 결과 요약:', results);
    console.groupEnd();

    return results;
  },
};

/**
 * 토큰에서 OpenVidu 서버 URL 추출
 */
export const extractServerUrlFromToken = (token: string): string | null => {
  try {
    // 토큰 형식: "wss://server:port?sessionId=...&token=..."
    const urlMatch = token.match(/^(wss?:\/\/[^?]+)/);
    return urlMatch ? urlMatch[1] : null;
  } catch (error) {
    console.error('토큰에서 서버 URL 추출 실패:', error);
    return null;
  }
};