import { useAuthStore, useUserStore } from 'common';
import { useNavigate, useParams } from 'react-router-dom';
import { User, CreditCard, Bell, Settings } from 'lucide-react';
import { ROUTE_PATH } from '@/router/route-path';

export interface MenuItemConfig {
  icon: typeof User;
  label: string;
  onClick: () => void;
}

export interface UseUserSelectionOptions {
  onSettings?: () => void;
  onBilling?: () => void;
  onNotifications?: () => void;
}

/**
 * 사용자 메뉴 선택 및 처리를 위한 커스텀 훅
 *
 * @description
 * 헤더의 사용자 드롭다운 메뉴 항목들과 로그아웃 처리를 관리합니다.
 */
export const useUserSelection = (options: UseUserSelectionOptions = {}) => {
  const { clearAuth } = useAuthStore();
  const { clearUser } = useUserStore();
  const navigate = useNavigate();
  const { storeId } = useParams<{ storeId: string }>();

  /**
   * 설정 처리
   */
  const handleSettings = () => {
    if (options.onSettings) {
      options.onSettings();
    } else {
      console.log('설정 페이지로 이동');
    }
  };

  /**
   * 정산 처리
   */
  const handleBilling = () => {
    if (options.onBilling) {
      options.onBilling();
    } else {
      navigate(
        ROUTE_PATH.STORE.OWNER_SETTLEMENT.replace(':storeId', storeId || '1'),
      );
    }
  };

  /**
   * 알림 처리
   */
  const handleNotifications = () => {
    if (options.onNotifications) {
      options.onNotifications();
    } else {
      navigate(
        ROUTE_PATH.STORE.OWNER_NOTIFICATIONS.replace(
          ':storeId',
          storeId || '1',
        ),
      );
    }
  };

  /**
   * 로그아웃 처리
   */
  const handleLogout = () => {
    clearAuth();
    clearUser();
    navigate(ROUTE_PATH.LOGIN);
  };

  /**
   * 메뉴 아이템 설정 배열
   */
  const menuItems: MenuItemConfig[] = [
    { icon: Settings, label: '설정', onClick: handleSettings },
    { icon: Bell, label: '알림', onClick: handleNotifications },
    { icon: CreditCard, label: '정산', onClick: handleBilling },
  ];

  return {
    /** 메뉴 아이템 설정 배열 */
    menuItems,
    /** 로그아웃 처리 */
    handleLogout,
  };
};
