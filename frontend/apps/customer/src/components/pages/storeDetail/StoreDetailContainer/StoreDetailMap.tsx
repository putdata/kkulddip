import { useEffect, useRef, useState } from 'react';
import type { StoreDetail } from '@/types/store';

interface StoreDetailMapProps {
  store: StoreDetail;
}

interface KakaoMap {
  setCenter: (position: KakaoLatLng) => void;
  getLevel: () => number;
  setLevel: (level: number) => void;
  relayout: () => void;
}

interface KakaoLatLng {
  getLat: () => number;
  getLng: () => number;
}

interface KakaoMarker {
  setMap: (map: KakaoMap | null) => void;
  getPosition: () => KakaoLatLng;
}

interface KakaoInfoWindow {
  open: (map: KakaoMap, marker: KakaoMarker) => void;
  close: () => void;
}

interface KakaoMaps {
  Map: new (
    container: HTMLElement,
    options: { center: KakaoLatLng; level: number },
  ) => KakaoMap;
  LatLng: new (lat: number, lng: number) => KakaoLatLng;
  Marker: new (options: {
    position: KakaoLatLng;
    map?: KakaoMap;
  }) => KakaoMarker;
  InfoWindow: new (options: { content: string }) => KakaoInfoWindow;
  event: {
    addListener: (
      target: KakaoMarker,
      type: string,
      handler: () => void,
    ) => void;
  };
  load: (callback: () => void) => void;
}

declare global {
  interface Window {
    kakao: {
      maps: KakaoMaps;
      isMapLoaded?: boolean;
    };
  }
}

