import { type ReactNode } from 'react';

/**
 * 메뉴 아이템 인터페이스 (icon 직접 포함)
 */
export interface MenuItem {
  id: string;
  label: string;
  path: string;
  icon: ReactNode;
}

/**
 * 메뉴 섹션 인터페이스
 */
export interface MenuSection {
  title: string;
  items: MenuItem[];
}

/**
 * MenuSection 컴포넌트 Props
 */
export interface MenuSectionProps {
  title: string;
  items: MenuItem[];
}
