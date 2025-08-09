import { useState, useEffect } from 'react';
import { likeService } from '@/services/likeService';
import useGeolocation from './useGeolocation';
import type { Store } from '@/types/likedStore';

export const useLikes = (customerId: number) => {
  const [stores, setStores] = useState<Store[]>([]);
  const location = useGeolocation();

  useEffect(() => {
    if (location.isLoaded && location.coordinate && !location.error) {
      const loadStores = async () => {
        const data = await likeService.getStores({
          customerId,
          userLatitude: location.coordinate?.latitude,
          userLongitude: location.coordinate?.longitude,
        });
        setStores(data.content);
      };
      loadStores();
    }
  }, [customerId, location]);

  return {
    stores,
  };
};
