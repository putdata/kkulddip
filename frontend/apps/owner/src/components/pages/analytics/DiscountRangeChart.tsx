import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { Badge } from '@/components/ui/badge';
import type { TopDiscountRange } from '@/types/analytics';

interface DiscountRangeChartProps {
  discountRanges: TopDiscountRange[];
}

const DiscountRangeChart = ({ discountRanges }: DiscountRangeChartProps) => {
  // 데이터 변환
  const chartData = discountRanges.map(range => ({
    range: range.discountRange,
    count: range.count,
    percentage: range.percentage,
  }));

  // 커스텀 툴팁
  const CustomTooltip = ({ active, payload, label }: {
    active?: boolean;
    payload?: Array<{ payload: { count: number; percentage: number } }>;
    label?: string;
  }) => {
    if (active && payload && payload.length) {
      const data = payload[0]?.payload;
      if (!data) {
        return null;
      }
      return (
        <div className="rounded-lg border bg-background p-3 shadow-md">
          <p className="font-medium">{label}</p>
          <p className="text-sm text-blue-600">
            판매 건수: {data.count}건 ({data.percentage.toFixed(1)}%)
          </p>
        </div>
      );
    }
    return null;
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>할인율별 판매 현황</CardTitle>
        <CardDescription>
          각 할인 구간별 판매 실적을 확인해보세요
        </CardDescription>
      </CardHeader>
      <CardContent>
        {chartData.length > 0 ? (
          <>
            {/* 차트 */}
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="range" 
                    fontSize={12}
                    angle={-45}
                    textAnchor="end"
                    height={80}
                  />
                  <YAxis fontSize={12} />
                  <Tooltip content={<CustomTooltip />} />
                  <Bar 
                    dataKey="count" 
                    fill="#3b82f6" 
                    radius={[4, 4, 0, 0]}
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>

            {/* 상위 할인 구간 요약 */}
            <div className="mt-4 space-y-2">
              <h4 className="text-sm font-medium">주요 할인 구간</h4>
              <div className="flex flex-wrap gap-2">
                {chartData.slice(0, 3).map((item, index) => (
                  <Badge 
                    key={item.range} 
                    variant={index === 0 ? "default" : "secondary"}
                    className="text-xs"
                  >
                    {item.range}: {item.count}건
                  </Badge>
                ))}
              </div>
            </div>
          </>
        ) : (
          <div className="flex h-80 items-center justify-center text-muted-foreground">
            할인별 판매 데이터가 없습니다
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default DiscountRangeChart;