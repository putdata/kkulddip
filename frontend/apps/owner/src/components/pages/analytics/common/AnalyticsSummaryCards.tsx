import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { DollarSign, ShoppingCart, Weight } from 'lucide-react';

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
  const averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
      {/* 총 매출 */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-muted-foreground text-sm font-medium">
            총 매출
          </CardTitle>
          <DollarSign className="text-muted-foreground h-4 w-4" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">
            ₩{totalRevenue.toLocaleString()}
          </div>
          <p className="text-muted-foreground text-xs">분석 기간 전체 매출</p>
        </CardContent>
      </Card>

      {/* 총 주문 수 */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-muted-foreground text-sm font-medium">
            총 주문 수
          </CardTitle>
          <ShoppingCart className="text-muted-foreground h-4 w-4" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">
            {totalOrders.toLocaleString()}건
          </div>
          <p className="text-muted-foreground text-xs">
            평균 주문 금액: ₩{Math.round(averageOrderValue).toLocaleString()}
          </p>
        </CardContent>
      </Card>

      {/* 총 무게 (선택적) */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-muted-foreground text-sm font-medium">
            {totalWeight !== null ? '총 판매 중량' : '분석 지표'}
          </CardTitle>
          <Weight className="text-muted-foreground h-4 w-4" />
        </CardHeader>
        <CardContent>
          {totalWeight !== null ? (
            <>
              <div className="text-2xl font-bold">
                {totalWeight.toFixed(1)}kg
              </div>
              <p className="text-muted-foreground text-xs">
                판매된 총 상품 중량
              </p>
            </>
          ) : (
            <>
              <div className="flex items-center gap-2">
                <Badge variant="secondary" className="text-xs">
                  매출 효율성
                </Badge>
              </div>
              <p className="text-muted-foreground mt-2 text-xs">
                주문당 평균 매출이 높습니다
              </p>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default AnalyticsSummaryCards;
