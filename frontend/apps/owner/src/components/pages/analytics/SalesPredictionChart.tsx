import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, ReferenceLine } from 'recharts';
import { Badge } from '@/components/ui/badge';
import { TrendingUp } from 'lucide-react';
import type { SalesPrediction } from '@/types/analytics';

interface SalesPredictionChartProps {
  predictions: SalesPrediction[];
}

const SalesPredictionChart = ({ predictions }: SalesPredictionChartProps) => {
  // 데이터 변환
  const chartData = predictions.map(prediction => ({
    date: new Date(prediction.date).toLocaleDateString('ko-KR', {
      month: 'short',
      day: 'numeric',
    }),
    revenue: prediction.predictedRevenue,
    confidence: prediction.confidence * 100, // 백분율로 변환
    fullDate: prediction.date,
  }));

  // 신뢰도별 색상 결정
  const getConfidenceColor = (confidence: number) => {
    if (confidence >= 80) {
      return '#10b981'; // green-500
    }
    if (confidence >= 60) {
      return '#f59e0b'; // amber-500
    }
    return '#ef4444'; // red-500
  };

  // 평균 예측 매출
  const avgPredictedRevenue = predictions.length > 0 
    ? predictions.reduce((sum, p) => sum + p.predictedRevenue, 0) / predictions.length
    : 0;

  // 커스텀 툴팁
  const CustomTooltip = ({ active, payload, label }: {
    active?: boolean;
    payload?: Array<{ payload: { date: string; revenue: number; confidence: number; fullDate: string } }>;
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
            예상 매출: ₩{data.revenue.toLocaleString()}
          </p>
          <p className="text-sm text-muted-foreground">
            신뢰도: {data.confidence.toFixed(1)}%
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
                <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="date" 
                    fontSize={12}
                  />
                  <YAxis 
                    fontSize={12}
                    tickFormatter={(value) => `₩${(value / 1000).toFixed(0)}K`}
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
                  ₩{Math.round(avgPredictedRevenue).toLocaleString()}
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
                        color: 'white'
                      }}
                      className="text-xs"
                    >
                      {item.date}: {item.confidence.toFixed(0)}%
                    </Badge>
                  ))}
                </div>
              </div>
            </div>
          </>
        ) : (
          <div className="flex h-80 items-center justify-center text-muted-foreground">
            매출 예측 데이터가 없습니다
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default SalesPredictionChart;