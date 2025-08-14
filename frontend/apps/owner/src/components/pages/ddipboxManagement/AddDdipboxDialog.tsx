import { type ReactNode } from 'react';
import { Plus, Package, Tag, DollarSign, Hash } from 'lucide-react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { useCreateDdipbox } from '@/queries/ddipbox';
import { useDdipboxForm } from '@/hooks/useDdipboxForm';
import type { CreateDdipBoxRequest } from '@/types/ddipbox';

interface AddDdipboxDialogProps {
  storeId: number;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  children?: ReactNode;
}

const AddDdipboxDialog = ({
  storeId,
  open,
  onOpenChange,
  children,
}: AddDdipboxDialogProps) => {
  const createDdipboxMutation = useCreateDdipbox();

  const {
    formData,
    handleInputChange,
    validateForm,
    getDiscountRate,
    resetForm,
    getSubmitData,
  } = useDdipboxForm({ mode: 'create' });

  const createFormData = formData as CreateDdipBoxRequest;

  const handleSubmit = () => {
    if (!validateForm()) {
      return;
    }

    createDdipboxMutation.mutate(
      {
        storeId,
        data: getSubmitData() as CreateDdipBoxRequest,
      },
      {
        onSuccess: () => {
          resetForm();
          onOpenChange(false);
        },
      },
    );
  };

  return (
    <Dialog
      open={open}
      onOpenChange={newOpen => {
        onOpenChange(newOpen);
        if (!newOpen) {
          resetForm();
        }
      }}
    >
      <DialogTrigger asChild>
        {children || (
          <Button>
            <Plus className="mr-2 h-4 w-4" />
            띱박스 등록
          </Button>
        )}
      </DialogTrigger>

      <DialogContent className="max-h-[90vh] max-w-2xl overflow-y-auto">
        <DialogHeader>
          <DialogTitle>새 띱박스 등록</DialogTitle>
          <DialogDescription>
            새로운 띱박스를 등록합니다. 모든 필수 정보를 입력해주세요.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* 기본 정보 */}
          <div className="space-y-4">
            <h3 className="text-sm font-medium">기본 정보</h3>

            <div className="space-y-2">
              <Label htmlFor="ddipboxName">
                띱박스 이름 <span className="text-destructive">*</span>
              </Label>
              <div className="relative">
                <Package className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                <Input
                  id="ddipboxName"
                  placeholder="띱박스 이름을 입력하세요"
                  value={createFormData.ddipboxName}
                  onChange={e =>
                    handleInputChange('ddipboxName', e.target.value)
                  }
                  className="pl-10"
                  maxLength={100}
                />
              </div>
              <div className="text-muted-foreground text-xs">
                {createFormData.ddipboxName.length}/100자
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="category">
                카테고리 <span className="text-destructive">*</span>
              </Label>
              <div className="relative">
                <Tag className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                <Input
                  id="category"
                  placeholder="예: 한식, 양식, 디저트 등"
                  value={createFormData.category}
                  onChange={e => handleInputChange('category', e.target.value)}
                  className="pl-10"
                  maxLength={50}
                />
              </div>
              <div className="text-muted-foreground text-xs">
                {createFormData.category.length}/50자
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">설명</Label>
              <Textarea
                id="description"
                placeholder="띱박스 설명을 입력하세요 (선택사항)"
                value={createFormData.description}
                onChange={e => handleInputChange('description', e.target.value)}
                rows={3}
                maxLength={1000}
              />
              <div className="text-muted-foreground text-xs">
                {createFormData.description?.length || 0}/1000자
              </div>
            </div>
          </div>

          {/* 가격 정보 */}
          <div className="space-y-4">
            <h3 className="text-sm font-medium">가격 정보</h3>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="originalPrice">
                  정가 <span className="text-destructive">*</span>
                </Label>
                <div className="relative">
                  <DollarSign className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="originalPrice"
                    type="number"
                    placeholder="0"
                    value={createFormData.originalPrice || ''}
                    onChange={e =>
                      handleInputChange('originalPrice', e.target.value)
                    }
                    className="pl-10"
                    min="0"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="salePrice">
                  판매가 <span className="text-destructive">*</span>
                </Label>
                <div className="relative">
                  <DollarSign className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="salePrice"
                    type="number"
                    placeholder="0"
                    value={createFormData.salePrice || ''}
                    onChange={e =>
                      handleInputChange('salePrice', e.target.value)
                    }
                    className="pl-10"
                    min="0"
                  />
                </div>
              </div>
            </div>

            {createFormData.originalPrice > 0 &&
              createFormData.salePrice > 0 && (
                <div className="text-muted-foreground text-sm">
                  할인율: {getDiscountRate()}%
                </div>
              )}
          </div>

          {/* 수량 정보 */}
          <div className="space-y-4">
            <h3 className="text-sm font-medium">수량 정보</h3>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="dailyQuantity">
                  일일 수량 <span className="text-destructive">*</span>
                </Label>
                <div className="relative">
                  <Hash className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="dailyQuantity"
                    type="number"
                    placeholder="1"
                    value={createFormData.dailyQuantity || ''}
                    onChange={e =>
                      handleInputChange('dailyQuantity', e.target.value)
                    }
                    className="pl-10"
                    min="1"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="maxPerCustomer">
                  고객당 최대 구매 <span className="text-destructive">*</span>
                </Label>
                <div className="relative">
                  <Hash className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
                  <Input
                    id="maxPerCustomer"
                    type="number"
                    placeholder="1"
                    value={createFormData.maxPerCustomer || ''}
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
            disabled={createDdipboxMutation.isPending}
          >
            취소
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={createDdipboxMutation.isPending}
          >
            {createDdipboxMutation.isPending ? '등록 중...' : '등록'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default AddDdipboxDialog;
