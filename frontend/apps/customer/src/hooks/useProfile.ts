import { useQuery } from '@tanstack/react-query';
import { myService } from '@/services/myService';
import { type ProfileData } from '@/components/pages/my/ProfileSection/ProfileSection';

// 프로필과 통계를 합쳐서 가져오는 훅
export const useProfile = () => {
  const profileQuery = useQuery({
    queryKey: ['profile'],
    queryFn: myService.getProfile,
  });

  const statsQuery = useQuery({
    queryKey: ['stats'],
    queryFn: myService.getStats,
  });

  return useQuery({
    queryKey: ['combinedProfile', profileQuery.data, statsQuery.data],
    queryFn: (): ProfileData => {
      if (!profileQuery.data || !statsQuery.data) {
        throw new Error('프로필 또는 통계 데이터가 없습니다');
      }

      const profile = profileQuery.data;
      const stats = statsQuery.data;

      return {
        name: profile.name,
        level: profile.level,
        orderCount: stats.totalOrder,
        points: stats.totalMoneySaved,
        co2: stats.totalCo2Saved,
        couponCount: 0,
        profileImageUrl: profile.profileImageUrl,
      };
    },
    enabled: Boolean(profileQuery.data) && Boolean(statsQuery.data),
  });
};

// 개별 훅들
export const useCustomerProfile = () => {
  return useQuery({
    queryKey: ['profile'],
    queryFn: myService.getProfile,
  });
};

export const useCustomerStats = () => {
  return useQuery({
    queryKey: ['stats'],
    queryFn: myService.getStats,
  });
};
