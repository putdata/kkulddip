import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';
import { Badge } from '@/components/ui/badge';
import { Package, AlertCircle, CheckCircle } from 'lucide-react';
import type { InventoryStatus } from '@/types/analytics';
import { useInventoryAnalytics } from '../hooks/useInventoryAnalytics';

interface InventoryStatusChartProps {
  inventoryStatus: InventoryStatus;
}

const InventoryStatusChart = ({
  inventoryStatus,
}: InventoryStatusChartProps) => {
  const { statusChartData: chartData, inventoryStatusInfo: statusInfo } =
    useInventoryAnalytics(undefined, inventoryStatus);

  const soldQuantity =
    inventoryStatus.totalDailyCount - inventoryStatus.totalRemainingCount;

  // 커스텀 툴팁
  const CustomTooltip = ({
    active,
    payload,
  }: {
    active?: boolean;
    payload?: Array<{
      payload: { name: string; value: number; color: string };
    }>;
  }) => {
    if (active && payload && payload.length) {
      const data = payload[0]?.payload;
      if (!data) {
        return null;
      }
      const percentage = (data.value / inventoryStatus.totalDailyCount) * 100;

      return (
        <div className="bg-background rounded-lg border p-3 shadow-md">
          <p className="font-medium">{data.name}</p>
          <p className="text-sm" style={{ color: data.color }}>
            {data.value}개 ({percentage.toFixed(2)}%)
          </p>
        </div>
      );
    }
    return null;
  };

  return (
    <Card className="h-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Package className="h-5 w-5" />
          재고 현황
        </CardTitle>
        <CardDescription>오늘의 전체 재고 상태를 확인해보세요</CardDescription>
      </CardHeader>
      <CardContent>
        {/* 재고 상태 차트 */}
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={chartData}
                cx="50%"
                cy="50%"
                innerRadius={60}
                outerRadius={100}
                paddingAngle={2}
                dataKey="value"
              >
                {chartData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip content={<CustomTooltip />} />
              {/* 중앙 퍼센테이지 표시 */}
              {chartData.length > 0 && (
                <text
                  x="50%"
                  y="50%"
                  textAnchor="middle"
                  dominantBaseline="middle"
                  className="fill-foreground text-lg font-bold"
                >
                  {inventoryStatus.remainingPercentage.toFixed(2)}%
                </text>
              )}
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* 재고 상태 정보 */}
        <div className="mt-4 space-y-3">
          {/* 상태 배지 */}
          {statusInfo && (
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                {statusInfo.status === 'good' ? (
                  <CheckCircle className="h-4 w-4" />
                ) : (
                  <AlertCircle className="h-4 w-4" />
                )}
                <span className="text-sm font-medium">재고 상태</span>
              </div>
              <Badge
                variant={
                  statusInfo.color as
                    | 'default'
                    | 'destructive'
                    | 'outline'
                    | 'secondary'
                }
              >
                {statusInfo.text}
              </Badge>
            </div>
          )}

          {/* 상태 설명 */}
          {statusInfo && (
            <p className="text-muted-foreground text-xs">
              {statusInfo.description}
            </p>
          )}

          {/* 수량 요약 */}
          <div className="grid grid-cols-2 gap-4 border-t pt-2">
            <div className="text-center">
              <div className="text-lg font-bold text-green-600">
                {soldQuantity}개
              </div>
              <div className="text-muted-foreground text-xs">판매완료</div>
            </div>
            <div className="text-center">
              <div className="text-lg font-bold text-blue-600">
                {inventoryStatus.totalRemainingCount}개
              </div>
              <div className="text-muted-foreground text-xs">남은재고</div>
            </div>
          </div>

          {/* 전체 재고 */}
          <div className="border-t pt-2 text-center">
            <div className="text-muted-foreground text-sm">
              일일 총 재고:
              <span className="font-medium">
                {inventoryStatus.totalDailyCount}개
              </span>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default InventoryStatusChart;
