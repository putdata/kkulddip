import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { DollarSign, ShoppingCart, Calculator } from 'lucide-react';
import type { SalesOverview } from '@/types/analytics';

interface DailyOverviewCardsProps {
  analysisDate: string;
  salesOverview: SalesOverview;
}

const DailyOverviewCards = ({
  analysisDate,
  salesOverview,
}: DailyOverviewCardsProps) => {
  return (
    <>
      {/* 날짜 표시 */}
      <div className="mb-4">
        <Badge variant="outline" className="text-sm">
          {analysisDate} 기준 분석
        </Badge>
      </div>

      {/* 개요 카드들 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {/* 일일 총 매출 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-muted-foreground text-sm font-medium">
              일일 총 매출
            </CardTitle>
            <DollarSign className="text-muted-foreground h-4 w-4" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              ₩{salesOverview.totalSales.toLocaleString()}
            </div>
            <p className="text-muted-foreground text-xs">
              분석일 기준 총 매출액
            </p>
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
              {salesOverview.totalOrderCount.toLocaleString()}건
            </div>
            <p className="text-muted-foreground text-xs">완료된 주문 건수</p>
          </CardContent>
        </Card>

        {/* 평균 주문 금액 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-muted-foreground text-sm font-medium">
              평균 주문 금액
            </CardTitle>
            <Calculator className="text-muted-foreground h-4 w-4" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              ₩{salesOverview.averageOrderAmount.toLocaleString()}
            </div>
            <p className="text-muted-foreground text-xs">
              주문당 평균 결제 금액
            </p>
          </CardContent>
        </Card>
      </div>
    </>
  );
};

export default DailyOverviewCards;
