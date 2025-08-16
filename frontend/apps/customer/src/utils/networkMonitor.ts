/**
 * 네트워크 상태 모니터링 유틸리티
 * TODO: Consider simplifying - Some detailed network analysis may be unnecessary for production
 */

export interface NetworkStatus {
  online: boolean;
  connectionType?: string;
  effectiveType?: string;
  downlink?: number;
  rtt?: number;
  saveData?: boolean;
}

export class NetworkMonitor {
  private listeners: ((status: NetworkStatus) => void)[] = [];
  private currentStatus: NetworkStatus = { online: navigator.onLine };

  constructor() {
    this.setupEventListeners();
    this.updateNetworkInfo();
  }

  /**
   * 네트워크 상태 변경 리스너 등록
   */
  addListener(callback: (status: NetworkStatus) => void) {
    this.listeners.push(callback);
    // 현재 상태를 즉시 콜백으로 전달
    callback(this.currentStatus);
  }

  /**
   * 리스너 제거
   */
  removeListener(callback: (status: NetworkStatus) => void) {
    const index = this.listeners.indexOf(callback);
    if (index > -1) {
      this.listeners.splice(index, 1);
    }
  }

  /**
   * 현재 네트워크 상태 반환
   */
  getStatus(): NetworkStatus {
    return { ...this.currentStatus };
  }

  /**
   * 이벤트 리스너 설정
   */
  private setupEventListeners() {
    // 온라인/오프라인 상태 변경
    window.addEventListener('online', this.handleOnlineStatusChange.bind(this));
    window.addEventListener(
      'offline',
      this.handleOnlineStatusChange.bind(this),
    );

    // 네트워크 정보 변경 (지원하는 브라우저에서만)
    if ('connection' in navigator) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const connection = (navigator as any).connection;
      connection?.addEventListener(
        'change',
        this.handleNetworkChange.bind(this),
      );
    }
  }

  /**
   * 온라인 상태 변경 처리
   */
  private handleOnlineStatusChange() {
    console.log('[NetworkMonitor] 온라인 상태 변경:', navigator.onLine);
    this.updateNetworkInfo();
    this.notifyListeners();
  }

  /**
   * 네트워크 정보 변경 처리
   */
  private handleNetworkChange() {
    console.log('[NetworkMonitor] 네트워크 정보 변경');
    this.updateNetworkInfo();
    this.notifyListeners();
  }

  /**
   * 네트워크 정보 업데이트
   */
  private updateNetworkInfo() {
    this.currentStatus = {
      online: navigator.onLine,
      ...this.getNetworkConnection(),
    };
  }

  /**
   * 네트워크 연결 정보 수집
   */
  private getNetworkConnection(): Partial<NetworkStatus> {
    const connection =
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      (navigator as any).connection ||
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      (navigator as any).mozConnection ||
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      (navigator as any).webkitConnection;

    if (!connection) {
      return {};
    }

    return {
      connectionType: connection.type,
      effectiveType: connection.effectiveType,
      downlink: connection.downlink,
      rtt: connection.rtt,
      saveData: connection.saveData,
    };
  }

  /**
   * 모든 리스너에게 상태 변경 알림
   */
  private notifyListeners() {
    this.listeners.forEach(callback => {
      try {
        callback(this.currentStatus);
      } catch (error) {
        console.error('[NetworkMonitor] 리스너 콜백 에러:', error);
      }
    });
  }

  /**
   * 네트워크 품질 평가
   */
  getQualityAssessment(): 'excellent' | 'good' | 'fair' | 'poor' | 'unknown' {
    if (!this.currentStatus.online) {
      return 'poor';
    }

    const { effectiveType, rtt, downlink } = this.currentStatus;

    if (effectiveType === '4g' && (rtt ?? 0) < 100 && (downlink ?? 0) > 2) {
      return 'excellent';
    } else if (
      effectiveType === '3g' ||
      ((rtt ?? 0) < 300 && (downlink ?? 0) > 1)
    ) {
      return 'good';
    } else if (effectiveType === '2g' || (rtt ?? 0) > 300) {
      return 'fair';
    } else if (effectiveType === 'slow-2g') {
      return 'poor';
    }

    return 'unknown';
  }

  /**
   * WebSocket 연결 적합성 검사
   */
  isWebSocketSuitable(): boolean {
    const quality = this.getQualityAssessment();
    return (
      this.currentStatus.online && quality !== 'poor' && quality !== 'unknown'
    );
  }

  /**
   * 리소스 정리
   */
  destroy() {
    window.removeEventListener(
      'online',
      this.handleOnlineStatusChange.bind(this),
    );
    window.removeEventListener(
      'offline',
      this.handleOnlineStatusChange.bind(this),
    );

    if ('connection' in navigator) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const connection = (navigator as any).connection;
      connection?.removeEventListener(
        'change',
        this.handleNetworkChange.bind(this),
      );
    }

    this.listeners = [];
  }
}

// 전역 인스턴스
export const networkMonitor = new NetworkMonitor();
