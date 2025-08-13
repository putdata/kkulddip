import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { RefreshCw, Home, AlertCircle } from 'lucide-react';
import { ApiError } from 'common';

interface OrdersManagementErrorProps {
  error: Error;
  onRetry: () => void;
}

const OrdersManagementError = ({
  error,
  onRetry,
}: OrdersManagementErrorProps) => {
  const navigate = useNavigate();

  const getErrorMessage = (error: Error): string => {
    if (error instanceof ApiError && error.response?.message) {
      return error.response.message;
    }
    return error.message || '주문 정보를 불러오는 중 문제가 발생했습니다.';
  };

  const handleGoHome = () => {
    navigate('/');
  };

  const errorMessage = getErrorMessage(error);

  return (
    <div className="flex min-h-96 items-center justify-center p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="pb-4 text-center">
          <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-gray-100">
            <AlertCircle className="h-12 w-12 text-red-500" />
          </div>
          <CardTitle className="text-xl">문제가 발생했습니다</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4 text-center">
          <p className="text-muted-foreground text-sm">{errorMessage}</p>

          <div className="flex flex-col gap-2 pt-2">
            <Button onClick={onRetry} className="gap-2">
              <RefreshCw className="h-4 w-4" />
              다시 시도
            </Button>

            <Button variant="outline" onClick={handleGoHome} className="gap-2">
              <Home className="h-4 w-4" />
              홈으로 이동
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default OrdersManagementError;
