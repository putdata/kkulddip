import { useDailyAnalytics } from '@/queries/analytics';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import DailyOverviewCards from './DailyOverviewCards';
import BestSellerTable from './BestSellerTable';
import InventoryStatusChart from './InventoryStatusChart';
import HighInventoryAlert from './HighInventoryAlert';
import ProfitMarginChart from './ProfitMarginChart';

interface DailyAnalyticsTabProps {
  storeId: number;
}

const DailyAnalyticsTab = ({ storeId }: DailyAnalyticsTabProps) => {
  const { data: dailyData, isLoading, error } = useDailyAnalytics(storeId);

  if (isLoading) {
    return <DailyAnalyticsTabSkeleton />;
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-destructive">데이터 로드 오류</CardTitle>
          <CardDescription>
            일별 분석 데이터를 불러오는 중 오류가 발생했습니다.
          </CardDescription>
        </CardHeader>
      </Card>
    );
  }

  if (!dailyData) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>데이터 없음</CardTitle>
          <CardDescription>
            현재 분석할 수 있는 일별 데이터가 없습니다.
          </CardDescription>
        </CardHeader>
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      {/* 일별 개요 카드들 */}
      <DailyOverviewCards 
        analysisDate={dailyData.analysisDate}
        salesOverview={dailyData.salesOverview}
      />

      {/* 베스트셀러와 재고 현황 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        {/* 베스트 셀러 테이블 */}
        <div className="lg:col-span-2">
          <BestSellerTable ddipBoxes={dailyData.topSellingDdipBoxes} />
        </div>

        {/* 재고 현황 차트 */}
        <InventoryStatusChart inventoryStatus={dailyData.inventoryStatus} />
      </div>

      {/* 재고 과다 알림과 수익률 분석 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 재고 과다 상품 알림 */}
        <HighInventoryAlert highInventoryItems={dailyData.highInventoryDdipBoxes} />

        {/* 수익률 분석 */}
        <ProfitMarginChart profitAnalysis={dailyData.profitMarginAnalysis} />
      </div>
    </div>
  );
};

// 로딩 스켈레톤 컴포넌트
const DailyAnalyticsTabSkeleton = () => {
  return (
    <div className="space-y-6">
      {/* 개요 카드 스켈레톤 */}
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

      {/* 테이블과 차트 스켈레톤 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader>
            <Skeleton className="h-6 w-40" />
          </CardHeader>
          <CardContent>
            <Skeleton className="h-64 w-full" />
          </CardContent>
        </Card>
        <Card>
          <CardHeader>
            <Skeleton className="h-6 w-32" />
          </CardHeader>
          <CardContent>
            <Skeleton className="h-64 w-full" />
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default DailyAnalyticsTab;