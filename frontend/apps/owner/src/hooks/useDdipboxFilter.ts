import { useState, useMemo } from 'react';
import type { DdipBox } from '@/types/ddipbox';
import type { DdipboxStatusFilter } from '@/types/ddipboxManagement';

/**
 * 띱박스 검색 및 필터링을 관리하는 커스텀 훅
 *
 * @description
 * 띱박스 목록을 검색어와 상태 필터로 필터링합니다.
 */
export const useDdipboxFilter = (ddipboxes: DdipBox[]) => {
  /**
   * 검색어 상태
   */
  const [searchTerm, setSearchTerm] = useState('');
  
  /**
   * 상태 필터 (전체/활성/비활성)
   */
  const [statusFilter, setStatusFilter] = useState<DdipboxStatusFilter>('all');

  /**
   * 검색어와 상태 필터로 필터링된 띱박스 목록
   */
  const filteredDdipboxes = useMemo(() => {
    return ddipboxes.filter((ddipbox: DdipBox) => {
      const matchesSearch =
        ddipbox.ddipboxName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        ddipbox.category.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesStatus =
        statusFilter === 'all' ||
        (statusFilter === 'active' && ddipbox.isActive) ||
        (statusFilter === 'inactive' && !ddipbox.isActive);
      return matchesSearch && matchesStatus;
    });
  }, [ddipboxes, searchTerm, statusFilter]);

  return {
    searchTerm,
    statusFilter,
    filteredDdipboxes,
    setSearchTerm,
    setStatusFilter,
  };
};
