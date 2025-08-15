import { useState, useCallback, useEffect, useRef } from 'react';
import { OpenVidu, Session, Subscriber } from 'openvidu-browser';
import { toast } from 'sonner';
import type { StreamPlayerStatus, StreamPlayerError } from '@/types/stream';

interface UseStreamViewerProps {
  onStreamConnected?: () => void;
  onStreamDisconnected?: () => void;
  onStreamEnded?: () => void;
  onError?: (error: StreamPlayerError) => void;
}

/**
 * OpenVidu 스트림 시청을 위한 커스텀 훅
 */
export const useStreamViewer = ({
  onStreamConnected,
  onStreamDisconnected,
  onStreamEnded,
  onError,
}: UseStreamViewerProps = {}) => {
  // 콜백 함수들을 ref로 관리하여 의존성 문제 해결
  const onStreamConnectedRef = useRef(onStreamConnected);
  const onStreamDisconnectedRef = useRef(onStreamDisconnected);
  const onStreamEndedRef = useRef(onStreamEnded);
  const onErrorRef = useRef(onError);

  // 콜백 함수들 업데이트
  useEffect(() => {
    onStreamConnectedRef.current = onStreamConnected;
    onStreamDisconnectedRef.current = onStreamDisconnected;
    onStreamEndedRef.current = onStreamEnded;
    onErrorRef.current = onError;
  });
  const [connectionStatus, setConnectionStatus] =
    useState<StreamPlayerStatus>('idle');
  const [error, setError] = useState<StreamPlayerError | null>(null);

  // connectionStatus 변경 시 ref도 동기화
  useEffect(() => {
    connectionStateRef.current = connectionStatus;
  }, [connectionStatus]);

  const openViduRef = useRef<OpenVidu | null>(null);
  const sessionRef = useRef<Session | null>(null);
  const subscriberRef = useRef<Subscriber | null>(null);
  const isConnectingRef = useRef(false);
  const connectingTokenRef = useRef<string | null>(null);
  const currentTokenRef = useRef<string | null>(null);
  const connectionTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const connectionStateRef = useRef<StreamPlayerStatus>('idle');
  const iceConnectionStateChangeCountRef = useRef<number>(0);

  /**
   * OpenVidu 인스턴스 초기화
   */
  const initializeOpenVidu = useCallback(() => {
    if (!openViduRef.current) {
      const ov = new OpenVidu();
      
      // WebRTC 연결 최적화 설정
      ov.enableProdMode(); // 프로덕션 모드 활성화로 불필요한 로깅 제거
      
      openViduRef.current = ov;
    }
    return openViduRef.current;
  }, []);

  /**
   * 모든 리소스 정리
   */
  const cleanup = useCallback(async () => {
    try {
      console.log('🧹 [useStreamViewer] 세션 정리 시작');
      
      // 타임아웃 정리
      if (connectionTimeoutRef.current) {
        clearTimeout(connectionTimeoutRef.current);
        connectionTimeoutRef.current = null;
      }
      
      // 세션 정리
      if (sessionRef.current) {
        sessionRef.current.disconnect();
        sessionRef.current = null;
      }
      
      // 참조 정리
      subscriberRef.current = null;
      openViduRef.current = null;
      isConnectingRef.current = false;
      connectingTokenRef.current = null;
      currentTokenRef.current = null;
      iceConnectionStateChangeCountRef.current = 0;
      
      // 상태 초기화
      setConnectionStatus('idle');
      setError(null);
      
      console.log('✅ [useStreamViewer] 세션 정리 완료');
    } catch (err) {
      console.error('❌ [useStreamViewer] 정리 중 에러:', err);
    }
  }, []);

  /**
   * WebSocket 연결 상태를 확인하는 유틸리티 함수
   */
  const waitForWebSocketReady = useCallback(async (session: Session, maxWaitTime = 15000): Promise<boolean> => {
    return new Promise((resolve) => {
      const startTime = Date.now();
      const checkInterval = 100; // 100ms마다 확인
      
      const checkWebSocketState = () => {
        const elapsed = Date.now() - startTime;
        
        // 타임아웃 체크
        if (elapsed >= maxWaitTime) {
          console.warn('[useStreamViewer] WebSocket 연결 대기 타임아웃');
          resolve(false);
          return;
        }
        
        // WebSocket 상태 확인 (OpenVidu 내부 WebSocket에 접근하기 어려우므로 간접적으로 확인)
        try {
          // 세션이 정상적으로 초기화되었고 연결 준비가 되었는지 확인
          if (session && typeof session.connect === 'function') {
            resolve(true);
            return;
          }
        } catch (error) {
          console.warn('[useStreamViewer] WebSocket 상태 확인 중 에러:', error);
        }
        
        // 100ms 후 재시도
        setTimeout(checkWebSocketState, checkInterval);
      };
      
      checkWebSocketState();
    });
  }, []);

  /**
   * 토큰 유효성 검증
   */
  const validateToken = useCallback((token: string): boolean => {
    if (!token || token.trim() === '') {
      return false;
    }
    
    // WebSocket URL 형식 검증
    if (token.startsWith('wss://') || token.startsWith('ws://')) {
      try {
        new URL(token);
        return true;
      } catch {
        return false;
      }
    }
    
    // JWT 토큰 형식 검증 (간단한 구조 확인)
    if (token.includes('.')) {
      const parts = token.split('.');
      return parts.length === 3;
    }
    
    return false;
  }, []);

  /**
   * STUN/TURN 서버 연결성 테스트
   */
  const testStunTurnConnectivity = useCallback(async (): Promise<boolean> => {
    try {
      console.log('🌐 [useStreamViewer] STUN/TURN 서버 연결성 테스트 시작');
      
      // STUN 서버 테스트
      const stunServer = 'stun:stun.l.google.com:19302';
      const configuration = {
        iceServers: [{ urls: stunServer }]
      };
      
      const pc = new RTCPeerConnection(configuration);
      
      return new Promise((resolve) => {
        let candidateFound = false;
        
        pc.onicecandidate = (event) => {
          if (event.candidate) {
            console.log('✅ [useStreamViewer] STUN 서버 연결 성공 - ICE Candidate 수집됨');
            console.log('📍 [useStreamViewer] ICE Candidate:', {
              type: event.candidate.type,
              protocol: event.candidate.protocol,
              address: event.candidate.address,
              port: event.candidate.port
            });
            candidateFound = true;
            pc.close();
            resolve(true);
          }
        };
        
        // 데이터 채널 생성으로 ICE 수집 시작
        pc.createDataChannel('test');
        pc.createOffer().then(offer => pc.setLocalDescription(offer));
        
        // 5초 타임아웃
        setTimeout(() => {
          if (!candidateFound) {
            console.warn('❌ [useStreamViewer] STUN 서버 연결 실패 - ICE Candidate 수집 안됨');
            pc.close();
            resolve(false);
          }
        }, 5000);
      });
    } catch (error) {
      console.error('❌ [useStreamViewer] STUN/TURN 테스트 중 에러:', error);
      return false;
    }
  }, []);

  /**
   * 네트워크 품질을 측정하는 함수
   */
  const measureNetworkQuality = useCallback(async (): Promise<'good' | 'medium' | 'poor'> => {
    try {
      const startTime = performance.now();
      
      // 현재 페이지 자체에 대한 간단한 네트워크 지연 측정
      await fetch(window.location.origin, { 
        method: 'HEAD',
        cache: 'no-cache',
        mode: 'no-cors' // CORS 이슈 방지
      });
      
      const latency = performance.now() - startTime;
      
      console.log(`📊 [useStreamViewer] 네트워크 품질 측정: ${latency.toFixed(2)}ms`);
      
      if (latency < 100) {
        return 'good';
      }
      if (latency < 300) {
        return 'medium';
      }
      return 'poor';
    } catch (error) {
      console.warn('📊 [useStreamViewer] 네트워크 품질 측정 실패, 기본값 사용:', error);
      return 'medium'; // 실패 시 중간값 사용
    }
  }, []);

  /**
   * 스트림에 연결하고 구독 시작
   */
  const connectToStream = useCallback(
    async (token: string, videoElementId: string) => {
      // 강력한 중복 실행 방지
      if (isConnectingRef.current) {
        console.warn('⚠️ [useStreamViewer] 이미 연결 중 - 중복 시도 차단');
        return;
      }
      
      if (connectingTokenRef.current === token) {
        console.warn('⚠️ [useStreamViewer] 동일한 토큰으로 연결 중 - 중복 시도 차단');
        return;
      }

      if (connectionStateRef.current === 'connecting' || connectionStateRef.current === 'connected') {
        console.warn('⚠️ [useStreamViewer] 이미 연결된 상태 - 중복 연결 시도 차단:', connectionStateRef.current);
        return;
      }

      // 연결 상태 플래그 설정
      isConnectingRef.current = true;
      connectingTokenRef.current = token;
      currentTokenRef.current = token;

      // 토큰 검증
      if (!validateToken(token)) {
        const errorData: StreamPlayerError = {
          code: 'TOKEN_ERROR',
          message: '유효하지 않은 토큰입니다.',
        };
        setError(errorData);
        setConnectionStatus('error');
        onErrorRef.current?.(errorData);
        
        // 에러 시 플래그 해제
        isConnectingRef.current = false;
        connectingTokenRef.current = null;
        return;
      }

      try {

        // 네트워크 품질 측정
        const networkQuality = await measureNetworkQuality();
        
        // STUN/TURN 서버 연결성 테스트
        const isStunTurnAccessible = await testStunTurnConnectivity();
        if (!isStunTurnAccessible) {
          console.warn('⚠️ [useStreamViewer] STUN/TURN 서버 접근 불가 - WebRTC 연결 제한적');
          console.log('📋 [useStreamViewer] 네트워크 제약 감지됨:');
          console.log('   • UDP 포트가 차단되었거나 방화벽에 의해 제한됨');
          console.log('   • P2P 연결이 불가능한 네트워크 환경');
          console.log('   • 회사/학교/공공장소 네트워크의 보안 정책');
          
          // 사용자에게 즉시 안내
          toast.warning('네트워크 제한이 감지되었습니다. 개인 네트워크 사용을 권장합니다.');
        }
        
        await cleanup();
        setConnectionStatus('connecting');
        setError(null);

        const openVidu = initializeOpenVidu();
        const session = openVidu.initSession();
        
        // WebRTC 연결 안정성을 위한 추가 설정
        console.log('🔧 [useStreamViewer] WebRTC 연결 최적화 설정 적용');
        sessionRef.current = session;

        // WebSocket 연결 준비 대기
        const isWebSocketReady = await waitForWebSocketReady(session);
        if (!isWebSocketReady) {
          throw new Error('WebSocket 연결 준비 시간이 초과되었습니다.');
        }

        // ICE 연결 상태 추적 변수들을 미리 선언
        let iceConnectionState = 'new';
        iceConnectionStateChangeCountRef.current = 0; // ref 초기화
        const maxIceConnectionChanges = 10; // ICE 상태 변경 횟수 제한
        const cleanupFunctions: (() => void)[] = [];
        
        // ICE 연결 상태 변경 이벤트 리스너 설정 함수
        const setupIceConnectionTracking = (stream: any) => {
          if (stream && stream.connection && stream.connection.connection) {
            const rtcPeerConnection = stream.connection.connection;
            
            // ICE 연결 상태 추적
            rtcPeerConnection.addEventListener('iceconnectionstatechange', () => {
              const previousState = iceConnectionState;
              iceConnectionState = rtcPeerConnection.iceConnectionState;
              iceConnectionStateChangeCountRef.current++;
              
              console.log(`🧊 [useStreamViewer] ICE 연결 상태 변경 (${iceConnectionStateChangeCountRef.current}/${maxIceConnectionChanges}):`);
              console.log(`   이전: ${previousState} → 현재: ${iceConnectionState}`);
              
              // 상태별 상세 정보
              console.log('📊 [useStreamViewer] WebRTC 연결 상세 정보:', {
                iceConnectionState: rtcPeerConnection.iceConnectionState,
                iceGatheringState: rtcPeerConnection.iceGatheringState,
                connectionState: rtcPeerConnection.connectionState,
                signalingState: rtcPeerConnection.signalingState
              });
            });
            
            // ICE Gathering 상태 추적
            rtcPeerConnection.addEventListener('icegatheringstatechange', () => {
              console.log('🔍 [useStreamViewer] ICE Gathering 상태 변경:', rtcPeerConnection.iceGatheringState);
            });
            
            // ICE Candidate 수집 추적
            rtcPeerConnection.addEventListener('icecandidate', (event) => {
              if (event.candidate) {
                console.log('📍 [useStreamViewer] ICE Candidate 수집:', {
                  type: event.candidate.type,
                  protocol: event.candidate.protocol,
                  address: event.candidate.address,
                  port: event.candidate.port,
                  priority: event.candidate.priority
                });
              } else {
                console.log('✅ [useStreamViewer] ICE Candidate 수집 완료');
              }
            });
            
            // ICE 연결 상태 주기적 감지 (이벤트가 누락될 경우 대비)
            const iceStatePolling = setInterval(() => {
              try {
                const currentIceState = rtcPeerConnection.iceConnectionState;
                const currentConnectionState = rtcPeerConnection.connectionState;
                
                // 상태 변화 감지
                if (currentIceState !== iceConnectionState) {
                  console.log('🔄 [useStreamViewer] ICE 상태 polling으로 감지:', {
                    previous: iceConnectionState,
                    current: currentIceState,
                    connectionState: currentConnectionState
                  });
                  iceConnectionState = currentIceState;
                  
                  // ICE 연결 성공 시 처리
                  if (currentIceState === 'connected' || currentIceState === 'completed') {
                    console.log('🎯 [useStreamViewer] ICE 연결 성공 (polling 감지)');
                    setConnectionStatus('connected');
                    toast.success('스트림에 연결되었습니다.');
                    onStreamConnectedRef.current?.();
                    
                    // 연결 성공 시 플래그 해제
                    isConnectingRef.current = false;
                    connectingTokenRef.current = null;
                    clearInterval(iceStatePolling);
                  }
                  // ICE 연결 실패 시 처리
                  else if (currentIceState === 'failed' || currentIceState === 'disconnected') {
                    console.error('❌ [useStreamViewer] ICE 연결 실패 (polling 감지):', currentIceState);
                    
                    // 연결 실패 시 플래그 해제
                    isConnectingRef.current = false;
                    connectingTokenRef.current = null;
                    clearInterval(iceStatePolling);
                  }
                }
                
                // 연결 상태가 'new'에서 30초 이상 지속되면 문제로 판단
                if (currentIceState === 'new' && iceConnectionStateChangeCountRef.current === 0) {
                  iceConnectionStateChangeCountRef.current++;
                  setTimeout(() => {
                    if (rtcPeerConnection.iceConnectionState === 'new') {
                      console.error('❌ [useStreamViewer] ICE 연결이 30초 동안 new 상태 - 연결 실패');
                      setConnectionStatus('error');
                      setError({
                        code: 'ICE_CONNECTION_STUCK',
                        message: 'WebRTC 연결을 시작할 수 없습니다. 네트워크 설정을 확인해 주세요.'
                      });
                      clearInterval(iceStatePolling);
                    }
                  }, 30000);
                }
              } catch (error) {
                console.error('❌ [useStreamViewer] ICE polling 중 오류:', error);
                clearInterval(iceStatePolling);
              }
            }, 1000); // 1초마다 체크
            
            // 정리 시 polling 중단
            cleanupFunctions.push(() => clearInterval(iceStatePolling));
          }
        };

        // 세션 이벤트 리스너 설정
        const setupSessionEventListeners = (session: Session, videoElementId: string, networkQuality: 'good' | 'medium' | 'poor') => {
          // 스트림 생성 이벤트 처리
        session.on('streamCreated', (event) => {
          console.log('🎥 [useStreamViewer] OpenVidu 스트림 생성 이벤트');
          console.log('📥 [useStreamViewer] 스트림 정보:', {
            streamId: event.stream.streamId,
            connectionId: event.stream.connection.connectionId,
            hasAudio: event.stream.hasAudio,
            hasVideo: event.stream.hasVideo,
            videoElementId,
            networkQuality,
            timestamp: new Date().toISOString(),
          });
          
          try {
            // 브라우저 WebRTC 안정성을 위한 단순한 구독 옵션
            const subscribeOptions = {
              insertMode: 'APPEND' as const,
              subscribeToAudio: true,
              subscribeToVideo: true,
              // 브라우저 호환성을 위해 안정적인 해상도 고정
              resolution: '480x360'
            };
            
            console.log('🔔 [useStreamViewer] OpenVidu 스트림 구독 시작');
            console.log('📤 [useStreamViewer] 구독 옵션:', subscribeOptions);
            
            const subscriber = session.subscribe(event.stream, videoElementId, subscribeOptions);
            subscriberRef.current = subscriber;
            
            // 비디오 엘리먼트 강제 생성 및 스트림 연결 확인
            setTimeout(() => {
              const videoContainer = document.getElementById(videoElementId);
              if (videoContainer) {
                const videoElements = videoContainer.querySelectorAll('video');
                
                // 첫 번째 비디오 엘리먼트가 있다면 강제로 재생 시도
                if (videoElements.length > 0) {
                  const video = videoElements[0];
                  if (!video) return;
                  
                  // 브라우저 자동재생 정책을 위한 설정
                  video.muted = true; // 자동재생을 위해 음소거 필요
                  video.autoplay = true;
                  video.playsInline = true; // 모바일 인라인 재생
                  
                  // 비디오 엘리먼트 크기 및 스타일 강제 설정 (!important 사용)
                  video.style.setProperty('width', '100%', 'important');
                  video.style.setProperty('height', '100%', 'important');
                  video.style.setProperty('object-fit', 'cover', 'important');
                  video.style.setProperty('background-color', 'transparent', 'important');
                  video.style.setProperty('display', 'block', 'important');
                  video.style.setProperty('visibility', 'visible', 'important');
                  video.style.setProperty('opacity', '1', 'important');
                  video.style.setProperty('z-index', '1', 'important');
                  video.style.setProperty('position', 'absolute', 'important');
                  video.style.setProperty('top', '0', 'important');
                  video.style.setProperty('left', '0', 'important');
                  video.style.setProperty('border', 'none', 'important');
                  video.style.setProperty('outline', 'none', 'important');
                  
                  // 비디오 메타데이터 로드 이벤트 리스너
                  video.addEventListener('loadedmetadata', () => {
                    // 메타데이터 로드 후 비디오 크기 재확인 및 강제 설정
                    if (video.videoWidth > 0 && video.videoHeight > 0) {
                      video.style.setProperty('width', '100%', 'important');
                      video.style.setProperty('height', '100%', 'important');
                      video.style.setProperty('object-fit', 'cover', 'important');
                      video.style.setProperty('display', 'block', 'important');
                      video.style.setProperty('visibility', 'visible', 'important');
                      video.style.setProperty('opacity', '1', 'important');
                    }
                  });
                  
                  video.play().then(() => {
                    // 재생 성공 후 음소거 해제
                    setTimeout(() => {
                      video.muted = false;
                    }, 1000);
                  }).catch(err => {
                    console.warn('⚠️ [useStreamViewer] 비디오 재생 실패:', err.message);
                  });
                }
                
                // 비디오 엘리먼트가 없으면 강제로 생성
                if (videoElements.length === 0) {
                  console.warn('⚠️ [useStreamViewer] 비디오 엘리먼트가 생성되지 않음 - 강제 생성 시도');
                  // OpenVidu가 비디오 엘리먼트를 생성하도록 재시도
                  (subscriber as any).createVideoElement(videoElementId, 'APPEND');
                }
              }
            }, 1000); // 1초 후 확인
            
            console.log('✅ [useStreamViewer] OpenVidu 구독자 생성 완료');
            
            // ICE 연결 상태 추적 시작
            setupIceConnectionTracking(subscriber.stream as any);
            
            // streamPlaying 이벤트 대기 (네트워크 품질에 따른 적응적 타임아웃)
            let playingTimeout: NodeJS.Timeout;
            
            // WebRTC ICE 연결을 위한 충분한 타임아웃 설정
            const getTimeoutDuration = () => {
              return 60000; // 60초 - 브라우저 WebRTC ICE 연결은 시간이 오래 걸릴 수 있음
            };
            
            const timeoutDuration = getTimeoutDuration();
            
            const handleStreamPlaying = () => {
              clearTimeout(playingTimeout);
              
              // 실제 비디오 엘리먼트와 스트림 상태 확인
              const videoElement = document.getElementById(videoElementId);
              const hasVideoTracks = subscriber.stream.hasVideo;
              const hasAudioTracks = subscriber.stream.hasAudio;
              
              // RTCPeerConnection에서 직접 ICE 연결 상태 확인
              let currentIceState = 'new';
              try {
                const rtcConnection = (subscriber as any)?.stream?.connection?.connection;
                if (rtcConnection) {
                  currentIceState = rtcConnection.iceConnectionState;
                  iceConnectionState = currentIceState; // 변수 업데이트
                }
              } catch (_) {
                console.warn('⚠️ [useStreamViewer] RTCPeerConnection 접근 실패, 기본값 사용');
              }
              // ICE가 이미 연결된 경우 즉시 성공 처리
              if (currentIceState === 'connected' || currentIceState === 'completed') {
                console.log('⚡ [useStreamViewer] ICE 연결 이미 완료 - 즉시 성공 처리');
                setConnectionStatus('connected');
                toast.success('스트림에 연결되었습니다.');
                onStreamConnectedRef.current?.();
                
                // 연결 성공 시 플래그 해제
                isConnectingRef.current = false;
                connectingTokenRef.current = null;
                return; // 추가 처리 불필요
              }
              
              console.log('🎉 [useStreamViewer] OpenVidu 스트림 재생 시작');
              
              // ICE 연결 상태와 미디어 트랙 모두 확인하여 실제 연결 판단
              if ((hasVideoTracks || hasAudioTracks) && (iceConnectionState === 'connected' || iceConnectionState === 'completed')) {
                console.log('✅ [useStreamViewer] 실제 스트림 연결 완료 (ICE + 미디어)');
                setConnectionStatus('connected');
                toast.success('스트림에 연결되었습니다.');
                onStreamConnectedRef.current?.();
              } else {
                console.warn('⚠️ [useStreamViewer] 스트림 연결 불완전:', {
                  hasMedia: hasVideoTracks || hasAudioTracks,
                  iceState: iceConnectionState,
                  isIceConnected: iceConnectionState === 'connected' || iceConnectionState === 'completed'
                });
                
                // ICE 연결이 안된 경우 강제 재시도
                if (iceConnectionState !== 'connected' && iceConnectionState !== 'completed') {
                  console.info('⏳ [useStreamViewer] ICE 연결 대기 중...');
                  
                  // ICE 연결이 'new' 상태에서 5초 동안 진행되지 않으면 강제 재협상
                  if (iceConnectionState === 'new') {
                    setTimeout(() => {
                      console.warn('🔄 [useStreamViewer] ICE 연결이 new 상태에서 멈춤 - 연결 분석 시도');
                      
                      // RTCPeerConnection 접근 시도 (OpenVidu 내부 구조)
                      try {
                        // OpenVidu subscriber의 stream manager에서 RTCPeerConnection 찾기
                        const streamManager = subscriber as any;
                        let rtcConnection: RTCPeerConnection | null = null;
                        
                        // 여러 경로로 RTCPeerConnection 접근 시도
                        if (streamManager?.stream?.webRtcPeer?.pc) {
                          rtcConnection = streamManager.stream.webRtcPeer.pc;
                        } else if (streamManager?.stream?.connection?.connection) {
                          rtcConnection = streamManager.stream.connection.connection;
                        } else if (streamManager?.stream?.getRTCPeerConnection) {
                          rtcConnection = streamManager.stream.getRTCPeerConnection();
                        }
                        
                        if (rtcConnection) {
                          console.log('🔍 [useStreamViewer] RTCPeerConnection 상태:', {
                            iceConnectionState: rtcConnection.iceConnectionState,
                            connectionState: rtcConnection.connectionState,
                            signalingState: rtcConnection.signalingState
                          });
                          
                          // ICE 연결 상태가 여전히 'new'이면 수동으로 연결 시도
                          if (rtcConnection.iceConnectionState === 'new') {
                            console.warn('⚠️ [useStreamViewer] ICE 연결이 시작되지 않음 - 수동 재협상 필요');
                            // 연결 상태를 에러로 변경하여 재시도 유도
                            setConnectionStatus('error');
                            setError({
                              code: 'ICE_CONNECTION_TIMEOUT',
                              message: 'WebRTC 연결이 시작되지 않았습니다. 페이지를 새로고침해 주세요.'
                            });
                          }
                        } else {
                          console.warn('❌ [useStreamViewer] RTCPeerConnection을 찾을 수 없음');
                        }
                      } catch (error) {
                        console.error('❌ [useStreamViewer] ICE 상태 확인 중 오류:', error);
                      }
                    }, 5000);
                  }
                }
              }
            };
            
            // streamPlaying 이벤트 리스너
            subscriber.on('streamPlaying', handleStreamPlaying);
            
            
            // 적응적 타임아웃 설정  
            playingTimeout = setTimeout(() => {
              console.warn(`⏱️ [useStreamViewer] streamPlaying 타임아웃 (${timeoutDuration}ms)`);
              console.log('🔍 [useStreamViewer] ICE 연결 상태:', iceConnectionState);
              
              // ICE 연결 상태 정확히 확인
              if (iceConnectionState === 'connected' || iceConnectionState === 'completed') {
                console.log('✅ [useStreamViewer] ICE 연결 성공으로 스트림 연결 완료 처리');
                setConnectionStatus('connected');
                onStreamConnectedRef.current?.();
              } else {
                console.warn(`❌ [useStreamViewer] ICE 연결 실패 (상태: ${iceConnectionState}) - 연결 불완전`);
                
                // ICE 연결 실패에 대한 사용자 친화적 메시지와 해결 방안
                let userMessage = 'WebRTC 연결이 불안정합니다.';
                
                if (iceConnectionState === 'disconnected') {
                  userMessage = isStunTurnAccessible 
                    ? '네트워크 연결이 불안정합니다. 잠시 후 다시 시도해주세요.'
                    : '현재 네트워크에서 스트리밍이 제한됩니다. 모바일 핫스팟이나 다른 와이파이를 시도해보세요.';
                } else if (iceConnectionState === 'failed') {
                  userMessage = isStunTurnAccessible 
                    ? '스트림 연결에 실패했습니다. 페이지를 새로고침해 주세요.'
                    : '회사/학교 네트워크에서 스트리밍이 차단되었습니다. 개인 네트워크를 사용해주세요.';
                } else {
                  userMessage = `연결 중입니다... (상태: ${iceConnectionState})`;
                }
                
                // 네트워크 문제 해결 가이드 로깅
                if (!isStunTurnAccessible) {
                  console.log('🔧 [useStreamViewer] 네트워크 문제 해결 가이드:');
                  console.log('   1. 모바일 핫스팟 사용');
                  console.log('   2. 다른 와이파이 네트워크 시도');
                  console.log('   3. VPN 사용 (가능한 경우)');
                  console.log('   4. 네트워크 관리자에게 WebRTC 포트 개방 요청');
                }
                
                const errorData: StreamPlayerError = {
                  code: 'ICE_CONNECTION_FAILED',
                  message: userMessage,
                };
                setError(errorData);
                setConnectionStatus('error');
                onErrorRef.current?.(errorData);
              }
            }, timeoutDuration);
          } catch (subscribeError) {
            console.error('스트림 구독 실패:', subscribeError);
            const errorData: StreamPlayerError = {
              code: 'SUBSCRIBE_ERROR',
              message: '스트림 구독에 실패했습니다.',
            };
            setError(errorData);
            setConnectionStatus('error');
            onErrorRef.current?.(errorData);
          }
        });

        // 스트림 종료 이벤트 처리
        session.on('streamDestroyed', (event) => {
          console.log('🛑 [useStreamViewer] OpenVidu 스트림 종료 이벤트');
          console.log('📥 [useStreamViewer] 종료 정보:', {
            streamId: event.stream?.streamId,
            reason: event.reason,
            timestamp: new Date().toISOString(),
          });
          setConnectionStatus('ended');
          toast.info('스트림이 종료되었습니다.');
          onStreamEndedRef.current?.();
        });

        // 세션 연결 해제 이벤트 처리
        session.on('sessionDisconnected', (event) => {
          console.log('🔌 [useStreamViewer] OpenVidu 세션 연결 해제 이벤트');
          console.log('📥 [useStreamViewer] 연결 해제 정보:', {
            reason: event.reason,
            timestamp: new Date().toISOString(),
          });
          setConnectionStatus('idle');
          onStreamDisconnectedRef.current?.();
        });

        // 예외 발생 이벤트 처리
        session.on('exception', (exception) => {
          console.error('OpenVidu exception:', exception.name, exception.message);
          
          let errorMessage = exception.message || '스트림 연결 중 오류가 발생했습니다.';
          let shouldRetry = false;
          
          // 네트워크 품질과 에러 타입에 따른 개선된 에러 처리
          if (exception.message?.includes('401') || exception.message?.includes('not valid')) {
            errorMessage = '사장님이 아직 방송을 시작하지 않았습니다. 잠시 후 다시 시도해주세요.';
          } else if (exception.name === 'ICE_CONNECTION_DISCONNECTED') {
            // ICE 연결 끊김 - 브라우저 WebRTC의 정상적인 재연결 과정
            console.info('🔌 [useStreamViewer] 브라우저 ICE 연결 재설정 중... (정상 과정)');
            
            // 에러가 아닌 정상적인 WebRTC 재연결 과정으로 처리
            shouldRetry = false; 
            return; // 에러 처리 건너뜀
          } else if (exception.name === 'NO_STREAM_PLAYING_EVENT') {
            // NO_STREAM_PLAYING_EVENT는 브라우저 WebRTC 연결의 정상적인 과정
            console.info('⏳ [useStreamViewer] 브라우저에서 스트림 재생 준비 중... (정상 과정)');
            
            // 에러가 아닌 정상적인 WebRTC 스트림 로딩 과정
            shouldRetry = false;
            return; // 에러 처리 건너뜀
          } else if (exception.message?.includes('timeout') || exception.message?.includes('Request has timed out')) {
            // 타임아웃 에러 - 네트워크 품질 기반 재시도 제안
            const retryDelay = networkQuality === 'good' ? 5 : networkQuality === 'medium' ? 10 : 15;
            errorMessage = `연결 시간이 초과되었습니다. ${retryDelay}초 후 다시 시도해주세요.`;
          }
          
          const errorData: StreamPlayerError = {
            code: exception.name,
            message: errorMessage,
          };
          
          // 일부 에러는 자동 복구 가능하므로 에러 상태로 바로 변경하지 않음
          if (!shouldRetry || exception.name === 'NO_STREAM_PLAYING_EVENT') {
            setError(errorData);
            setConnectionStatus('error');
            onErrorRef.current?.(errorData);
          }
        });
        }; // setupSessionEventListeners 함수 종료

        // 초기 세션에 이벤트 리스너 설정 (네트워크 품질 정보 전달)
        setupSessionEventListeners(session, videoElementId, networkQuality);

        // 토큰으로 세션 연결
        console.log('🟡 [useStreamViewer] OpenVidu 서버 연결 시작');
        console.log('📤 [useStreamViewer] OpenVidu 연결 요청 정보:', {
          token: token.substring(0, 100) + '...',
          tokenLength: token.length,
          tokenType: token.startsWith('wss://') ? 'WebSocket URL' : 'JWT Token',
          networkQuality,
          timestamp: new Date().toISOString(),
        });

        let connectionAttempts = 0;
        const maxRetries = networkQuality === 'poor' ? 1 : 2;
        
        while (connectionAttempts <= maxRetries) {
          try {
            console.log(`🔄 [useStreamViewer] OpenVidu 연결 시도 ${connectionAttempts + 1}/${maxRetries + 1}`);
            
            // 브라우저 WebRTC 연결을 위한 충분한 타임아웃 설정
            const connectWithTimeout = new Promise<void>((resolve, reject) => {
              const timeoutDuration = 60000; // 60초 통일
              
              const connectTimeout = setTimeout(() => {
                reject(new Error(`세션 연결 시간 초과 (${timeoutDuration}ms, 네트워크: ${networkQuality})`));
              }, timeoutDuration);
              
              // 서버에서 받은 원본 토큰 그대로 사용 (WebSocket URL 전체)
              const connectStartTime = performance.now();
              session.connect(token)
                .then(() => {
                  clearTimeout(connectTimeout);
                  const connectEndTime = performance.now();
                  console.log('✅ [useStreamViewer] OpenVidu 서버 연결 성공');
                  console.log('📥 [useStreamViewer] OpenVidu 연결 응답:', {
                    connectionTime: `${(connectEndTime - connectStartTime).toFixed(2)}ms`,
                    sessionId: session.sessionId,
                    connection: session.connection,
                    localParticipant: session.connection?.connectionId,
                    timestamp: new Date().toISOString(),
                  });
                  resolve();
                })
                .catch((error) => {
                  clearTimeout(connectTimeout);
                  const connectEndTime = performance.now();
                  console.error('❌ [useStreamViewer] OpenVidu 서버 연결 실패');
                  console.error('📥 [useStreamViewer] OpenVidu 연결 에러:', {
                    connectionTime: `${(connectEndTime - connectStartTime).toFixed(2)}ms`,
                    error: error.message || error,
                    errorCode: error.code,
                    timestamp: new Date().toISOString(),
                  });
                  reject(error);
                });
            });
            
            await connectWithTimeout;
            break; // 연결 성공 시 루프 탈출
            
          } catch (connectionError) {
            connectionAttempts++;
            
            if (connectionAttempts > maxRetries) {
              // 모든 재시도 실패
              const errorMessage = connectionError instanceof Error ? connectionError.message : '알 수 없는 오류';
              throw new Error(`세션 연결 실패 (${connectionAttempts}/${maxRetries + 1} 시도): ${errorMessage}`);
            } else {
              // 재시도 대기 (더 긴 시간으로 증가)
              const retryDelay = networkQuality === 'good' ? 3000 : networkQuality === 'medium' ? 5000 : 8000;
              console.log(`⏳ [useStreamViewer] ${retryDelay}ms 후 재시도...`);
              await new Promise(resolve => setTimeout(resolve, retryDelay));
            }
          }
        }
      } catch (err) {
        console.error('스트림 연결 실패:', err);
        
        // 연결 실패 시 플래그 해제
        isConnectingRef.current = false;
        connectingTokenRef.current = null;
        
        // 재시도 없이 바로 에러 처리
        const errorData: StreamPlayerError = {
          code: 'CONNECTION_ERROR',
          message: err instanceof Error ? err.message : '스트림 연결에 실패했습니다.',
        };
        
        await cleanup();
        setError(errorData);
        setConnectionStatus('error');
        onErrorRef.current?.(errorData);
        toast.error(errorData.message);
      } finally {
        isConnectingRef.current = false;
      }
    },
    [
      connectionStatus,
      validateToken,
      measureNetworkQuality,
      waitForWebSocketReady,
      initializeOpenVidu,
      cleanup,
      // 콜백 함수들을 의존성에서 제거 (ref로 관리)
    ],
  );

  /**
   * 스트림 연결 해제
   */
  const disconnectFromStream = useCallback(async (showToast = true) => {
    try {
      await cleanup();
      if (showToast) {
        toast.info('스트림 연결이 해제되었습니다.');
      }
      onStreamDisconnectedRef.current?.();
    } catch (err) {
      console.error('스트림 연결 해제 실패:', err);
      if (showToast) {
        toast.error('연결 해제 중 오류가 발생했습니다.');
      }
    }
  }, [cleanup]);

  // 컴포넌트 언마운트 시 정리 (토스트 없이)
  useEffect(() => {
    return () => {
      cleanup();
    };
  }, [cleanup]);

  const isConnected = connectionStatus === 'connected';
  const isConnecting = connectionStatus === 'connecting';
  const isError = connectionStatus === 'error';
  const isEnded = connectionStatus === 'ended';

  return {
    connectionStatus,
    error,
    isConnected,
    isConnecting,
    isError,
    isEnded,
    connectToStream,
    disconnectFromStream,
    session: sessionRef.current,
    subscriber: subscriberRef.current,
  };
};