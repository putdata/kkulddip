import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { TrendingUp, Crown } from 'lucide-react';
import { useDailyAnalytics } from '@/queries/analytics';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';

interface SalesAnalyticsSectionProps {
  storeId: number;
}

const SalesAnalyticsSection = ({ storeId }: SalesAnalyticsSectionProps) => {
  const { data: dailyData, isLoading } = useDailyAnalytics(storeId);

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
  const inventoryStatus = dailyData?.inventoryStatus;

  // 도넛 차트용 데이터 준비
  const pieChartData = topSellingDdipBoxes.slice(0, 5).map((item, index) => ({
    name: item.ddipBoxName,
    value: item.totalSalesAmount,
    color: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'][index],
  }));

  const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

  // 재고 소진율 색상 결정
  const getInventoryColor = (percentage: number) => {
    if (percentage <= 30) {
      return 'text-green-600'; // 재고 많이 소진됨 (좋음)
    }
    if (percentage <= 60) {
      return 'text-blue-600'; // 중간
    }
    return 'text-orange-600'; // 재고 많이 남음 (주의)
  };

  const getInventoryBgColor = (percentage: number) => {
    if (percentage <= 30) {
      return 'bg-green-50';
    }
    if (percentage <= 60) {
      return 'bg-blue-50';
    }
    return 'bg-orange-50';
  };

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

      {/* 재고 소진 현황 - 1컬럼 */}
      <Card className="lg:col-span-1">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <TrendingUp className="h-5 w-5 text-blue-500" />
            재고 소진 현황
          </CardTitle>
        </CardHeader>
        <CardContent>
          {inventoryStatus && inventoryStatus.totalDailyCount > 0 ? (
            <div className="space-y-4">
              {/* 재고 소진율 표시 */}
              <div
                className={`rounded-lg p-4 text-center ${getInventoryBgColor(inventoryStatus.remainingPercentage)}`}
              >
                <div
                  className={`text-3xl font-bold ${getInventoryColor(inventoryStatus.remainingPercentage)}`}
                >
                  {(100 - inventoryStatus.remainingPercentage).toFixed(1)}%
                </div>
                <div className="text-muted-foreground text-sm">폐기 방지율</div>
                <div className="text-muted-foreground mt-1 text-xs">
                  (재고 {inventoryStatus.remainingPercentage.toFixed(1)}% 남음)
                </div>
              </div>

              {/* 재고 현황 */}
              <div className="grid grid-cols-1 gap-3">
                <div className="rounded-lg border p-3">
                  <div className="text-muted-foreground text-xs">
                    판매된 재고
                  </div>
                  <div className="text-lg font-semibold text-green-600">
                    {(
                      inventoryStatus.totalDailyCount -
                      inventoryStatus.totalRemainingCount
                    ).toLocaleString()}
                    개
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-2">
                  <div className="rounded-lg border p-2">
                    <div className="text-muted-foreground text-xs">
                      전체 재고
                    </div>
                    <div className="text-sm font-medium">
                      {inventoryStatus.totalDailyCount.toLocaleString()}개
                    </div>
                  </div>
                  <div className="rounded-lg border p-2">
                    <div className="text-muted-foreground text-xs">
                      남은 재고
                    </div>
                    <div className="text-sm font-medium">
                      {inventoryStatus.totalRemainingCount.toLocaleString()}개
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div className="flex items-center justify-center py-8">
              <div className="text-muted-foreground text-center">
                <TrendingUp className="mx-auto mb-2 h-8 w-8 text-gray-400" />
                <p>재고 데이터가 없습니다</p>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default SalesAnalyticsSection;
