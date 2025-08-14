import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AlertTriangle, Package, TrendingDown } from 'lucide-react';
import type { HighInventoryDdipBox } from '@/types/analytics';

interface HighInventoryAlertProps {
  highInventoryItems: HighInventoryDdipBox[];
}

const HighInventoryAlert = ({
  highInventoryItems,
}: HighInventoryAlertProps) => {
  // 재고 위험도 계산
  const getInventoryRisk = (item: HighInventoryDdipBox) => {
    const remainingRatio = item.remainingCount / item.dailyCount;

    if (remainingRatio >= 0.8) {
      return {
        level: 'high',
        color: 'destructive',
        text: '긴급',
        percentage: remainingRatio * 100,
      };
    } else if (remainingRatio >= 0.6) {
      return {
        level: 'medium',
        color: 'default',
        text: '주의',
        percentage: remainingRatio * 100,
      };
    } else {
      return {
        level: 'low',
        color: 'secondary',
        text: '보통',
        percentage: remainingRatio * 100,
      };
    }
  };

  // 위험도 높은 순서로 정렬
  const sortedItems = [...highInventoryItems].sort((a, b) => {
    const ratioA = a.remainingCount / a.dailyCount;
    const ratioB = b.remainingCount / b.dailyCount;
    return ratioB - ratioA;
  });

  // 전체 알림 수준 계산
  const getOverallAlertLevel = () => {
    if (highInventoryItems.length === 0) {
      return null;
    }

    const highRiskCount = highInventoryItems.filter(
      item => getInventoryRisk(item).level === 'high',
    ).length;

    if (highRiskCount > 0) {
      return {
        level: 'high',
        color: 'destructive',
        message: `${highRiskCount}개 상품이 재고 과다 상태입니다`,
        icon: AlertTriangle,
      };
    } else if (highInventoryItems.length > 3) {
      return {
        level: 'medium',
        color: 'default',
        message: '재고 관리가 필요한 상품들이 있습니다',
        icon: Package,
      };
    } else {
      return {
        level: 'low',
        color: 'secondary',
        message: '재고 상태가 양호합니다',
        icon: Package,
      };
    }
  };

  const overallAlert = getOverallAlertLevel();

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <AlertTriangle className="h-5 w-5" />
          재고 과다 알림
        </CardTitle>
        <CardDescription>
          재고가 많이 남은 상품들에 대한 할인 적용을 고려해보세요
        </CardDescription>
      </CardHeader>
      <CardContent>
        {highInventoryItems.length > 0 ? (
          <div className="space-y-4">
            {/* 전체 알림 상태 */}
            {overallAlert && (
              <Alert
                className={
                  overallAlert.level === 'high' ? 'border-destructive' : ''
                }
              >
                <overallAlert.icon className="h-4 w-4" />
                <AlertDescription>{overallAlert.message}</AlertDescription>
              </Alert>
            )}

            {/* 재고 과다 상품 목록 */}
            <div className="space-y-3">
              {sortedItems.slice(0, 5).map(item => {
                const risk = getInventoryRisk(item);

                return (
                  <div
                    key={item.ddipBoxId}
                    className="flex items-center justify-between rounded-lg border p-3"
                  >
                    <div className="flex-1">
                      <div className="mb-1 flex items-center gap-2">
                        <span className="font-medium">{item.ddipBoxName}</span>
                        <Badge
                          variant={
                            risk.color as
                              | 'default'
                              | 'destructive'
                              | 'outline'
                              | 'secondary'
                          }
                          className="text-xs"
                        >
                          {risk.text}
                        </Badge>
                      </div>
                      <div className="text-muted-foreground text-sm">
                        잔여: {item.remainingCount}개 / 일일: {item.dailyCount}
                        개
                      </div>
                    </div>

                    <div className="text-right">
                      <div className="font-bold text-orange-600">
                        {risk.percentage}%
                      </div>
                      <div className="text-muted-foreground text-xs">
                        재고율
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* 추천 액션 */}
            <div className="bg-muted/50 mt-4 rounded-lg p-3">
              <div className="mb-2 flex items-center gap-2">
                <TrendingDown className="h-4 w-4 text-blue-600" />
                <span className="text-sm font-medium">권장 조치</span>
              </div>
              <ul className="text-muted-foreground space-y-1 text-sm">
                <li>• 재고 과다 상품에 할인 이벤트 적용</li>
                <li>• 번들 상품으로 판매 촉진</li>
                <li>• 다음 발주 시 수량 조정</li>
              </ul>
            </div>

            {/* 추가 상품이 있는 경우 */}
            {highInventoryItems.length > 5 && (
              <div className="text-center">
                <Badge variant="outline" className="text-xs">
                  +{highInventoryItems.length - 5}개 상품 더 있음
                </Badge>
              </div>
            )}
          </div>
        ) : (
          <div className="text-muted-foreground flex h-32 flex-col items-center justify-center">
            <Package className="mb-2 h-8 w-8" />
            <p className="text-sm">재고 과다 상품이 없습니다</p>
            <p className="text-xs">재고 관리가 잘 되고 있습니다!</p>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default HighInventoryAlert;
