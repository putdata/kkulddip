import type { Store } from '@/types/store';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { useStoreEditForm } from '@/hooks/useStoreEditForm';

interface StoreEditDialogProps {
  store: Store | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const StoreEditDialog = ({
  store,
  open,
  onOpenChange,
}: StoreEditDialogProps) => {
  const { formData, updateField, handleSubmit, resetForm, isSubmitting } =
    useStoreEditForm({
      store,
      isOpen: open,
      onSuccess: () => onOpenChange(false),
    });

  const handleCancel = () => {
    onOpenChange(false);
    resetForm();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>가게 정보 수정</DialogTitle>
          <DialogDescription>가게 정보를 수정할 수 있습니다.</DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="storeName" className="text-sm font-medium">
                가게명
              </label>
              <Input
                id="storeName"
                value={formData.storeName || ''}
                onChange={e => updateField('storeName')(e.target.value)}
                placeholder="가게명을 입력하세요"
              />
            </div>
            <div>
              <label htmlFor="phone" className="text-sm font-medium">
                전화번호
              </label>
              <Input
                id="phone"
                value={formData.phone || ''}
                onChange={e => updateField('phone')(e.target.value)}
                placeholder="전화번호를 입력하세요"
              />
            </div>
          </div>
          <div>
            <label htmlFor="storeAddress" className="text-sm font-medium">
              주소
            </label>
            <Input
              id="storeAddress"
              value={formData.storeAddress || ''}
              onChange={e => updateField('storeAddress')(e.target.value)}
              placeholder="주소를 입력하세요"
            />
          </div>
          <div>
            <label htmlFor="description" className="text-sm font-medium">
              설명
            </label>
            <Textarea
              id="description"
              value={formData.description || ''}
              onChange={e => updateField('description')(e.target.value)}
              placeholder="가게 설명을 입력하세요"
              rows={3}
            />
          </div>
          <div>
            <label htmlFor="operatingHours" className="text-sm font-medium">
              운영시간
            </label>
            <Input
              id="operatingHours"
              value={formData.operatingHours || ''}
              onChange={e => updateField('operatingHours')(e.target.value)}
              placeholder="운영시간을 입력하세요"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="latitude" className="text-sm font-medium">
                위도
              </label>
              <Input
                id="latitude"
                type="number"
                step="any"
                min="-90"
                max="90"
                value={formData.latitude || ''}
                onChange={e =>
                  updateField('latitude')(parseFloat(e.target.value) || 0)
                }
                placeholder="위도를 입력하세요"
              />
            </div>
            <div>
              <label htmlFor="longitude" className="text-sm font-medium">
                경도
              </label>
              <Input
                id="longitude"
                type="number"
                step="any"
                min="-180"
                max="180"
                value={formData.longitude || ''}
                onChange={e =>
                  updateField('longitude')(parseFloat(e.target.value) || 0)
                }
                placeholder="경도를 입력하세요"
              />
            </div>
          </div>
        </div>
        <div className="flex justify-end gap-2">
          <Button variant="outline" onClick={handleCancel}>
            취소
          </Button>
          <Button onClick={handleSubmit} disabled={isSubmitting}>
            {isSubmitting ? '수정 중...' : '수정'}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default StoreEditDialog;
