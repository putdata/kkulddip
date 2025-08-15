import { Badge } from '@/components/ui/badge';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import DashboardMetricsCards from '@/components/pages/dashboard/DashboardMetricsCards';
import PendingOrdersSection from '@/components/pages/dashboard/PendingOrdersSection';
import DdipboxStatusSection from '@/components/pages/dashboard/DdipboxStatusSection';
import SalesAnalyticsSection from '@/components/pages/dashboard/SalesAnalyticsSection';
import SettlementSection from '@/components/pages/dashboard/SettlementSection';

const Dashboard = () => {
  const { storeId } = useStoreSelection();

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">대시보드</h1>
          <p className="text-muted-foreground">
            매장 운영 현황을 한눈에 확인하세요
          </p>
        </div>
        <Badge variant="secondary" className="text-sm">
          {new Date().toLocaleDateString('ko-KR')}
        </Badge>
      </div>

      {/* 당일 핵심 지표 카드들 - 독립적 로딩 */}
      <DashboardMetricsCards storeId={storeId} />

      {/* 중간 섹션: 매출 분석 - 전체 폭 */}
      <SalesAnalyticsSection storeId={storeId} />

      {/* 하단 섹션: 3개 컬럼 레이아웃 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        {/* 대기 중인 주문 - 좁은 컬럼 */}
        <div className="h-full lg:col-span-1">
          <PendingOrdersSection storeId={storeId} />
        </div>

        {/* 띱박스 현황 - 넓은 컬럼 */}
        <div className="lg:col-span-2">
          <DdipboxStatusSection storeId={storeId} />
        </div>
      </div>

      {/* 최하단: 정산 요약 - 전체 폭 */}
      <SettlementSection storeId={storeId} />
    </div>
  );
};

export default Dashboard;
