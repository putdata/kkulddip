import { SettingsCard, StoreManagement } from '@/components/pages/settings';
import SettingsPageSection from '@/components/SettingsPageSection';
import { Store as StoreIcon, Bell, Shield } from 'lucide-react';
import { useState } from 'react';

const Settings = () => {
  const [activeSection, setActiveSection] = useState<
    'store' | 'notification' | 'permission'
  >('store');

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between px-4">
        <div>
          <h1 className="text-2xl font-bold">설정</h1>
          <p className="text-muted-foreground">
            가게 운영과 관련된 설정을 관리하세요
          </p>
        </div>
      </div>

      {/* 설정 카테고리 그리드 */}
      <div className="grid gap-6 md:grid-cols-3">
        <SettingsCard
          icon={StoreIcon}
          title="가게 관리"
          description="가게 정보 및 운영 설정"
          isActive={activeSection === 'store'}
          onClick={() => setActiveSection('store')}
        />
        <SettingsCard
          icon={Bell}
          title="알림 설정"
          description="주문 및 시스템 알림 관리"
          isActive={activeSection === 'notification'}
          onClick={() => setActiveSection('notification')}
        />
        <SettingsCard
          icon={Shield}
          title="권한 설정"
          description="사용자 권한 및 접근 관리"
          isActive={activeSection === 'permission'}
          onClick={() => setActiveSection('permission')}
        />
      </div>

      {/* 가게 관리 섹션 */}
      {activeSection === 'store' && (
        <SettingsPageSection
          icon={StoreIcon}
          title="가게 관리"
          description="현재 선택된 가게의 정보를 관리할 수 있습니다"
        >
          <StoreManagement />
        </SettingsPageSection>
      )}

      {/* 알림 설정 섹션 */}
      {activeSection === 'notification' && (
        <SettingsPageSection
          icon={Bell}
          title="알림 설정"
          description="푸시 알림 및 알림 수신 설정을 관리할 수 있습니다"
        >
          <div className="text-muted-foreground py-8 text-center">
            알림 설정 기능은 준비 중입니다.
          </div>
        </SettingsPageSection>
      )}

      {/* 권한 설정 섹션 */}
      {activeSection === 'permission' && (
        <SettingsPageSection
          icon={Shield}
          title="권한 설정"
          description="사용자 권한 및 접근 관리 기능입니다"
        >
          <div className="text-muted-foreground py-8 text-center">
            권한 설정 기능은 준비 중입니다.
          </div>
        </SettingsPageSection>
      )}
    </div>
  );
};

export default Settings;
