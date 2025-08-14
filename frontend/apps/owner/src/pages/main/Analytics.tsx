import { useState } from 'react';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { CalendarDays } from 'lucide-react';
import SalesAnalyticsTab from '@/components/pages/analytics/sales/SalesAnalyticsTab';
import DailyAnalyticsTab from '@/components/pages/analytics/daily/DailyAnalyticsTab';

const Analytics = () => {
  const { storeId } = useStoreSelection();
  const [activeTab, setActiveTab] = useState('sales');

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">분석</h1>
          <p className="text-muted-foreground">
            매장 운영 데이터를 분석하고 인사이트를 얻어보세요
          </p>
        </div>
        <Badge variant="secondary" className="flex items-center gap-2">
          <CalendarDays className="h-4 w-4" />
          {new Date().toLocaleDateString('ko-KR')}
        </Badge>
      </div>

      {/* 분석 탭 */}
      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="sales">매출 분석</TabsTrigger>
          <TabsTrigger value="daily">일별 분석</TabsTrigger>
        </TabsList>

        <TabsContent value="sales" className="space-y-6">
          <SalesAnalyticsTab storeId={storeId} />
        </TabsContent>

        <TabsContent value="daily" className="space-y-6">
          <DailyAnalyticsTab storeId={storeId} />
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default Analytics;
