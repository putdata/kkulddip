import StoreManagement from '@/components/StoreManagement';
import SettingsCard from '@/components/settings/SettingsCard';
import SettingsPageSection from '@/components/settings/SettingsPageSection';
import { Store as StoreIcon, Bell, Shield } from 'lucide-react';

const Settings = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="mb-2 text-2xl font-bold">설정</h1>
        <p className="text-gray-600">가게 운영과 관련된 설정을 관리하세요.</p>
      </div>

      {/* 설정 카테고리 그리드 */}
      <div className="grid gap-6 md:grid-cols-3">
        <SettingsCard
          icon={StoreIcon}
          title="가게 관리"
          description="가게 정보 및 운영 설정"
          isActive={true}
        />
        <SettingsCard
          icon={Bell}
          title="알림 설정"
          description="주문 및 시스템 알림 관리"
          isActive={false}
        />
        <SettingsCard
          icon={Shield}
          title="권한 설정"
          description="사용자 권한 및 접근 관리"
          isActive={false}
        />
      </div>

      {/* 가게 관리 상세 섹션 */}
      <SettingsPageSection
        icon={StoreIcon}
        title="가게 관리"
        description="현재 선택된 가게의 정보를 관리할 수 있습니다"
      >
        <StoreManagement />
      </SettingsPageSection>
    </div>
  );
};

export default Settings;
