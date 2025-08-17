import { useState, useEffect } from 'react';

export interface LocationType {
  isLoaded: boolean;
  coordinate?: {
    latitude: number;
    longitude: number;
  };
  error?: { code: number; message: string };
}

const useGeolocation = () => {
  const [location, setLocation] = useState<LocationType>({
    isLoaded: false,
  });

  const onSuccess = (location: {
    coords: { latitude: number; longitude: number };
  }) => {
    setLocation({
      isLoaded: true,
      coordinate: {
        latitude: location.coords.latitude,
        longitude: location.coords.longitude,
      },
    });
  };

  const onError = (error: { code: number; message: string }) => {
    setLocation({
      isLoaded: true,
      error,
    });
  };

  useEffect(() => {
    if (!('geolocation' in navigator)) {
      onError({
        code: 0,
        message: '위치기능이 지원되지 않아요',
      });
    }
    navigator.geolocation.getCurrentPosition(onSuccess, onError);
  }, []);

  return location;
};

export default useGeolocation;
