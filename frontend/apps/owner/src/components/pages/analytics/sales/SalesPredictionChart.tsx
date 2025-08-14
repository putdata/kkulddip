import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  ReferenceLine,
} from 'recharts';
import { Badge } from '@/components/ui/badge';
import { TrendingUp } from 'lucide-react';
import type { SalesPrediction } from '@/types/analytics';
import { useSalesAnalytics } from '../hooks/useSalesAnalytics';

interface SalesPredictionChartProps {
  predictions: SalesPrediction[];
}

const SalesPredictionChart = ({ predictions }: SalesPredictionChartProps) => {
  const {
    predictionChartData: chartData,
    getConfidenceColor,
    avgPredictedRevenue,
  } = useSalesAnalytics(predictions);

  // 커스텀 툴팁
  const CustomTooltip = ({
    active,
    payload,
    label,
  }: {
    active?: boolean;
    payload?: Array<{
      payload: {
        date: string;
        revenue: number;
        confidence: number;
      };
    }>;
    label?: string;
  }) => {
    if (active && payload && payload.length) {
      const data = payload[0]?.payload;
      if (!data) {
        return null;
      }
      return (
        <div className="bg-background rounded-lg border p-3 shadow-md">
          <p className="font-medium">{label}</p>
          <p className="text-sm text-blue-600">
            예상 매출: ₩{data.revenue.toLocaleString()}
          </p>
          <p className="text-muted-foreground text-sm">
            신뢰도: {data.confidence.toFixed(2)}%
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
          매출 예측
        </CardTitle>
        <CardDescription>
          AI 기반 매출 예측과 신뢰도를 확인해보세요
        </CardDescription>
      </CardHeader>
      <CardContent>
        {chartData.length > 0 ? (
          <>
            {/* 차트 */}
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart
                  data={chartData}
                  margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" fontSize={12} />
                  <YAxis
                    fontSize={12}
                    tickFormatter={value => `₩${Math.round(value / 1000)}K`}
                  />
                  <Tooltip content={<CustomTooltip />} />

                  {/* 평균선 */}
                  <ReferenceLine
                    y={avgPredictedRevenue}
                    stroke="#94a3b8"
                    strokeDasharray="5 5"
                    label="평균"
                  />

                  <Line
                    type="monotone"
                    dataKey="revenue"
                    stroke="#3b82f6"
                    strokeWidth={2}
                    dot={{ r: 4 }}
                    activeDot={{ r: 6 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>

            {/* 예측 요약 */}
            <div className="mt-4 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">평균 예상 매출</span>
                <span className="text-lg font-bold text-blue-600">
                  ₩{avgPredictedRevenue.toFixed(0)}
                </span>
              </div>

              {/* 신뢰도 표시 */}
              <div className="space-y-2">
                <span className="text-sm font-medium">신뢰도 수준</span>
                <div className="flex flex-wrap gap-2">
                  {chartData.slice(0, 3).map((item, index) => (
                    <Badge
                      key={index}
                      style={{
                        backgroundColor: getConfidenceColor(item.confidence),
                        color: 'white',
                      }}
                      className="text-xs"
                    >
                      {item.date}: {item.confidence.toFixed(2)}%
                    </Badge>
                  ))}
                </div>
              </div>
            </div>
          </>
        ) : (
          <div className="text-muted-foreground flex min-h-80 flex-1 items-center justify-center">
            매출 예측 데이터가 없습니다
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default SalesPredictionChart;
