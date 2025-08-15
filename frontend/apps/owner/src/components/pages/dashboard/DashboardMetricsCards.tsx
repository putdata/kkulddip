import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { DollarSign, ShoppingCart, Calculator } from 'lucide-react';
import { useDailyAnalytics } from '@/queries/analytics';

interface DashboardMetricsCardsProps {
  storeId: number;
}

const DashboardMetricsCards = ({ storeId }: DashboardMetricsCardsProps) => {
  const { data: dailyData, isLoading } = useDailyAnalytics(storeId);
  const salesOverview = dailyData?.salesOverview;

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {[1, 2, 3].map(i => (
          <Card key={i} className="animate-pulse">
            <CardHeader className="pb-2">
              <div className="h-4 rounded bg-gray-200"></div>
            </CardHeader>
            <CardContent>
              <div className="mb-2 h-8 rounded bg-gray-200"></div>
              <div className="h-3 w-3/4 rounded bg-gray-200"></div>
            </CardContent>
          </Card>
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
      <Card className="border-l-4 border-l-green-500">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-muted-foreground text-sm font-medium">
            오늘 총 매출
          </CardTitle>
          <div className="rounded-full bg-green-100 p-2">
            <DollarSign className="h-4 w-4 text-green-600" />
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-green-600">
            ₩{salesOverview?.totalSales?.toLocaleString() || '0'}
          </div>
          <p className="text-muted-foreground text-xs">오늘 발생한 총 매출액</p>
        </CardContent>
      </Card>

      <Card className="border-l-4 border-l-blue-500">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-muted-foreground text-sm font-medium">
            총 주문 수
          </CardTitle>
          <div className="rounded-full bg-blue-100 p-2">
            <ShoppingCart className="h-4 w-4 text-blue-600" />
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-blue-600">
            {salesOverview?.totalOrderCount?.toLocaleString() || '0'}건
          </div>
          <p className="text-muted-foreground text-xs">완료된 주문 건수</p>
        </CardContent>
      </Card>

      <Card className="border-l-4 border-l-purple-500">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-muted-foreground text-sm font-medium">
            평균 주문 금액
          </CardTitle>
          <div className="rounded-full bg-purple-100 p-2">
            <Calculator className="h-4 w-4 text-purple-600" />
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-purple-600">
            ₩{salesOverview?.averageOrderAmount?.toLocaleString() || '0'}
          </div>
          <p className="text-muted-foreground text-xs">주문당 평균 결제 금액</p>
        </CardContent>
      </Card>
    </div>
  );
};

export default DashboardMetricsCards;
