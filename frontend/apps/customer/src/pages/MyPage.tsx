import MenuSection from '@/components/pages/my/MenuSection/MenuSection';
import ProfileSection from '@/components/pages/my/ProfileSection/ProfileSection';
import QuickActions from '@/components/pages/my/QuickAction/QuickAction';
import { MENU_SECTIONS } from '@/constants/myPageMenus';
import { useProfile } from '@/hooks/useProfile';
import { Loader2 } from 'lucide-react';

const MyPage = () => {
  const { data: profile, isLoading, error } = useProfile();

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">프로필을 불러오고 있어요...</div>
        </div>
      </div>
    );
  }

  if (error || !profile) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center space-y-4 px-4">
        <div className="text-4xl">😵</div>
        <div className="text-center">
          <h3 className="mb-2 text-lg font-semibold text-gray-800">
            프로필을 불러올 수 없어요
          </h3>
          <p className="text-sm text-gray-500">잠시 후 다시 시도해주세요</p>
        </div>
      </div>
    );
  }

  return (
    <div className="pb-16 pt-12">
      <div className="flex flex-col items-center justify-between">
        <div className="w-full max-w-md space-y-1">
          <ProfileSection profile={profile} />
          <QuickActions />
          {MENU_SECTIONS.map(({ title, items }) => (
            <MenuSection key={title} title={title} items={items} />
          ))}
        </div>
      </div>
    </div>
  );
};

export default MyPage;
