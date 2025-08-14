import { useSalesAnalytics } from '@/queries/analytics';
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import AnalyticsSummaryCards from './AnalyticsSummaryCards';
import TopSellingChart from './TopSellingChart';
import DiscountRangeChart from './DiscountRangeChart';
import SalesPredictionChart from './SalesPredictionChart';
import InventoryPredictionChart from './InventoryPredictionChart';
import SalesAnalyticsTabSkeleton from './SalesAnalyticsTabSkeleton';

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

export default SalesAnalyticsTab;
