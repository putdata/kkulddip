import { useState } from 'react';
import type { CreateStreamRequest } from 'common';
import { useStreamForm } from '@/hooks/useStreamForm';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import { Plus } from 'lucide-react';

interface CreateStreamDialogProps {
  onSubmit: (data: CreateStreamRequest) => void | Promise<void>;
  isLoading?: boolean;
  trigger?: React.ReactNode;
}

export const CreateStreamDialog = ({ 
  onSubmit, 
  isLoading = false,
  trigger 
}: CreateStreamDialogProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const { selectedStore } = useStoreSelection();

  const handleSubmitSuccess = async (data: CreateStreamRequest) => {
    await onSubmit(data);
    setIsOpen(false);
    resetForm();
  };

  const {
    title,
    description,
    isValid,
    handleSubmit,
    handleTitleChange,
    handleDescriptionChange,
    resetForm,
  } = useStreamForm({
    storeId: selectedStore?.id || 0,
    onSubmit: handleSubmitSuccess,
  });

  const defaultTrigger = (
    <Button className="gap-2">
      <Plus className="h-4 w-4" />
      새 스트림 생성
    </Button>
  );

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        {trigger || defaultTrigger}
      </DialogTrigger>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>새 라이브 스트림 생성</DialogTitle>
        </DialogHeader>
        
        <Card>
          <CardContent className="pt-6">
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <label htmlFor="title" className="text-sm font-medium">
                  스트림 제목 <span className="text-red-500">*</span>
                </label>
                <Input
                  id="title"
                  value={title}
                  onChange={handleTitleChange}
                  placeholder="스트림 제목을 입력하세요"
                  maxLength={200}
                  disabled={isLoading}
                />
                <p className="text-xs text-muted-foreground">
                  {title.length}/200자
                </p>
              </div>

              <div className="space-y-2">
                <label htmlFor="description" className="text-sm font-medium">
                  설명 (선택)
                </label>
                <Textarea
                  id="description"
                  value={description}
                  onChange={handleDescriptionChange}
                  placeholder="스트림에 대한 설명을 입력하세요"
                  maxLength={1000}
                  rows={3}
                  disabled={isLoading}
                />
                <p className="text-xs text-muted-foreground">
                  {description.length}/1000자
                </p>
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setIsOpen(false)}
                  disabled={isLoading}
                >
                  취소
                </Button>
                <Button
                  type="submit"
                  disabled={!isValid || isLoading}
                >
                  {isLoading ? '생성 중...' : '스트림 생성'}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </DialogContent>
    </Dialog>
  );
};