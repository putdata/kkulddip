import { Badge } from '@/components/ui/badge';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import DashboardMetricsCards from '@/components/pages/dashboard/DashboardMetricsCards';
import LowStockAlertSection from '@/components/pages/dashboard/LowStockAlertSection';
import PendingOrdersSection from '@/components/pages/dashboard/PendingOrdersSection';
import PickupReadySection from '@/components/pages/dashboard/PickupReadySection';
import DdipboxStatusSection from '@/components/pages/dashboard/DdipboxStatusSection';
import SalesAnalyticsSection from '@/components/pages/dashboard/SalesAnalyticsSection';

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

      {/* 재고 부족 알림 */}
      <LowStockAlertSection storeId={storeId} />

      {/* 주문 관리 섹션: 대기 + 픽업 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 대기 중인 주문 */}
        <PendingOrdersSection storeId={storeId} />

        {/* 픽업 대기 주문 */}
        <PickupReadySection storeId={storeId} />
      </div>

      {/* 띱박스 현황 섹션: 전체 폭 */}
      <DdipboxStatusSection storeId={storeId} />

      {/* 매출 분석 섹션: 전체 폭 */}
      <SalesAnalyticsSection storeId={storeId} />
    </div>
  );
};

export default Dashboard;
