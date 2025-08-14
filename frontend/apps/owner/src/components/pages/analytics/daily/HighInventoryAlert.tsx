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
import { useInventoryAnalytics } from '../hooks/useInventoryAnalytics';

interface HighInventoryAlertProps {
  highInventoryItems: HighInventoryDdipBox[];
}

const HighInventoryAlert = ({
  highInventoryItems,
}: HighInventoryAlertProps) => {
  const {
    getHighInventoryRisk,
    sortedHighInventoryItems: sortedItems,
    overallAlertLevel: overallAlert,
  } = useInventoryAnalytics(undefined, undefined, highInventoryItems);

  return (
    <Card className="h-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <AlertTriangle className="h-5 w-5" />
          재고 과다 알림
        </CardTitle>
        <CardDescription>
          재고가 많이 남은 상품들에 대한 할인 적용을 고려해보세요
        </CardDescription>
      </CardHeader>
      <CardContent className="flex flex-1 flex-col">
        {highInventoryItems.length > 0 ? (
          <div className="space-y-4">
            {/* 전체 알림 상태 */}
            {overallAlert && (
              <Alert
                className={
                  overallAlert.level === 'high' ? 'border-destructive' : ''
                }
              >
                {overallAlert.level === 'high' ? (
                  <AlertTriangle className="h-4 w-4" />
                ) : (
                  <Package className="h-4 w-4" />
                )}
                <AlertDescription>{overallAlert.message}</AlertDescription>
              </Alert>
            )}

            {/* 재고 과다 상품 목록 */}
            <div className="space-y-3">
              {sortedItems.slice(0, 5).map(item => {
                const risk = getHighInventoryRisk(item);

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
                        {risk.percentage.toFixed(2)}%
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
          <div className="text-muted-foreground flex h-full min-h-32 flex-col items-center justify-center">
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
