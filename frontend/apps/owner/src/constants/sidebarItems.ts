import {
  BarChart3,
  Bell,
  ChefHat,
  CreditCard,
  Home,
  Settings,
  ShoppingBag,
  Video,
} from 'lucide-react';
import { ROUTE_PATH } from '@/router/route-path';

export interface SidebarItem {
  key: string;
  path: string;
  label: string;
  icon: typeof Home;
  tooltip: string;
}

export const sidebarItems: SidebarItem[] = [
  {
    key: 'dashboard',
    path: ROUTE_PATH.STORE.DASHBOARD,
    label: '대시보드',
    icon: Home,
    tooltip: '대시보드',
  },
  {
    key: 'menu',
    path: ROUTE_PATH.STORE.MENU,
    label: '메뉴 관리',
    icon: ChefHat,
    tooltip: '메뉴 관리',
  },
  {
    key: 'orders',
    path: ROUTE_PATH.STORE.ORDERS,
    label: '주문 관리',
    icon: ShoppingBag,
    tooltip: '주문 관리',
  },
  {
    key: 'streaming',
    path: ROUTE_PATH.STORE.STREAMING,
    label: '라이브 방송',
    icon: Video,
    tooltip: '라이브 방송',
  },
  {
    key: 'analytics',
    path: ROUTE_PATH.STORE.ANALYTICS,
    label: '분석',
    icon: BarChart3,
    tooltip: '분석',
  },
  {
    key: 'settlement',
    path: ROUTE_PATH.STORE.SETTLEMENT,
    label: '정산',
    icon: CreditCard,
    tooltip: '정산',
  },
  {
    key: 'notifications',
    path: ROUTE_PATH.STORE.NOTIFICATIONS,
    label: '알림',
    icon: Bell,
    tooltip: '알림',
  },
  {
    key: 'settings',
    path: ROUTE_PATH.STORE.SETTINGS,
    label: '설정',
    icon: Settings,
    tooltip: '설정',
  },
];
