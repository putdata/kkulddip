import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { Package, AlertTriangle, CheckCircle } from 'lucide-react';
import { useDdipboxList } from '@/queries/ddipbox';

interface DdipboxStatusSectionProps {
  storeId: number;
}

const DdipboxStatusSection = ({ storeId }: DdipboxStatusSectionProps) => {
  const { data: ddipboxData, isLoading, error } = useDdipboxList(storeId);

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Package className="h-5 w-5 text-blue-500" />
            띱박스 현황
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[1, 2, 3].map(i => (
              <div key={i} className="animate-pulse">
                <div className="h-16 rounded-lg bg-gray-200"></div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Package className="h-5 w-5 text-blue-500" />
            띱박스 현황
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-center py-8">
            <div className="text-muted-foreground text-center">
              <AlertTriangle className="mx-auto mb-2 h-8 w-8 text-red-500" />
              <p>띱박스 데이터를 불러올 수 없습니다</p>
            </div>
          </div>
        </CardContent>
      </Card>
    );
  }

  const ddipboxes = ddipboxData || [];
  const activeDdipboxes = ddipboxes.filter(box => box.isActive);
  const lowStockDdipboxes = ddipboxes.filter(
    box => box.remainingQuantity <= Math.floor(box.dailyQuantity * 0.2),
  );

  const totalRemaining = ddipboxes.reduce(
    (sum, box) => sum + box.remainingQuantity,
    0,
  );
  const totalDaily = ddipboxes.reduce((sum, box) => sum + box.dailyQuantity, 0);

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Package className="h-5 w-5 text-blue-500" />
          띱박스 현황
          <Badge variant="secondary" className="ml-auto">
            {activeDdipboxes.length}/{ddipboxes.length}개 운영중
          </Badge>
        </CardTitle>
      </CardHeader>
      <CardContent>
        {/* 요약 통계 */}
        <div className="mb-4 grid grid-cols-2 gap-4">
          <div className="rounded-lg bg-blue-50 p-3 text-center">
            <div className="text-lg font-semibold text-blue-600">
              {totalRemaining}개
            </div>
            <div className="text-muted-foreground text-sm">남은 재고</div>
          </div>
          <div className="rounded-lg bg-green-50 p-3 text-center">
            <div className="text-lg font-semibold text-green-600">
              {totalDaily}개
            </div>
            <div className="text-muted-foreground text-sm">일일 등록량</div>
          </div>
        </div>

        {/* 낮은 재고 알림 */}
        {lowStockDdipboxes.length > 0 && (
          <div className="mb-4">
            <div className="mb-2 flex items-center gap-2">
              <AlertTriangle className="h-4 w-4 text-orange-500" />
              <span className="text-sm font-medium">낮은 재고 알림</span>
            </div>
            <div className="space-y-2">
              {lowStockDdipboxes.slice(0, 3).map(ddipbox => (
                <div
                  key={ddipbox.ddipboxId}
                  className="flex items-center justify-between rounded border bg-orange-50 p-2"
                >
                  <div>
                    <div className="text-sm font-medium">
                      {ddipbox.ddipboxName}
                    </div>
                    <div className="text-muted-foreground text-xs">
                      남은 재고: {ddipbox.remainingQuantity}개
                    </div>
                  </div>
                  <Badge variant="outline" className="text-orange-600">
                    부족
                  </Badge>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 최근 활성 띱박스 */}
        {activeDdipboxes.length > 0 ? (
          <div>
            <div className="mb-2 flex items-center gap-2">
              <CheckCircle className="h-4 w-4 text-green-500" />
              <span className="text-sm font-medium">활성 띱박스</span>
            </div>
            <div className="space-y-2">
              {activeDdipboxes.slice(0, 4).map(ddipbox => {
                const stockPercentage =
                  (ddipbox.remainingQuantity / ddipbox.dailyQuantity) * 100;
                const stockStatus =
                  stockPercentage > 50
                    ? 'good'
                    : stockPercentage > 20
                      ? 'medium'
                      : 'low';

                return (
                  <div
                    key={ddipbox.ddipboxId}
                    className="rounded-lg border bg-gray-50 p-3"
                  >
                    <div className="mb-2 flex items-center justify-between">
                      <div className="flex-1">
                        <div className="text-sm font-medium">
                          {ddipbox.ddipboxName}
                        </div>
                        <div className="text-muted-foreground text-xs">
                          ₩{ddipbox.salePrice.toLocaleString()} •{' '}
                          {ddipbox.category}
                        </div>
                      </div>
                      <Badge
                        variant={
                          stockStatus === 'good'
                            ? 'default'
                            : stockStatus === 'medium'
                              ? 'secondary'
                              : 'outline'
                        }
                        className={`text-xs ${
                          stockStatus === 'low' ? 'text-orange-600' : ''
                        }`}
                      >
                        {Math.round(stockPercentage)}%
                      </Badge>
                    </div>
                    <div className="space-y-1">
                      <div className="text-muted-foreground flex justify-between text-xs">
                        <span>재고 현황</span>
                        <span>
                          {ddipbox.remainingQuantity}/{ddipbox.dailyQuantity}개
                        </span>
                      </div>
                      <Progress
                        value={stockPercentage}
                        className="h-2"
                        style={
                          {
                            '--progress-background':
                              stockStatus === 'good'
                                ? '#10b981'
                                : stockStatus === 'medium'
                                  ? '#f59e0b'
                                  : '#ef4444',
                          } as React.CSSProperties
                        }
                      />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        ) : (
          <div className="flex items-center justify-center py-8">
            <div className="text-muted-foreground text-center">
              <Package className="mx-auto mb-2 h-8 w-8" />
              <p>등록된 띱박스가 없습니다</p>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default DdipboxStatusSection;
