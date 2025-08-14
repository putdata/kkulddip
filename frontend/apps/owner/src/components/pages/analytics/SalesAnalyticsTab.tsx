import { useSalesAnalytics } from '@/queries/analytics';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import AnalyticsSummaryCards from './AnalyticsSummaryCards';
import TopSellingChart from './TopSellingChart';
import DiscountRangeChart from './DiscountRangeChart';
import SalesPredictionChart from './SalesPredictionChart';
import InventoryPredictionChart from './InventoryPredictionChart';

interface SalesAnalyticsTabProps {
  storeId: number;
}

const SalesAnalyticsTab = ({ storeId }: SalesAnalyticsTabProps) => {
  const { data: salesData, isLoading, error } = useSalesAnalytics(storeId);

  if (isLoading) {
    return <SalesAnalyticsTabSkeleton />;
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-destructive">데이터 로드 오류</CardTitle>
          <CardDescription>
            매출 분석 데이터를 불러오는 중 오류가 발생했습니다.
          </CardDescription>
        </CardHeader>
      </Card>
    );
  }

  if (!salesData) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>데이터 없음</CardTitle>
          <CardDescription>
            현재 분석할 수 있는 매출 데이터가 없습니다.
          </CardDescription>
        </CardHeader>
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      {/* 매출 요약 카드들 */}
      <AnalyticsSummaryCards
        totalRevenue={salesData.totalRevenue}
        totalOrders={salesData.totalOrders}
        totalWeight={salesData.totalWeight}
      />

      {/* 차트 그리드 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 인기 상품 차트 */}
        <TopSellingChart
          items={salesData.topSellingItems}
          products={salesData.topSellingProducts}
        />

        {/* 할인율별 판매 현황 */}
        <DiscountRangeChart discountRanges={salesData.topDiscountRanges} />
      </div>

      {/* 예측 차트들 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 매출 예측 */}
        <SalesPredictionChart predictions={salesData.salesPrediction} />

        {/* 재고 예측 */}
        <InventoryPredictionChart predictions={salesData.inventoryPrediction} />
      </div>
    </div>
  );
};

// 로딩 스켈레톤 컴포넌트
const SalesAnalyticsTabSkeleton = () => {
  return (
    <div className="space-y-6">
      {/* 요약 카드 스켈레톤 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {[1, 2, 3].map(i => (
          <Card key={i}>
            <CardHeader className="pb-2">
              <Skeleton className="h-4 w-24" />
            </CardHeader>
            <CardContent>
              <Skeleton className="h-8 w-32" />
            </CardContent>
          </Card>
        ))}
      </div>

      {/* 차트 스켈레톤 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {[1, 2].map(i => (
          <Card key={i}>
            <CardHeader>
              <Skeleton className="h-6 w-40" />
              <Skeleton className="h-4 w-60" />
            </CardHeader>
            <CardContent>
              <Skeleton className="h-80 w-full" />
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default SalesAnalyticsTab;
