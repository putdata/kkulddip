import { useQuery } from '@tanstack/react-query';
import { getNotifications } from '@/services/notificationService';
import { useUserStore } from 'common';

export const useNotifications = () => {
  const { user } = useUserStore();

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['notifications', user?.userid],
    queryFn: () => getNotifications(user!.userid),
    enabled: Boolean(user?.userid),
  });

  const notifications = data || [];

  return {
    notifications,
    isLoading,
    error,
    refetch,
  };
};
