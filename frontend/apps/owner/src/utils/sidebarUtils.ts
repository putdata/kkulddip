import { matchPath } from 'react-router-dom';
import { sidebarItems } from '@/constants/sidebarItems';
import { ROUTE_PATH } from '@/router/route-path';

/**
 * 현재 경로를 기반으로 페이지 제목을 반환합니다
 * @param pathname - 현재 경로
 * @returns 페이지 제목
 */
export const getPageTitle = (pathname: string): string => {
  const item = sidebarItems.find(route =>
    matchPath({ path: route.path }, pathname),
  );
  return item?.label ?? '관리자 패널';
};

/**
 * 현재 경로를 기반으로 매칭되는 라우트 경로를 반환합니다
 * @param pathname - 현재 경로
 * @returns 매칭되는 라우트 경로
 */
export const getCurrentPageRoute = (pathname: string): string => {
  const item = sidebarItems.find(route =>
    matchPath({ path: route.path }, pathname),
  );
  return item?.path || ROUTE_PATH.STORE.DASHBOARD;
};
