import { useState } from 'react';
import type { DdipBox } from '@/types/ddipbox';

/**
 * 띱박스 Dialog 상태를 관리하는 훅
 *
 * @description
 * 띱박스 추가/편집/삭제/수량 Dialog의 상태와 선택된 띱박스를 관리합니다.
 */
export const useDdipboxDialog = () => {
  /**
   * 선택된 띱박스 정보
   */
  const [selectedDdipbox, setSelectedDdipbox] = useState<DdipBox | null>(null);

  /**
   * 띱박스 추가 다이얼로그 표시 여부
   */
  const [showAddDialog, setShowAddDialog] = useState(false);

  /**
   * 띱박스 편집 다이얼로그 표시 여부
   */
  const [showEditDialog, setShowEditDialog] = useState(false);

  /**
   * 띱박스 삭제 다이얼로그 표시 여부
   */
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

  /**
   * 띱박스 수량 변경 다이얼로그 표시 여부
   */
  const [showQuantityDialog, setShowQuantityDialog] = useState(false);

  /**
   * 띱박스 편집 버튼 클릭 핸들러
   */
  const handleEditClick = (ddipbox: DdipBox) => {
    setSelectedDdipbox(ddipbox);
    setShowEditDialog(true);
  };

  /**
   * 띱박스 삭제 버튼 클릭 핸들러
   */
  const handleDeleteClick = (ddipbox: DdipBox) => {
    setSelectedDdipbox(ddipbox);
    setShowDeleteDialog(true);
  };

  /**
   * 띱박스 수량 변경 버튼 클릭 핸들러
   */
  const handleQuantityClick = (ddipbox: DdipBox) => {
    setSelectedDdipbox(ddipbox);
    setShowQuantityDialog(true);
  };

  return {
    selectedDdipbox,
    showAddDialog,
    showEditDialog,
    showDeleteDialog,
    showQuantityDialog,
    setShowAddDialog,
    setShowEditDialog,
    setShowDeleteDialog,
    setShowQuantityDialog,
    handleEditClick,
    handleDeleteClick,
    handleQuantityClick,
  };
};
