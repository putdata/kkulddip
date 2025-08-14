import { useState, useMemo } from 'react';
import type { DdipBox } from '@/types/ddipbox';
import type { DdipboxStatusFilter } from '@/types/ddipboxManagement';

/**
 * 띱박스 검색 및 필터링을 관리하는 훅
 *
 * @description
 * 검색어와 상태 필터를 통해 띱박스 목록을 필터링합니다.
 *
 * @param {DdipBox[]} ddipboxes - 전체 띱박스 목록
 * @returns 필터링된 띱박스와 검색/필터 상태
 */
export const useDdipboxFilter = (ddipboxes: DdipBox[]) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<DdipboxStatusFilter>('all');

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
