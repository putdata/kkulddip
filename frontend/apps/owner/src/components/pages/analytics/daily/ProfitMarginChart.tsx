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
import { useProfitAnalytics } from '@/hooks/useProfitAnalytics';

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
          <div className="bg-muted/30 grid grid-cols-2 gap-4 rounded-lg p-4">
            <div className="text-center">
              <div className="text-2xl font-bold text-green-600">
                ₩{profitAnalysis.totalProfit.toLocaleString()}
              </div>
              <div className="text-muted-foreground text-xs font-medium">
                총 수익
              </div>
            </div>
            <div className="text-center">
              <div className="flex items-center justify-center gap-1">
                {profitStatus?.status === 'poor' ? (
                  <TrendingUp className="h-4 w-4 text-red-500" />
                ) : profitStatus?.status === 'fair' ? (
                  <Percent className="h-4 w-4 text-amber-500" />
                ) : (
                  <TrendingUp className="h-4 w-4 text-green-500" />
                )}
                <span className="text-2xl font-bold">
                  {profitAnalysis.profitMarginPercentage.toFixed(2)}%
                </span>
              </div>
              <div className="text-muted-foreground text-xs font-medium">
                수익률
              </div>
            </div>
          </div>

          {/* 수익률 상태 */}
          {profitStatus && (
            <div className="bg-muted/20 flex items-center justify-between rounded-lg p-3">
              <span className="text-sm font-medium">수익률 평가</span>
              <Badge
                variant="outline"
                className={`${profitStatus.status === 'excellent' ? 'border-green-200 bg-green-50 text-green-700' : profitStatus.status === 'good' ? 'border-blue-200 bg-blue-50 text-blue-700' : profitStatus.status === 'fair' ? 'border-amber-200 bg-amber-50 text-amber-700' : 'border-red-200 bg-red-50 text-red-700'}`}
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
            <div className="space-y-3">
              <h4 className="text-sm font-semibold">구간별 매출 현황</h4>
              <div className="bg-muted/20 space-y-3 rounded-lg p-3">
                {chartData.map((item, index) => (
                  <div
                    key={index}
                    className="flex items-center justify-between text-sm"
                  >
                    <div className="flex items-center gap-3">
                      <div
                        className="h-3 w-3 rounded-full"
                        style={{ backgroundColor: item.color }}
                      />
                      <span className="font-medium">{item.name}</span>
                    </div>
                    <div className="text-right">
                      <div className="font-semibold">
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
          <div className="border-muted border-t pt-4">
            <div className="bg-muted/20 grid grid-cols-2 gap-4 rounded-lg p-3">
              <div className="text-center">
                <div className="text-muted-foreground">총 매출</div>
                <div className="font-semibold text-blue-600">
                  ₩{profitAnalysis.totalRevenue.toLocaleString()}
                </div>
              </div>
              <div className="text-center">
                <div className="text-muted-foreground">총 비용</div>
                <div className="font-semibold text-red-600">
                  ₩{profitAnalysis.totalCost.toLocaleString()}
                </div>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default ProfitMarginChart;
