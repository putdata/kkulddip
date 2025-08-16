import { useState } from 'react';
import { useMediaQuery } from '@/hooks/use-media-query';
import SimpleBar from 'simplebar-react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Drawer,
  DrawerContent,
  DrawerHeader,
  DrawerTitle,
} from '@/components/ui/drawer';
import SettingsSidebar from './SettingsSidebar';
import StoreSettings from './StoreSettings';
import GeneralSettings from './GeneralSettings';
import { SETTING_IDS, type SettingItemId } from '@/constants/settingItems';

interface SettingsModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const SettingsModal = ({ open, onOpenChange }: SettingsModalProps) => {
  const [selectedTab, setSelectedTab] = useState<SettingItemId>(
    SETTING_IDS.STORE,
  );
  const isDesktop = useMediaQuery('(min-width: 768px)');

  const renderContent = () => {
    switch (selectedTab) {
      case SETTING_IDS.STORE:
        return (
          <div className="space-y-6">
            <div>
              <h2 className="mb-1 text-lg font-semibold">가게 설정</h2>
              <p className="mb-4 text-sm text-gray-600">
                등록된 가게를 관리합니다.
              </p>
            </div>
            <StoreSettings />
          </div>
        );
      case SETTING_IDS.GENERAL:
        return (
          <div className="space-y-6">
            <div>
              <h2 className="mb-1 text-lg font-semibold">일반 설정</h2>
              <p className="mb-4 text-sm text-gray-600">
                일반적인 앱 설정을 관리합니다.
              </p>
            </div>
            <GeneralSettings />
          </div>
        );
      default:
        return (
          <div className="space-y-6">
            <div>
              <h2 className="mb-1 text-lg font-semibold">가게 설정</h2>
              <p className="mb-4 text-sm text-gray-600">
                등록된 가게를 관리합니다.
              </p>
            </div>
            <StoreSettings />
          </div>
        );
    }
  };

  if (isDesktop) {
    return (
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent
          className="max-w-10/12 sm:max-w-10/12 h-[40rem] max-h-[85vh] w-[60rem] overflow-hidden p-0"
          data-dialog-content
        >
          <div className="flex h-[40rem] max-h-[85vh] w-full">
            {/* 사이드바 */}
            <div className="flex h-full w-60 min-w-60 max-w-60 flex-col border-r bg-gray-50">
              <div className="flex-shrink-0 border-b p-4">
                <DialogHeader>
                  <DialogTitle className="text-left">설정</DialogTitle>
                </DialogHeader>
              </div>
              <div className="p-4">
                <SettingsSidebar
                  selectedTab={selectedTab}
                  onTabChange={setSelectedTab}
                />
              </div>
            </div>
            {/* 메인 콘텐츠 */}
            <div className="flex h-full min-w-0 flex-1 flex-col">
              <SimpleBar className="min-h-0 flex-1" autoHide={true}>
                <div className="p-6">{renderContent()}</div>
              </SimpleBar>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    );
  }

  return (
    <Drawer open={open} onOpenChange={onOpenChange}>
      <DrawerContent
        className="h-5/6 max-h-full overflow-hidden"
        data-drawer-content
      >
        <div className="flex h-full w-full flex-col">
          <DrawerHeader className="flex-shrink-0 border-b text-left">
            <DrawerTitle>설정</DrawerTitle>
          </DrawerHeader>
          {/* 모바일 탭 네비게이션 */}
          <div className="flex-shrink-0 border-b">
            <SettingsSidebar
              selectedTab={selectedTab}
              onTabChange={setSelectedTab}
              className="flex-row justify-around space-x-1 space-y-0 p-2"
            />
          </div>
          {/* 메인 콘텐츠 */}
          <SimpleBar className="min-h-0 flex-1" autoHide={true}>
            <div className="p-4">{renderContent()}</div>
          </SimpleBar>
        </div>
      </DrawerContent>
    </Drawer>
  );
};

export default SettingsModal;
