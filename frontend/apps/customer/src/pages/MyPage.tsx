import MenuSection from '@/components/pages/my/MenuSection/MenuSection';
import ProfileSection, {
  type ProfileData,
} from '@/components/pages/my/ProfileSection/ProfileSection';
import QuickActions from '@/components/pages/my/QuickAction/QuickAction';
import { MENU_SECTIONS } from '@/constants/myPageMenus';

const MyPage = () => {
  // 더미 프로필 데이터
  const dummyProfile: ProfileData = {
    name: '홍혜린',
    level: '말벌',
    orderCount: 127,
    points: 2450,
    couponCount: 5,
  };

  return (
    <div className="pb-16 pt-12">
      <div className="flex flex-col items-center justify-between">
        <div className="w-full max-w-md space-y-1">
          <ProfileSection profile={dummyProfile} />
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
