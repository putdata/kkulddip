import MenuSection, {
  type MenuItem,
} from '@/components/pages/my/MenuSection/MenuSection';
import ProfileSection, {
  type ProfileData,
} from '@/components/pages/my/ProfileSection/ProfileSection';
import QuickActions from '@/components/pages/my/QuickAction/QuickAction';

const MyPage = () => {
  // 더미 프로필 데이터
  const dummyProfile: ProfileData = {
    name: '홍혜린',
    level: '말벌',
    orderCount: 127,
    points: 2450,
    couponCount: 5,
  };

  // 주문 관리 메뉴
  const orderMenuItems: MenuItem[] = [
    {
      id: 'order-history',
      label: '주문 내역',
      onClick: () => console.log('주문내역 클릭'),
    },
    {
      id: 'reviews',
      label: '리뷰 관리',
      onClick: () => console.log('리뷰관리 클릭'),
    },
    {
      id: 'reorder',
      label: '재주문',
      onClick: () => console.log('재주문 클릭'),
    },
  ];

  // 계정 설정 메뉴
  const accountMenuItems: MenuItem[] = [
    {
      id: 'address',
      label: '주소 관리',
      onClick: () => console.log('주소 관리 클릭'),
    },
    {
      id: 'notifications',
      label: '알림 설정',
      onClick: () => console.log('알림 설정 클릭'),
    },
    {
      id: 'app-settings',
      label: '앱 설정',
      onClick: () => console.log('앱 설정 클릭'),
    },
  ];

  // 고객지원 메뉴
  const supportMenuItems: MenuItem[] = [
    {
      id: 'customer-service',
      label: '고객센터',
      onClick: () => console.log('고객센터 클릭'),
    },
  ];

  return (
    <div className="pb-16 pt-12">
      <div className="flex flex-col items-center justify-between">
        <div className="w-full max-w-md space-y-1">
          <ProfileSection profile={dummyProfile} />
          <QuickActions />
          <MenuSection title="주문 관리" items={orderMenuItems} />
          <MenuSection title="계정 설정" items={accountMenuItems} />
          <MenuSection title="고객지원" items={supportMenuItems} />
        </div>
      </div>
    </div>
  );
};

export default MyPage;
