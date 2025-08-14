import { AlertCircle, RefreshCw } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';

interface DdipboxManagementErrorProps {
  error: Error;
  onRetry: () => void;
}

const DdipboxManagementError = ({
  error,
  onRetry,
}: DdipboxManagementErrorProps) => {
  return (
    <Card className="mx-auto max-w-md">
      <CardContent className="flex flex-col items-center p-6 text-center">
        <div className="bg-destructive/10 mb-4 rounded-full p-3">
          <AlertCircle className="text-destructive h-6 w-6" />
        </div>
        <h3 className="mb-2 text-lg font-semibold">오류가 발생했습니다</h3>
        <p className="text-muted-foreground mb-4 text-sm">
          띱박스 정보를 불러오는 중 문제가 발생했습니다.
        </p>
        <p className="text-muted-foreground mb-4 text-xs">
          {error.message}
        </p>
        <Button onClick={onRetry} className="gap-2">
          <RefreshCw className="h-4 w-4" />
          다시 시도
        </Button>
      </CardContent>
    </Card>
  );
};

export default DdipboxManagementError;