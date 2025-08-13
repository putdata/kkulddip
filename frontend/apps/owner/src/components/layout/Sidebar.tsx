import { NavLink, useLocation, generatePath } from 'react-router-dom';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarTrigger,
  useSidebar,
} from '@/components/ui/sidebar';
import { sidebarItems } from '@/constants/sidebarItems';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import StoreSwitcher from '@/components/layout/StoreSwitcher';
import UserSwitcher from '@/components/layout/UserSwitcher';

const AppSidebar = () => {
  const location = useLocation();
  const { storeId } = useStoreSelection();
  const { state, isMobile, setOpenMobile } = useSidebar();

  return (
    <Sidebar variant="sidebar" collapsible="icon">
      <SidebarHeader>
        <div className="flex items-center gap-2">
          {state === 'collapsed' ? (
            <SidebarTrigger className="h-8 w-8" />
          ) : (
            <>
              <StoreSwitcher />
              <SidebarTrigger className="ml-auto hidden h-8 w-8 md:flex" />
            </>
          )}
        </div>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>메인 메뉴</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {sidebarItems.map(item => {
                const targetPath = generatePath(item.path, { storeId });
                const isActive = location.pathname === targetPath;

                const handleItemClick = () => {
                  if (isMobile) {
                    setOpenMobile(false);
                  }
                };

                return (
                  <SidebarMenuItem key={item.key}>
                    <SidebarMenuButton
                      asChild
                      tooltip={item.tooltip}
                      isActive={isActive}
                    >
                      <NavLink to={targetPath} onClick={handleItemClick}>
                        <item.icon />
                        <span>{item.label}</span>
                      </NavLink>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                );
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter>
        <UserSwitcher />
      </SidebarFooter>
    </Sidebar>
  );
};

export default AppSidebar;
