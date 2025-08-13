import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import { Button } from '@/components/ui/button';
import { StopCircle, AlertTriangle } from 'lucide-react';

interface EndStreamAlertDialogProps {
  onConfirm: () => void | Promise<void>;
  isLoading?: boolean;
  trigger?: React.ReactNode;
  streamTitle?: string;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
}

export const EndStreamAlertDialog = ({
  onConfirm,
  isLoading = false,
  trigger,
  streamTitle = '현재 스트림',
  open,
  onOpenChange,
}: EndStreamAlertDialogProps) => {
  const defaultTrigger = (
    <Button variant="destructive" className="gap-2" disabled={isLoading}>
      <StopCircle className="h-4 w-4" />
      {isLoading ? '종료 중...' : '방송 종료'}
    </Button>
  );

  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      {trigger !== undefined && (
        <AlertDialogTrigger asChild>
          {trigger || defaultTrigger}
        </AlertDialogTrigger>
      )}
      <AlertDialogContent>
        <AlertDialogHeader>
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-red-100 dark:bg-red-900/20">
              <AlertTriangle className="h-6 w-6 text-red-600 dark:text-red-500" />
            </div>
            <div>
              <AlertDialogTitle>
                라이브 방송을 종료하시겠습니까?
              </AlertDialogTitle>
            </div>
          </div>
        </AlertDialogHeader>
        <AlertDialogDescription className="space-y-2">
          <p>
            &quot;<strong>{streamTitle}</strong>&quot; 방송이 즉시 종료됩니다.
          </p>
          <p className="text-muted-foreground text-sm">
            • 시청자들의 연결이 끊어집니다
            <br />
            • 방송 기록이 저장됩니다
            <br />• 이 작업은 되돌릴 수 없습니다
          </p>
        </AlertDialogDescription>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={isLoading}>취소</AlertDialogCancel>
          <AlertDialogAction
            onClick={onConfirm}
            disabled={isLoading}
            className="bg-red-600 hover:bg-red-700 focus:ring-red-600"
          >
            {isLoading ? '종료 중...' : '방송 종료'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};
