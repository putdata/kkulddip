import { useState } from 'react';
import type { DdipBox } from '@/types/ddipbox';

/**
 * 띱박스 Dialog 상태를 관리하는 훅
 *
 * @description
 * 띱박스 추가/편집/삭제/수량 Dialog의 상태와 선택된 띱박스를 관리합니다.
 *
 * @returns Dialog 상태와 핸들러 함수들
 */
export const useDdipboxDialog = () => {
  const [selectedDdipbox, setSelectedDdipbox] = useState<DdipBox | null>(null);
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [showEditDialog, setShowEditDialog] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [showQuantityDialog, setShowQuantityDialog] = useState(false);

  const handleEditClick = (ddipbox: DdipBox) => {
    setSelectedDdipbox(ddipbox);
    setShowEditDialog(true);
  };

  const handleDeleteClick = (ddipbox: DdipBox) => {
    setSelectedDdipbox(ddipbox);
    setShowDeleteDialog(true);
  };

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