const StoreDetailMap = ({ store }: StoreDetailMapProps) => {
  const mapContainer = useRef<HTMLDivElement>(null);
  const mapInstance = useRef<KakaoMap | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadKakaoMapScript = () => {
      return new Promise<void>((resolve, reject) => {
        console.log('🔍 [KakaoMap] loadKakaoMapScript 시작');

        if (window.kakao && window.kakao.maps) {
          console.log('✅ [KakaoMap] 이미 로드됨');
          resolve();
          return;
        }

        // 이미 스크립트가 로드 중인지 확인
        const existingScript = document.querySelector(
          'script[src*="dapi.kakao.com"]',
        );
        if (existingScript) {
          console.log('⏳ [KakaoMap] 스크립트 로드 중...');
          existingScript.addEventListener('load', () => resolve());
          existingScript.addEventListener('error', () =>
            reject(new Error('Kakao Map 스크립트 로드 실패')),
          );
          return;
        }

        const apiKey = import.meta.env.VITE_KAKAO_MAP_API_KEY;
        console.log('🔑 [KakaoMap] API Key:', apiKey ? '설정됨' : '없음');

        if (!apiKey) {
          reject(new Error('Kakao Map API 키가 설정되지 않았습니다.'));
          return;
        }

        const script = document.createElement('script');
        script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${apiKey}&autoload=false`;
        console.log('📜 [KakaoMap] 스크립트 로드 시작:', script.src);

        script.onload = () => {
          console.log('✅ [KakaoMap] 스크립트 로드 완료');
          resolve();
        };
        script.onerror = () => {
          console.error('❌ [KakaoMap] 스크립트 로드 실패');
          reject(new Error('Kakao Map 스크립트 로드 실패'));
        };
        document.head.appendChild(script);
      });
    };

    const initializeMap = () => {
      console.log('🗺️ [KakaoMap] initializeMap 시작');

      if (!mapContainer.current) {
        console.error('❌ [KakaoMap] mapContainer.current가 null');
        return;
      }

      // 이미 지도가 생성되었으면 중복 생성 방지
      if (mapInstance.current) {
        console.log('⚠️ [KakaoMap] 지도가 이미 생성됨, 중복 생성 방지');
        setIsLoading(false);
        return;
      }

      try {
        const { latitude, longitude } = store;
        console.log('📍 [KakaoMap] 좌표:', { latitude, longitude });
        console.log('🏪 [KakaoMap] 전체 store 객체:', store);

        if (!latitude || !longitude || latitude === 0 || longitude === 0) {
          throw new Error(
            `위도나 경도가 없습니다. latitude: ${latitude}, longitude: ${longitude}`,
          );
        }

        const options = {
          center: new window.kakao.maps.LatLng(latitude, longitude),
          level: 3,
        };
        console.log('🔧 [KakaoMap] 지도 옵션:', options);

        const map = new window.kakao.maps.Map(mapContainer.current, options);
        mapInstance.current = map; // 지도 인스턴스 저장
        console.log('✅ [KakaoMap] 지도 생성 완료');

        // 지도 크기 재조정 (중요!)
        setTimeout(() => {
          map.relayout();
          console.log('🔄 [KakaoMap] 지도 크기 재조정 완료');
        }, 100);

        const markerPosition = new window.kakao.maps.LatLng(
          latitude,
          longitude,
        );
        const marker = new window.kakao.maps.Marker({
          position: markerPosition,
        });

        marker.setMap(map);
        console.log('📌 [KakaoMap] 마커 생성 완료');

        const infowindowContent = `
          <div style="padding:12px; min-width:200px; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
            <div style="font-weight:bold; font-size:14px; color:#333; margin-bottom:6px;">
              ${store.storeName}
            </div>
            <div style="font-size:12px; color:#666; margin-bottom:4px;">
              📍 ${store.storeAddress}
            </div>
            <div style="font-size:12px; color:#666; margin-bottom:4px;">
              📞 ${store.phoneNumber || '전화번호 정보 없음'}
            </div>
            <div style="font-size:12px; color:#666; margin-bottom:4px;">
              🕒 ${store.operatingHours}
            </div>
            <div style="font-size:12px; color:#f59e0b; font-weight:bold;">
              ⭐ ${store.ratingAverage.toFixed(1)} (리뷰 ${store.reviewCount}개)
            </div>
          </div>
        `;

        const infowindow = new window.kakao.maps.InfoWindow({
          content: infowindowContent,
        });

        let isInfowindowOpen = false;

        window.kakao.maps.event.addListener(marker, 'click', () => {
          if (isInfowindowOpen) {
            infowindow.close();
            isInfowindowOpen = false;
            console.log('🔄 [KakaoMap] 인포윈도우 닫기');
          } else {
            infowindow.open(map, marker);
            isInfowindowOpen = true;
            console.log('📝 [KakaoMap] 인포윈도우 열기');
          }
        });

        setIsLoading(false);
        console.log('🎉 [KakaoMap] 초기화 완료');
      } catch (err) {
        console.error('❌ [KakaoMap] Map initialization error:', err);
        setError(
          err instanceof Error ? err.message : '지도를 불러올 수 없습니다.',
        );
        setIsLoading(false);
      }
    };

    console.log('🚀 [KakaoMap] useEffect 시작');

    loadKakaoMapScript()
      .then(() => {
        console.log('🎯 [KakaoMap] 스크립트 로드 완료, 지도 초기화 시작');
        if (window.kakao && window.kakao.maps) {
          window.kakao.maps.load(initializeMap);
        } else {
          console.error('❌ [KakaoMap] window.kakao.maps가 없음');
          setError('Kakao Maps API를 사용할 수 없습니다.');
          setIsLoading(false);
        }
      })
      .catch(err => {
        console.error('❌ [KakaoMap] script loading error:', err);
        setError(
          err instanceof Error
            ? err.message
            : '스크립트 로드 중 오류가 발생했습니다.',
        );
        setIsLoading(false);
      });
  }, [store]);

  if (error) {
    return (
      <div className="flex flex-col items-start gap-2 overflow-hidden p-3">
        <div className="flex h-60 w-full items-center justify-center rounded-lg border bg-gray-100">
          <p className="text-gray-500">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="w-full overflow-hidden p-3">
      <div className="relative h-60 w-full rounded-lg border">
        {isLoading && (
          <div className="absolute inset-0 flex items-center justify-center bg-gray-100">
            <div className="text-gray-500">지도 로딩 중...</div>
          </div>
        )}
        <div
          ref={mapContainer}
          className="h-full w-full rounded-lg"
          style={{
            height: '240px',
            width: '100%',
            minWidth: '300px',
            display: 'block',
          }}
        />
      </div>
    </div>
  );
};

export default StoreDetailMap;
