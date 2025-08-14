import { Outlet } from 'react-router-dom';
import { useState } from 'react';
import SimpleBar from 'simplebar-react';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';
import AppSidebar from './Sidebar';
import MobileHeader from './MobileHeader';
import SettingsModal from '@/components/settingModal/SettingsModal';

const OwnerLayout = () => {
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <MobileHeader onSettingsClick={() => setIsSettingsOpen(true)} />
        <SimpleBar className="h-dvh" autoHide={true}>
          <main className="flex flex-1 flex-col gap-4 p-4 pt-16 md:pt-4">
            <Outlet />
          </main>
        </SimpleBar>
      </SidebarInset>

      <SettingsModal open={isSettingsOpen} onOpenChange={setIsSettingsOpen} />
    </SidebarProvider>
  );
};

export default OwnerLayout;
