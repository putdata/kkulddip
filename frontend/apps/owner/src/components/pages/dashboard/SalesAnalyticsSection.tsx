import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { TrendingUp, Crown, AlertTriangle } from 'lucide-react';
import { useSalesAnalytics, useDailyAnalytics } from '@/queries/analytics';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';

interface SalesAnalyticsSectionProps {
  storeId: number;
}

const SalesAnalyticsSection = ({ storeId }: SalesAnalyticsSectionProps) => {
  const { data: salesData, isLoading: salesLoading } =
    useSalesAnalytics(storeId);
  const { data: dailyData, isLoading: dailyLoading } =
    useDailyAnalytics(storeId);

  const isLoading = salesLoading || dailyLoading;

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <Card className="animate-pulse">
          <CardHeader>
            <div className="h-6 w-1/3 rounded bg-gray-200"></div>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {[1, 2, 3].map(i => (
                <div key={i} className="h-16 rounded bg-gray-200"></div>
              ))}
            </div>
          </CardContent>
        </Card>
        <Card className="animate-pulse">
          <CardHeader>
            <div className="h-6 w-1/3 rounded bg-gray-200"></div>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {[1, 2, 3].map(i => (
                <div key={i} className="h-12 rounded bg-gray-200"></div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  const topSellingDdipBoxes = dailyData?.topSellingDdipBoxes || [];
  const highInventoryDdipBoxes = dailyData?.highInventoryDdipBoxes || [];

  // 도넛 차트용 데이터 준비
  const pieChartData = topSellingDdipBoxes.slice(0, 5).map((item, index) => ({
    name: item.ddipBoxName,
    value: item.totalSalesAmount,
    color: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'][index],
  }));

  const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

  return (
    <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
      {/* 베스트셀러 - 2컬럼 */}
      <Card className="lg:col-span-2">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Crown className="h-5 w-5 text-yellow-500" />
            오늘의 베스트셀러 Top 5
          </CardTitle>
        </CardHeader>
        <CardContent className="h-full content-center">
          {topSellingDdipBoxes.length > 0 ? (
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              {/* 도넛 차트 */}
              <div className="h-56">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={pieChartData}
                      cx="50%"
                      cy="50%"
                      innerRadius={40}
                      outerRadius={80}
                      paddingAngle={2}
                      dataKey="value"
                    >
                      {pieChartData.map((_, index) => (
                        <Cell
                          key={`cell-${index}`}
                          fill={COLORS[index % COLORS.length]}
                        />
                      ))}
                    </Pie>
                    <Tooltip
                      formatter={(value: number) => [
                        `₩${value.toLocaleString()}`,
                        '매출',
                      ]}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </div>

              {/* 순위 리스트 */}
              <div className="content-center space-y-2">
                {topSellingDdipBoxes.slice(0, 5).map((ddipBox, index) => (
                  <div
                    key={ddipBox.ddipBoxId}
                    className="flex items-center space-x-2"
                  >
                    <div
                      className="h-3 w-3 rounded-full"
                      style={{ backgroundColor: COLORS[index % COLORS.length] }}
                    />
                    <div className="min-w-0 flex-1">
                      <div className="truncate text-sm font-medium">
                        {ddipBox.ddipBoxName}
                      </div>
                      <div className="text-muted-foreground text-xs">
                        {ddipBox.quantitySold}개 • ₩
                        {ddipBox.totalSalesAmount.toLocaleString()}
                      </div>
                    </div>
                    <Badge
                      variant={index < 3 ? 'default' : 'secondary'}
                      className="text-xs"
                    >
                      {index + 1}위
                    </Badge>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <div className="flex items-center justify-center py-8">
              <div className="text-muted-foreground text-center">
                <Crown className="mx-auto mb-2 h-8 w-8" />
                <p>아직 판매된 띱박스가 없습니다</p>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 3개월 매출 트렌드 - 1컬럼 */}
      <Card className="lg:col-span-1">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <TrendingUp className="h-5 w-5 text-blue-500" />
            3개월 매출 트렌드
          </CardTitle>
        </CardHeader>
        <CardContent>
          {salesData ? (
            <div className="space-y-4">
              <div className="grid grid-cols-1 gap-3">
                <div className="rounded-lg bg-blue-50 p-3 text-center">
                  <div className="text-lg font-semibold text-blue-600">
                    ₩{salesData.totalRevenue?.toLocaleString() || '0'}
                  </div>
                  <div className="text-muted-foreground text-sm">
                    총 매출 (3개월)
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div className="rounded-lg bg-green-50 p-3 text-center">
                    <div className="text-lg font-semibold text-green-600">
                      {salesData.totalOrders?.toLocaleString() || '0'}건
                    </div>
                    <div className="text-muted-foreground text-sm">총 주문</div>
                  </div>
                  <div className="rounded-lg bg-purple-50 p-3 text-center">
                    <div className="text-lg font-semibold text-purple-600">
                      {salesData.totalWeight
                        ? `${salesData.totalWeight}kg`
                        : '0kg'}
                    </div>
                    <div className="text-muted-foreground text-sm">총 중량</div>
                  </div>
                </div>
              </div>

              {/* 높은 재고 알림 */}
              {highInventoryDdipBoxes.length > 0 && (
                <div className="border-t pt-3">
                  <div className="mb-2 flex items-center gap-2">
                    <AlertTriangle className="h-4 w-4 text-orange-500" />
                    <span className="text-sm font-medium">높은 재고 알림</span>
                  </div>
                  <div className="space-y-2">
                    {highInventoryDdipBoxes.slice(0, 2).map(ddipBox => (
                      <div
                        key={ddipBox.ddipBoxId}
                        className="flex items-center justify-between rounded border bg-orange-50 p-2"
                      >
                        <div>
                          <div className="text-sm font-medium">
                            {ddipBox.ddipBoxName}
                          </div>
                          <div className="text-muted-foreground text-xs">
                            재고: {ddipBox.remainingCount}개
                          </div>
                        </div>
                        <Badge
                          variant="outline"
                          className="text-xs text-orange-600"
                        >
                          재고 많음
                        </Badge>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          ) : (
            <div className="flex items-center justify-center py-8">
              <div className="text-muted-foreground text-center">
                <AlertTriangle className="mx-auto mb-2 h-8 w-8 text-red-500" />
                <p>매출 데이터를 불러올 수 없습니다</p>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default SalesAnalyticsSection;
