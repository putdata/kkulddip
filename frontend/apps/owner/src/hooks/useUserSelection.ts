import { useAuthStore, useUserStore } from 'common';
import { useNavigate } from 'react-router-dom';
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
 * 사용자 메뉴 선택 및 처리를 위한 훅
 * 메뉴 아이템과 로그아웃 기능을 제공합니다
 */
export const useUserSelection = (options: UseUserSelectionOptions = {}) => {
  const { clearAuth } = useAuthStore();
  const { clearUser } = useUserStore();
  const navigate = useNavigate();

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
      console.log('정산 페이지로 이동');
    }
  };

  /**
   * 알림 처리
   */
  const handleNotifications = () => {
    if (options.onNotifications) {
      options.onNotifications();
    } else {
      console.log('알림 페이지로 이동');
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
