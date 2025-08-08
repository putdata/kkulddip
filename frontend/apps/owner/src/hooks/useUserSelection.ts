import { useAuthStore, useUserStore } from 'common';
import { useNavigate } from 'react-router-dom';
import { User, CreditCard, Bell } from 'lucide-react';
import { ROUTE_PATH } from '@/router/route-path';

export interface MenuItemConfig {
  icon: typeof User;
  label: string;
  onClick: () => void;
}

/**
 * 사용자 메뉴 선택 및 처리를 위한 훅
 * 메뉴 아이템과 로그아웃 기능을 제공합니다
 */
export const useUserSelection = () => {
  const { clearAuth } = useAuthStore();
  const { clearUser } = useUserStore();
  const navigate = useNavigate();

  /**
   * 계정 페이지로 이동
   */
  const handleAccount = () => {
    // TODO: 계정 관리 페이지 구현 후 경로 추가
    console.log('계정 페이지로 이동');
  };

  /**
   * 정산 페이지로 이동
   */
  const handleBilling = () => {
    // TODO: 정산 관리 페이지 구현 후 경로 추가
    console.log('정산 페이지로 이동');
  };

  /**
   * 알림 페이지로 이동
   */
  const handleNotifications = () => {
    // TODO: 알림 관리 페이지 구현 후 경로 추가
    console.log('알림 페이지로 이동');
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
    { icon: User, label: '계정', onClick: handleAccount },
    { icon: CreditCard, label: '정산', onClick: handleBilling },
    { icon: Bell, label: '알림', onClick: handleNotifications },
  ];

  return {
    /** 메뉴 아이템 설정 배열 */
    menuItems,
    /** 로그아웃 처리 */
    handleLogout,
  };
};
