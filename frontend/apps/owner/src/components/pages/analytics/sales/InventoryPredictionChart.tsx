import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  ComposedChart,
} from 'recharts';
import { Badge } from '@/components/ui/badge';
import { Package, AlertTriangle } from 'lucide-react';
import type { InventoryPrediction } from '@/types/analytics';
import { useInventoryAnalytics } from '@/hooks/useInventoryAnalytics';

interface InventoryPredictionChartProps {
  predictions: InventoryPrediction[] | null;
}

const InventoryPredictionChart = ({
  predictions,
}: InventoryPredictionChartProps) => {
  const {
    predictionChartData: chartData,
    inventoryRiskLevel: riskLevel,
    avgInventoryRatio,
  } = useInventoryAnalytics(predictions);

  // 커스텀 툴팁
  const CustomTooltip = ({
    active,
    payload,
    label,
  }: {
    active?: boolean;
    payload?: Array<{
      payload: {
        dailyQuantity: number;
        remainingQuantity: number;
        inventoryRatio: number;
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
            일일 예상량: {data.dailyQuantity}개
          </p>
          <p className="text-sm text-green-600">
            잔여 예상량: {data.remainingQuantity}개
          </p>
          <p className="text-muted-foreground text-sm">
            재고율: {data.inventoryRatio.toFixed(2)}%
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
          <Package className="h-5 w-5" />
          재고 예측
        </CardTitle>
        <CardDescription>
          AI 기반 재고 수요 예측과 잔여량을 확인해보세요
        </CardDescription>
      </CardHeader>
      <CardContent>
        {predictions && chartData.length > 0 ? (
          <>
            {/* 차트 */}
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <ComposedChart
                  data={chartData}
                  margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" fontSize={12} />
                  <YAxis fontSize={12} />
                  <Tooltip content={<CustomTooltip />} />

                  {/* 일일 예상 판매량 (막대) */}
                  <Line
                    type="monotone"
                    dataKey="dailyQuantity"
                    stroke="#3b82f6"
                    strokeWidth={2}
                    name="일일 예상량"
                    dot={{ r: 3 }}
                  />

                  {/* 잔여 예상량 (라인) */}
                  <Line
                    type="monotone"
                    dataKey="remainingQuantity"
                    stroke="#10b981"
                    strokeWidth={2}
                    name="잔여 예상량"
                    dot={{ r: 3 }}
                  />
                </ComposedChart>
              </ResponsiveContainer>
            </div>

            {/* 재고 위험도와 요약 */}
            <div className="mt-4 space-y-3">
              {/* 재고 위험도 */}
              {riskLevel && (
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <AlertTriangle className="text-muted-foreground h-4 w-4" />
                    <span className="text-sm font-medium">재고 위험도</span>
                  </div>
                  <Badge
                    variant={
                      riskLevel.color as
                        | 'default'
                        | 'destructive'
                        | 'outline'
                        | 'secondary'
                    }
                  >
                    {riskLevel.text}
                  </Badge>
                </div>
              )}

              {/* 평균 재고율 */}
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">평균 재고율</span>
                <span className="text-lg font-bold text-green-600">
                  {avgInventoryRatio.toFixed(2)}%
                </span>
              </div>

              {/* 일별 예측 요약 */}
              <div className="space-y-2">
                <span className="text-sm font-medium">주요 예측</span>
                <div className="flex flex-wrap gap-2">
                  {chartData.slice(0, 3).map((item, index) => (
                    <Badge key={index} variant="outline" className="text-xs">
                      {item.date}: {item.dailyQuantity}개 판매 예상
                    </Badge>
                  ))}
                </div>
              </div>
            </div>
          </>
        ) : (
          <div className="text-muted-foreground flex min-h-80 flex-1 items-center justify-center">
            {predictions === null
              ? '재고 예측 서비스를 사용할 수 없습니다'
              : '재고 예측 데이터가 없습니다'}
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default InventoryPredictionChart;
