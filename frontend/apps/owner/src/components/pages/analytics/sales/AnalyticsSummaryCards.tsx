import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { DollarSign, ShoppingCart, Weight } from 'lucide-react';
import { useDailyAnalytics } from '@/hooks/useDailyAnalytics';

interface AnalyticsSummaryCardsProps {
  totalRevenue: number;
  totalOrders: number;
  totalWeight: number | null;
}

const AnalyticsSummaryCards = ({
  totalRevenue,
  totalOrders,
  totalWeight,
}: AnalyticsSummaryCardsProps) => {
  const { averageOrderValue } = useDailyAnalytics(totalRevenue, totalOrders);

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
      {/* 총 매출 */}
      <Card className="border-l-4 border-l-green-500">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-muted-foreground text-sm font-medium">
            총 매출
          </CardTitle>
          <div className="bg-green-100 rounded-full p-2">
            <DollarSign className="h-4 w-4 text-green-600" />
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-green-600">
            ₩{totalRevenue.toLocaleString()}
          </div>
          <p className="text-muted-foreground text-xs">분석 기간 전체 매출</p>
        </CardContent>
      </Card>

      {/* 총 주문 수 */}
      <Card className="border-l-4 border-l-blue-500">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-muted-foreground text-sm font-medium">
            총 주문 수
          </CardTitle>
          <div className="bg-blue-100 rounded-full p-2">
            <ShoppingCart className="h-4 w-4 text-blue-600" />
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-blue-600">
            {totalOrders.toLocaleString()}건
          </div>
          <p className="text-muted-foreground text-xs">
            평균 주문 금액: ₩{averageOrderValue.toFixed(0)}
          </p>
        </CardContent>
      </Card>

      {/* 총 무게 (선택적) */}
      <Card className="border-l-4 border-l-amber-500">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-muted-foreground text-sm font-medium">
            {totalWeight !== null ? '총 판매 중량' : '분석 지표'}
          </CardTitle>
          <div className="bg-amber-100 rounded-full p-2">
            <Weight className="h-4 w-4 text-amber-600" />
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-amber-600">
            {totalWeight !== null ? `${totalWeight}kg` : '0kg'}
          </div>
          <p className="text-muted-foreground text-xs">판매된 총 상품 중량</p>
        </CardContent>
      </Card>
    </div>
  );
};

export default AnalyticsSummaryCards;
