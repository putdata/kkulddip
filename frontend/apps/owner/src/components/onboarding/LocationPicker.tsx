import { useCallback, useEffect, useRef, useState } from 'react';
import { MapPin, Search } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent } from '@/components/ui/card';
import { toast } from 'sonner';

interface LocationPickerProps {
  initialAddress?: string;
  initialLatitude?: number;
  initialLongitude?: number;
  onLocationSelect: (data: {
    address: string;
    latitude: number;
    longitude: number;
  }) => void;
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
  setPosition: (position: KakaoLatLng) => void;
  getPosition: () => KakaoLatLng;
}

interface KakaoAddressResult {
  y: string;
  x: string;
  address_name?: string;
}

interface KakaoCoordResult {
  address?: { address_name: string };
  road_address?: { address_name: string };
}

interface KakaoGeocoder {
  addressSearch: (
    address: string,
    callback: (result: KakaoAddressResult[], status: string) => void,
  ) => void;
  coord2Address: (
    longitude: number,
    latitude: number,
    callback: (result: KakaoCoordResult[], status: string) => void,
  ) => void;
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
  event: {
    addListener: (
      target: KakaoMap,
      type: string,
      handler: (mouseEvent: { latLng: KakaoLatLng }) => void,
    ) => void;
  };
  load: (callback: () => void) => void;
  services: {
    Geocoder: new () => KakaoGeocoder;
    Status: {
      OK: string;
    };
  };
}

declare global {
  interface Window {
    kakao: {
      maps: KakaoMaps;
      isMapLoaded?: boolean;
    };
  }
}

