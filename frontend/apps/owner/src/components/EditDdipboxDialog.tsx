import { useState, useEffect } from 'react';
import { Package, Tag, DollarSign, Hash } from 'lucide-react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { useUpdateDdipbox } from '@/queries/ddipbox';
import type { DdipBox, UpdateDdipBoxRequest } from '@/types/ddipbox';

interface EditDdipboxDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  ddipbox: DdipBox;
  storeId: number;
}

const EditDdipboxDialog = ({
  open,
  onOpenChange,
  ddipbox,
  storeId,
}: EditDdipboxDialogProps) => {
  const [formData, setFormData] = useState<UpdateDdipBoxRequest>({
    ddipboxName: ddipbox.ddipboxName,
    description: ddipbox.description,
    category: ddipbox.category,
    originalPrice: ddipbox.originalPrice,
    salePrice: ddipbox.salePrice,
    dailyQuantity: ddipbox.dailyQuantity,
    maxPerCustomer: ddipbox.maxPerCustomer,
  });

  const updateDdipboxMutation = useUpdateDdipbox();

  // 다이얼로그가 열릴 때마다 폼 데이터 초기화
  useEffect(() => {
    if (open) {
      setFormData({
        ddipboxName: ddipbox.ddipboxName,
        description: ddipbox.description,
        category: ddipbox.category,
        originalPrice: ddipbox.originalPrice,
        salePrice: ddipbox.salePrice,
        dailyQuantity: ddipbox.dailyQuantity,
        maxPerCustomer: ddipbox.maxPerCustomer,
      });
    }
  }, [open, ddipbox]);

  const handleInputChange = (
    field: keyof UpdateDdipBoxRequest,
    value: string | number,
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]:
        field.includes('Price') ||
        field.includes('Quantity') ||
        field === 'maxPerCustomer'
          ? value === ''
            ? undefined
            : Number(value)
          : value,
    }));
  };

  const validateForm = () => {
    if (formData.ddipboxName && !formData.ddipboxName.trim()) {
      return false;
    }

    if (formData.category && !formData.category.trim()) {
      return false;
    }

    if (formData.originalPrice !== undefined && formData.originalPrice <= 0) {
      return false;
    }

    if (formData.salePrice !== undefined && formData.salePrice <= 0) {
      return false;
    }

    const originalPrice = formData.originalPrice ?? ddipbox.originalPrice;
    const salePrice = formData.salePrice ?? ddipbox.salePrice;

    if (salePrice > originalPrice) {
      return false;
    }

    if (formData.dailyQuantity !== undefined && formData.dailyQuantity < 1) {
      return false;
    }

    if (formData.maxPerCustomer !== undefined && formData.maxPerCustomer < 1) {
      return false;
    }

    const dailyQuantity = formData.dailyQuantity ?? ddipbox.dailyQuantity;
    const maxPerCustomer = formData.maxPerCustomer ?? ddipbox.maxPerCustomer;

    if (maxPerCustomer > dailyQuantity) {
      return false;
    }

    if (formData.ddipboxName && formData.ddipboxName.length > 100) {
      return false;
    }

    if (formData.description && formData.description.length > 1000) {
      return false;
    }

    if (formData.category && formData.category.length > 50) {
      return false;
    }

    return true;
  };

  const hasChanges = () => {
    return (
      formData.ddipboxName !== ddipbox.ddipboxName ||
      formData.description !== ddipbox.description ||
      formData.category !== ddipbox.category ||
      formData.originalPrice !== ddipbox.originalPrice ||
      formData.salePrice !== ddipbox.salePrice ||
      formData.dailyQuantity !== ddipbox.dailyQuantity ||
      formData.maxPerCustomer !== ddipbox.maxPerCustomer
    );
  };

  const handleSubmit = () => {
    if (!validateForm()) {
      return;
    }

    if (!hasChanges()) {
      return;
    }

    // undefined 값들을 제거하여 실제 변경된 필드만 전송
    const updateData: UpdateDdipBoxRequest = {};

    if (formData.ddipboxName !== ddipbox.ddipboxName && formData.ddipboxName) {
      updateData.ddipboxName = formData.ddipboxName.trim();
    }
    if (formData.description !== ddipbox.description) {
      updateData.description = formData.description?.trim() || undefined;
    }
    if (formData.category !== ddipbox.category && formData.category) {
      updateData.category = formData.category.trim();
    }
    if (
      formData.originalPrice !== ddipbox.originalPrice &&
      formData.originalPrice !== undefined
    ) {
      updateData.originalPrice = formData.originalPrice;
    }
    if (
      formData.salePrice !== ddipbox.salePrice &&
      formData.salePrice !== undefined
    ) {
      updateData.salePrice = formData.salePrice;
    }
    if (
      formData.dailyQuantity !== ddipbox.dailyQuantity &&
      formData.dailyQuantity !== undefined
    ) {
      updateData.dailyQuantity = formData.dailyQuantity;
    }
    if (
      formData.maxPerCustomer !== ddipbox.maxPerCustomer &&
      formData.maxPerCustomer !== undefined
    ) {
      updateData.maxPerCustomer = formData.maxPerCustomer;
    }

    updateDdipboxMutation.mutate(
      {
        storeId,
        ddipboxId: ddipbox.ddipboxId,
        data: updateData,
      },
      {
        onSuccess: () => {
          onOpenChange(false);
        },
      },
    );
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90vh] max-w-2xl overflow-y-auto">
        <DialogHeader>
          <DialogTitle>띱박스 수정</DialogTitle>
          <DialogDescription>
            {ddipbox.ddipboxName}의 정보를 수정합니다.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* 기본 정보 */}
          <div className="space-y-4">
            <h3 className="text-sm font-medium">기본 정보</h3>

            <div className="space-y-2">
              <Label htmlFor="ddipboxName">띱박스 이름</Label>
              <div className="relative">
                <Package className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                <Input
                  id="ddipboxName"
                  placeholder="띱박스 이름을 입력하세요"
                  value={formData.ddipboxName || ''}
                  onChange={e =>
                    handleInputChange('ddipboxName', e.target.value)
                  }
                  className="pl-10"
                  maxLength={100}
                />
              </div>
              <div className="text-muted-foreground text-xs">
                {(formData.ddipboxName || '').length}/100자
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="category">카테고리</Label>
              <div className="relative">
                <Tag className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                <Input
                  id="category"
                  placeholder="예: 한식, 양식, 디저트 등"
                  value={formData.category || ''}
                  onChange={e => handleInputChange('category', e.target.value)}
                  className="pl-10"
                  maxLength={50}
                />
              </div>
              <div className="text-muted-foreground text-xs">
                {(formData.category || '').length}/50자
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">설명</Label>
              <Textarea
                id="description"
                placeholder="띱박스 설명을 입력하세요"
                value={formData.description || ''}
                onChange={e => handleInputChange('description', e.target.value)}
                rows={3}
                maxLength={1000}
              />
              <div className="text-muted-foreground text-xs">
                {(formData.description || '').length}/1000자
              </div>
            </div>
          </div>

          {/* 가격 정보 */}
          <div className="space-y-4">
            <h3 className="text-sm font-medium">가격 정보</h3>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="originalPrice">정가</Label>
                <div className="relative">
                  <DollarSign className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="originalPrice"
                    type="number"
                    placeholder="0"
                    value={formData.originalPrice || ''}
                    onChange={e =>
                      handleInputChange('originalPrice', e.target.value)
                    }
                    className="pl-10"
                    min="0"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="salePrice">판매가</Label>
                <div className="relative">
                  <DollarSign className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="salePrice"
                    type="number"
                    placeholder="0"
                    value={formData.salePrice || ''}
                    onChange={e =>
                      handleInputChange('salePrice', e.target.value)
                    }
                    className="pl-10"
                    min="0"
                  />
                </div>
              </div>
            </div>

            {(formData.originalPrice || ddipbox.originalPrice) > 0 &&
              (formData.salePrice || ddipbox.salePrice) > 0 && (
                <div className="text-muted-foreground text-sm">
                  할인율:{' '}
                  {Math.round(
                    (((formData.originalPrice || ddipbox.originalPrice) -
                      (formData.salePrice || ddipbox.salePrice)) /
                      (formData.originalPrice || ddipbox.originalPrice)) *
                      100,
                  )}
                  %
                </div>
              )}
          </div>

          {/* 수량 정보 */}
          <div className="space-y-4">
            <h3 className="text-sm font-medium">수량 정보</h3>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="dailyQuantity">일일 수량</Label>
                <div className="relative">
                  <Hash className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="dailyQuantity"
                    type="number"
                    placeholder="1"
                    value={formData.dailyQuantity || ''}
                    onChange={e =>
                      handleInputChange('dailyQuantity', e.target.value)
                    }
                    className="pl-10"
                    min="1"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="maxPerCustomer">고객당 최대 구매</Label>
                <div className="relative">
                  <Hash className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="maxPerCustomer"
                    type="number"
                    placeholder="1"
                    value={formData.maxPerCustomer || ''}
                    onChange={e =>
                      handleInputChange('maxPerCustomer', e.target.value)
                    }
                    className="pl-10"
                    min="1"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            disabled={updateDdipboxMutation.isPending}
          >
            취소
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={updateDdipboxMutation.isPending || !hasChanges()}
          >
            {updateDdipboxMutation.isPending ? '수정 중...' : '수정'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default EditDdipboxDialog;
