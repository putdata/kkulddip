import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Tooltip,
  Legend,
} from 'recharts';
import { Badge } from '@/components/ui/badge';
import { TrendingUp, Percent } from 'lucide-react';
import type { ProfitMarginAnalysis } from '@/types/analytics';
import { useProfitAnalytics } from '../hooks/useProfitAnalytics';

interface ProfitMarginChartProps {
  profitAnalysis: ProfitMarginAnalysis;
}

const ProfitMarginChart = ({ profitAnalysis }: ProfitMarginChartProps) => {
  const { chartData, profitStatus, calculateSalesPercentage } =
    useProfitAnalytics(profitAnalysis);

  // 커스텀 툴팁
  const CustomTooltip = ({
    active,
    payload,
  }: {
    active?: boolean;
    payload?: Array<{
      payload: {
        name: string;
        value: number;
        productCount: number;
        color: string;
      };
    }>;
  }) => {
    if (active && payload && payload.length) {
      const data = payload[0]?.payload;
      if (!data) {
        return null;
      }
      const percentage = calculateSalesPercentage(data.value);

      return (
        <div className="bg-background rounded-lg border p-3 shadow-md">
          <p className="font-medium">{data.name}</p>
          <p className="text-sm text-blue-600">
            매출: ₩{data.value.toLocaleString()} ({percentage.toFixed(2)}%)
          </p>
          <p className="text-muted-foreground text-sm">
            상품 수: {data.productCount}개
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
          <TrendingUp className="h-5 w-5" />
          수익률 분석
        </CardTitle>
        <CardDescription>
          수익률 구간별 매출 분포를 확인해보세요
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {/* 전체 수익 요약 */}
          <div className="bg-muted/50 grid grid-cols-2 gap-4 rounded-lg p-4">
            <div className="text-center">
              <div className="text-2xl font-bold text-green-600">
                ₩{profitAnalysis.totalProfit.toLocaleString()}
              </div>
              <div className="text-muted-foreground text-xs">총 수익</div>
            </div>
            <div className="text-center">
              <div className="flex items-center justify-center gap-1">
                {profitStatus?.status === 'poor' ? (
                  <TrendingUp className="h-5 w-5" />
                ) : profitStatus?.status === 'fair' ? (
                  <Percent className="h-5 w-5" />
                ) : (
                  <TrendingUp className="h-5 w-5" />
                )}
                <span className="text-2xl font-bold">
                  {profitAnalysis.profitMarginPercentage.toFixed(2)}%
                </span>
              </div>
              <div className="text-muted-foreground text-xs">수익률</div>
            </div>
          </div>

          {/* 수익률 상태 */}
          {profitStatus && (
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium">수익률 평가</span>
              <Badge
                variant={
                  profitStatus.color as
                    | 'default'
                    | 'destructive'
                    | 'outline'
                    | 'secondary'
                }
              >
                {profitStatus.text}
              </Badge>
            </div>
          )}

          {/* 수익률 구간별 차트 */}
          {chartData.length > 0 ? (
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={chartData}
                    cx="50%"
                    cy="50%"
                    innerRadius={50}
                    outerRadius={90}
                    paddingAngle={2}
                    dataKey="value"
                  >
                    {chartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip content={<CustomTooltip />} />
                  <Legend
                    wrapperStyle={{ fontSize: '12px' }}
                    iconType="circle"
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>
          ) : (
            <div className="text-muted-foreground flex min-h-64 flex-1 items-center justify-center">
              수익률 분석 데이터가 없습니다
            </div>
          )}

          {/* 구간별 상세 정보 */}
          {chartData.length > 0 && (
            <div className="space-y-2">
              <h4 className="text-sm font-medium">구간별 매출 현황</h4>
              <div className="space-y-2">
                {chartData.map((item, index) => (
                  <div
                    key={index}
                    className="flex items-center justify-between text-sm"
                  >
                    <div className="flex items-center gap-2">
                      <div
                        className="h-3 w-3 rounded-full"
                        style={{ backgroundColor: item.color }}
                      />
                      <span>{item.name}</span>
                    </div>
                    <div className="text-right">
                      <div className="font-medium">
                        ₩{item.value.toLocaleString()}
                      </div>
                      <div className="text-muted-foreground text-xs">
                        {item.productCount}개 상품
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* 매출 vs 비용 비교 */}
          <div className="border-t pt-2">
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div className="flex items-center justify-between">
                <span className="text-muted-foreground">총 매출</span>
                <span className="font-medium">
                  ₩{profitAnalysis.totalRevenue.toLocaleString()}
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-muted-foreground">총 비용</span>
                <span className="font-medium">
                  ₩{profitAnalysis.totalCost.toLocaleString()}
                </span>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default ProfitMarginChart;
