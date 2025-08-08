import {
  NavLink,
  useLocation,
  useParams,
  generatePath,
} from 'react-router-dom';
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
} from '@/components/ui/sidebar';
import { sidebarItems } from '@/constants/sidebarItems';
import StoreSwitcher from '@/components/StoreSelector';
import UserSwitcher from '@/components/UserSwitcher';

const AppSidebar = () => {
  const location = useLocation();
  const { storeId } = useParams();

  return (
    <Sidebar variant="sidebar" collapsible="icon">
      <SidebarHeader>
        <StoreSwitcher />
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>메인 메뉴</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {sidebarItems.map(item => {
                const targetPath = generatePath(item.path, { storeId });
                const isActive = location.pathname === targetPath;

                return (
                  <SidebarMenuItem key={item.key}>
                    <SidebarMenuButton
                      asChild
                      tooltip={item.tooltip}
                      isActive={isActive}
                    >
                      <NavLink to={targetPath}>
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
