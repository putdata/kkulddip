import { useState } from 'react';
import { User, LogOut, ChevronsUpDown } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar';
import { useUserStore } from 'common';
import { useUserSelection } from '@/hooks/useUserSelection';
import SettingsModal from '@/components/settingModal/SettingsModal';

/**
 * 사용자 메뉴 드롭다운 컴포넌트
 * 설정, 정산, 알림, 로그아웃 메뉴를 제공합니다
 */
const UserSwitcher = () => {
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const user = useUserStore(state => state.user);
  const { menuItems, handleLogout } = useUserSelection({
    onSettings: () => setIsSettingsOpen(true),
  });

  /**
   * 사용자 프로필 이미지 또는 기본 아바타 렌더링
   */
  const renderUserAvatar = () => {
    if (user?.profileImageUrl) {
      return (
        <img
          src={user.profileImageUrl}
          alt="Profile"
          className="h-8 w-8 rounded-full object-cover"
        />
      );
    }

    return (
      <div className="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
        <User className="size-4" />
      </div>
    );
  };

  return (
    <>
      <SidebarMenu>
        <SidebarMenuItem>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <SidebarMenuButton
                size="lg"
                className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
              >
                {renderUserAvatar()}
                <div className="grid flex-1 text-left text-sm leading-tight">
                  <span className="truncate font-semibold">
                    {user?.name || '사용자'}
                  </span>
                  <span className="truncate text-xs">
                    {user?.email || '로그인이 필요합니다'}
                  </span>
                </div>
                <ChevronsUpDown className="ml-auto size-4" />
              </SidebarMenuButton>
            </DropdownMenuTrigger>
            <DropdownMenuContent
              className="w-[--radix-dropdown-menu-trigger-width] min-w-56 rounded-lg"
              side="bottom"
              align="end"
              sideOffset={4}
            >
              {menuItems.map(item => (
                <DropdownMenuItem
                  key={item.label}
                  className="gap-2"
                  onClick={item.onClick}
                >
                  <item.icon className="size-4" />
                  <span>{item.label}</span>
                </DropdownMenuItem>
              ))}
              <DropdownMenuSeparator />
              <DropdownMenuItem className="gap-2" onClick={handleLogout}>
                <LogOut className="size-4" />
                <span>로그아웃</span>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </SidebarMenuItem>
      </SidebarMenu>

      <SettingsModal open={isSettingsOpen} onOpenChange={setIsSettingsOpen} />
    </>
  );
};

export default UserSwitcher;
