import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';
import { Badge } from '@/components/ui/badge';
import { Package, AlertCircle, CheckCircle } from 'lucide-react';
import type { InventoryStatus } from '@/types/analytics';

interface InventoryStatusChartProps {
  inventoryStatus: InventoryStatus;
}

const InventoryStatusChart = ({ inventoryStatus }: InventoryStatusChartProps) => {
  // 차트 데이터 준비
  const soldQuantity = inventoryStatus.totalDailyCount - inventoryStatus.totalRemainingCount;
  
  const chartData = [
    {
      name: '판매완료',
      value: soldQuantity,
      color: '#10b981', // green-500
    },
    {
      name: '남은재고',
      value: inventoryStatus.totalRemainingCount,
      color: '#3b82f6', // blue-500
    },
  ];

  // 재고 상태 평가
  const getInventoryStatus = () => {
    const percentage = inventoryStatus.remainingPercentage;
    if (percentage >= 70) {
      return {
        status: 'good',
        color: 'secondary',
        icon: CheckCircle,
        text: '양호',
        description: '재고가 충분합니다'
      };
    } else if (percentage >= 30) {
      return {
        status: 'warning',
        color: 'default',
        icon: AlertCircle,
        text: '주의',
        description: '재고 보충을 고려해보세요'
      };
    } else {
      return {
        status: 'critical',
        color: 'destructive',
        icon: AlertCircle,
        text: '부족',
        description: '재고 보충이 필요합니다'
      };
    }
  };

  const statusInfo = getInventoryStatus();

  // 커스텀 툴팁
  const CustomTooltip = ({ active, payload }: {
    active?: boolean;
    payload?: Array<{ payload: { name: string; value: number; color: string } }>;
  }) => {
    if (active && payload && payload.length) {
      const data = payload[0]?.payload;
      if (!data) {
        return null;
      }
      const percentage = (data.value / inventoryStatus.totalDailyCount * 100).toFixed(1);
      
      return (
        <div className="rounded-lg border bg-background p-3 shadow-md">
          <p className="font-medium">{data.name}</p>
          <p className="text-sm" style={{ color: data.color }}>
            {data.value}개 ({percentage}%)
          </p>
        </div>
      );
    }
    return null;
  };


  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Package className="h-5 w-5" />
          재고 현황
        </CardTitle>
        <CardDescription>
          오늘의 전체 재고 상태를 확인해보세요
        </CardDescription>
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
                  {inventoryStatus.remainingPercentage.toFixed(1)}%
                </text>
              )}
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* 재고 상태 정보 */}
        <div className="space-y-3 mt-4">
          {/* 상태 배지 */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <statusInfo.icon className="h-4 w-4" />
              <span className="text-sm font-medium">재고 상태</span>
            </div>
            <Badge variant={statusInfo.color as "default" | "destructive" | "outline" | "secondary"}>
              {statusInfo.text}
            </Badge>
          </div>

          {/* 상태 설명 */}
          <p className="text-xs text-muted-foreground">
            {statusInfo.description}
          </p>

          {/* 수량 요약 */}
          <div className="grid grid-cols-2 gap-4 pt-2 border-t">
            <div className="text-center">
              <div className="text-lg font-bold text-green-600">
                {soldQuantity}개
              </div>
              <div className="text-xs text-muted-foreground">판매완료</div>
            </div>
            <div className="text-center">
              <div className="text-lg font-bold text-blue-600">
                {inventoryStatus.totalRemainingCount}개
              </div>
              <div className="text-xs text-muted-foreground">남은재고</div>
            </div>
          </div>

          {/* 전체 재고 */}
          <div className="text-center pt-2 border-t">
            <div className="text-sm text-muted-foreground">
              일일 총 재고: <span className="font-medium">{inventoryStatus.totalDailyCount}개</span>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default InventoryStatusChart;