import {
  ShoppingBag,
  Star,
  Clock,
  MapPin,
  Bell,
  Settings,
  HelpCircle,
} from 'lucide-react';

/**
 * 마이페이지 메뉴 섹션 상수
 * TODO: ROUTE_PATH 수정 후 연결
 */
export const MENU_SECTIONS = [
  {
    title: '주문 관리',
    items: [
      {
        id: 'order-history',
        label: '주문 내역',
        path: '/order',
        icon: <ShoppingBag className="h-4 w-4" />,
      },
      {
        id: 'reviews',
        label: '리뷰 관리',
        path: '/reviews',
        icon: <Star className="h-4 w-4" />,
      },
      {
        id: 'reorder',
        label: '재주문',
        path: '/reorder',
        icon: <Clock className="h-4 w-4" />,
      },
    ],
  },
  {
    title: '계정 설정',
    items: [
      {
        id: 'address',
        label: '주소 관리',
        path: '/address',
        icon: <MapPin className="h-4 w-4" />,
      },
      {
        id: 'notifications',
        label: '알림 설정',
        path: '/settings/notification',
        icon: <Bell className="h-4 w-4" />,
      },
      {
        id: 'app-settings',
        label: '앱 설정',
        path: '/settings',
        icon: <Settings className="h-4 w-4" />,
      },
    ],
  },
  {
    title: '고객지원',
    items: [
      {
        id: 'customer-service',
        label: '고객센터',
        path: '/support',
        icon: <HelpCircle className="h-4 w-4" />,
      },
    ],
  },
];
