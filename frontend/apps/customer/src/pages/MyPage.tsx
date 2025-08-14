import MenuSection from '@/components/pages/my/MenuSection/MenuSection';
import ProfileSection from '@/components/pages/my/ProfileSection/ProfileSection';
import QuickActions from '@/components/pages/my/QuickAction/QuickAction';
import { MENU_SECTIONS } from '@/constants/myPageMenus';
import { useProfile } from '@/hooks/useProfile';
import { Loader2 } from 'lucide-react';

const MyPage = () => {
  const { data: profile, isLoading } = useProfile();

  if (isLoading || !profile) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">프로필을 불러오고 있어요...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-between">
      <div className="w-full max-w-md space-y-1">
        <ProfileSection profile={profile} />
        <QuickActions />
        {MENU_SECTIONS.map(({ title, items }) => (
          <MenuSection key={title} title={title} items={items} />
        ))}
      </div>
    </div>
  );
};

export default MyPage;
