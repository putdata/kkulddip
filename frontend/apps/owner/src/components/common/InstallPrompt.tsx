import { useState } from 'react';
import { Download, Smartphone, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { useInstallPrompt } from '@/hooks/useInstallPrompt';

interface InstallPromptProps {
  onDismiss?: () => void;
}

const InstallPrompt = ({ onDismiss }: InstallPromptProps) => {
  const { isInstallable, needsManualInstall, showInstallPrompt } =
    useInstallPrompt();
  const [isVisible, setIsVisible] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  if (!isVisible || (!isInstallable && !needsManualInstall)) {
    return null;
  }

  const handleInstall = async () => {
    setIsLoading(true);
    const success = await showInstallPrompt();
    setIsLoading(false);

    if (success) {
      setIsVisible(false);
      onDismiss?.();
    }
  };

  const handleDismiss = () => {
    setIsVisible(false);
    onDismiss?.();
  };

  return (
    <Card className="fixed bottom-4 left-4 right-4 z-50 border-blue-200 bg-blue-50 shadow-lg md:left-auto md:right-4 md:w-96">
      <CardHeader className="pb-2">
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2 text-sm text-blue-900">
            <Smartphone className="h-4 w-4" />
            앱으로 설치하기
          </CardTitle>
          <Button
            variant="ghost"
            size="sm"
            onClick={handleDismiss}
            className="h-6 w-6 p-0 text-blue-700 hover:bg-blue-200"
          >
            <X className="h-4 w-4" />
          </Button>
        </div>
      </CardHeader>
      <CardContent className="space-y-3">
        <p className="text-sm text-blue-800">
          꿀띱 사장님을 홈 화면에 추가하여 더 빠르고 편리하게 이용하세요!
        </p>

        {isInstallable && (
          <Button
            onClick={handleInstall}
            disabled={isLoading}
            className="w-full bg-blue-600 hover:bg-blue-700"
            size="sm"
          >
            <Download className="mr-2 h-4 w-4" />
            {isLoading ? '설치 중...' : '지금 설치하기'}
          </Button>
        )}

        {needsManualInstall && (
          <Alert>
            <Smartphone className="h-4 w-4" />
            <AlertDescription className="text-sm">
              <strong>Safari에서 설치하는 방법:</strong>
              <br />
              1. 화면 하단의 공유 버튼 탭
              <br />
              2. "홈 화면에 추가" 선택
              <br />
              3. "추가" 버튼 탭
            </AlertDescription>
          </Alert>
        )}
      </CardContent>
    </Card>
  );
};

export default InstallPrompt;