const LocationPicker = ({
  initialAddress = '',
  initialLatitude = 37.5665,
  initialLongitude = 126.978,
  onLocationSelect,
}: LocationPickerProps) => {
  const mapContainer = useRef<HTMLDivElement>(null);
  const mapInstance = useRef<KakaoMap | null>(null);
  const markerInstance = useRef<KakaoMarker | null>(null);
  const geocoderInstance = useRef<KakaoGeocoder | null>(null);

  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchAddress, setSearchAddress] = useState(initialAddress);
  const [currentAddress, setCurrentAddress] = useState(initialAddress);
  const [isSearching, setIsSearching] = useState(false);

  // 카카오맵 스크립트 로드
  const loadKakaoMapScript = () => {
    return new Promise<void>((resolve, reject) => {
      if (window.kakao && window.kakao.maps) {
        resolve();
        return;
      }

      const existingScript = document.querySelector(
        'script[src*="dapi.kakao.com"]',
      );
      if (existingScript) {
        existingScript.addEventListener('load', () => resolve());
        existingScript.addEventListener('error', () =>
          reject(new Error('Kakao Map 스크립트 로드 실패')),
        );
        return;
      }

      const apiKey = import.meta.env.VITE_KAKAO_MAP_API_KEY;
      if (!apiKey) {
        reject(new Error('Kakao Map API 키가 설정되지 않았습니다.'));
        return;
      }

      const script = document.createElement('script');
      script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${apiKey}&autoload=false&libraries=services`;

      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Kakao Map 스크립트 로드 실패'));
      document.head.appendChild(script);
    });
  };

  // 주소를 좌표로 변환
  const addressToCoordinates = (
    address: string,
  ): Promise<{ lat: number; lng: number }> => {
    return new Promise((resolve, reject) => {
      if (!geocoderInstance.current) {
        reject(new Error('Geocoder가 초기화되지 않았습니다.'));
        return;
      }

      geocoderInstance.current.addressSearch(address, (result, status) => {
        if (
          status === window.kakao.maps.services.Status.OK &&
          result.length > 0
        ) {
          const firstResult = result[0];
          if (firstResult) {
            resolve({
              lat: parseFloat(firstResult.y),
              lng: parseFloat(firstResult.x),
            });
          } else {
            reject(new Error('주소를 찾을 수 없습니다.'));
          }
        } else {
          reject(new Error('주소를 찾을 수 없습니다.'));
        }
      });
    });
  };

  // 좌표를 주소로 변환
  const coordinatesToAddress = (lat: number, lng: number): Promise<string> => {
    return new Promise((resolve, reject) => {
      if (!geocoderInstance.current) {
        reject(new Error('Geocoder가 초기화되지 않았습니다.'));
        return;
      }

      geocoderInstance.current.coord2Address(lng, lat, (result, status) => {
        if (
          status === window.kakao.maps.services.Status.OK &&
          result.length > 0
        ) {
          const firstResult = result[0];
          const address =
            firstResult?.address?.address_name ||
            firstResult?.road_address?.address_name;
          resolve(address || '주소를 가져올 수 없습니다.');
        } else {
          reject(new Error('주소를 가져올 수 없습니다.'));
        }
      });
    });
  };

  // 마커 위치 업데이트
  const updateMarkerPosition = useCallback(
    async (lat: number, lng: number) => {
      if (!mapInstance.current || !markerInstance.current) {
        return;
      }

      const position = new window.kakao.maps.LatLng(lat, lng);
      markerInstance.current.setPosition(position);
      mapInstance.current.setCenter(position);

      try {
        const address = await coordinatesToAddress(lat, lng);
        setCurrentAddress(address);
        onLocationSelect({ address, latitude: lat, longitude: lng });
      } catch (error) {
        console.error('주소 변환 실패:', error);
      }
    },
    [onLocationSelect],
  );

  // 지도 초기화
  const initializeMap = useCallback(() => {
    if (!mapContainer.current) {
      return;
    }

    try {
      const options = {
        center: new window.kakao.maps.LatLng(initialLatitude, initialLongitude),
        level: 3,
      };

      const map = new window.kakao.maps.Map(mapContainer.current, options);
      mapInstance.current = map;

      const marker = new window.kakao.maps.Marker({
        position: options.center,
        map: map,
      });
      markerInstance.current = marker;

      // Geocoder 초기화
      geocoderInstance.current = new window.kakao.maps.services.Geocoder();

      // 지도 클릭 이벤트
      window.kakao.maps.event.addListener(
        map,
        'click',
        (mouseEvent: { latLng: KakaoLatLng }) => {
          const latlng = mouseEvent.latLng;
          const lat = latlng.getLat();
          const lng = latlng.getLng();
          updateMarkerPosition(lat, lng);
        },
      );

      // 지도 크기 재조정
      setTimeout(() => {
        map.relayout();
      }, 100);

      setIsLoading(false);
    } catch (err) {
      console.error('지도 초기화 실패:', err);
      setError('지도를 불러올 수 없습니다.');
      setIsLoading(false);
    }
  }, [initialLatitude, initialLongitude, updateMarkerPosition]);

  // 주소 검색
  const handleAddressSearch = async () => {
    if (!searchAddress.trim()) {
      return;
    }

    setIsSearching(true);
    try {
      const coordinates = await addressToCoordinates(searchAddress);
      await updateMarkerPosition(coordinates.lat, coordinates.lng);
    } catch (error) {
      console.error('주소 검색 실패:', error);
      toast.error('주소를 찾을 수 없습니다. 다시 시도해주세요.');
    } finally {
      setIsSearching(false);
    }
  };

  // 초기화
  useEffect(() => {
    loadKakaoMapScript()
      .then(() => {
        if (window.kakao && window.kakao.maps) {
          window.kakao.maps.load(initializeMap);
        } else {
          setError('Kakao Maps API를 사용할 수 없습니다.');
          setIsLoading(false);
        }
      })
      .catch(err => {
        console.error('스크립트 로드 실패:', err);
        setError(err.message);
        setIsLoading(false);
      });
  }, [initializeMap]);

  if (error) {
    return (
      <Card className="border-red-200 bg-red-50">
        <CardContent className="p-4 text-center">
          <MapPin className="mx-auto mb-2 h-8 w-8 text-red-500" />
          <p className="text-red-700">{error}</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      {/* 주소 검색 */}
      <div className="flex gap-2">
        <Input
          placeholder="주소를 입력하세요 (예: 서울특별시 마포구 홍익로 15)"
          value={searchAddress}
          onChange={e => setSearchAddress(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleAddressSearch()}
          className="flex-1"
        />
        <Button
          onClick={handleAddressSearch}
          disabled={isSearching || !searchAddress.trim()}
          className="px-4"
        >
          <Search className="h-4 w-4" />
        </Button>
      </div>

      {/* 현재 선택된 주소 */}
      {currentAddress && (
        <div className="rounded-lg bg-green-50 p-3">
          <div className="flex items-center gap-2 text-sm text-green-700">
            <MapPin className="h-4 w-4" />
            <span className="font-medium">선택된 위치:</span>
          </div>
          <p className="mt-1 text-sm text-green-600">{currentAddress}</p>
        </div>
      )}

      {/* 지도 */}
      <Card className="overflow-hidden">
        <div className="relative">
          {isLoading && (
            <div className="absolute inset-0 z-10 flex items-center justify-center bg-gray-100">
              <div className="text-gray-500">지도 로딩 중...</div>
            </div>
          )}
          <div
            ref={mapContainer}
            className="h-64 w-full"
            style={{ minHeight: '256px' }}
          />
        </div>
      </Card>

      {/* 안내 메시지 */}
      <div className="rounded-lg bg-blue-50 p-3">
        <p className="text-xs text-blue-600">
          💡 지도를 클릭하거나 주소를 검색하여 정확한 위치를 선택하세요
        </p>
      </div>
    </div>
  );
};

export default LocationPicker;
