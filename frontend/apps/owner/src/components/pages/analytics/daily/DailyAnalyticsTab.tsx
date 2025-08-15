import { useState } from 'react';
import { useDailyAnalytics } from '@/queries/analytics';
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Calendar } from '@/components/ui/calendar';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { CalendarIcon } from 'lucide-react';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import { cn } from '@/lib/utils';
import DailyOverviewCards from './DailyOverviewCards';
import BestSellerTable from './BestSellerTable';
import InventoryStatusChart from './InventoryStatusChart';
import HighInventoryAlert from './HighInventoryAlert';
import ProfitMarginChart from './ProfitMarginChart';
import DailyAnalyticsTabSkeleton from './DailyAnalyticsTabSkeleton';

interface DailyAnalyticsTabProps {
  storeId: number;
}

const DailyAnalyticsTab = ({ storeId }: DailyAnalyticsTabProps) => {
  const [selectedDate, setSelectedDate] = useState<Date | undefined>(
    new Date(),
  );
  const targetDate = selectedDate
    ? format(selectedDate, 'yyyy-MM-dd')
    : undefined;
  const {
    data: dailyData,
    isLoading,
    error,
  } = useDailyAnalytics(storeId, targetDate);

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
      {/* 날짜 선택 */}
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold">일별 분석</h3>
        <Popover>
          <PopoverTrigger asChild>
            <Button
              variant="outline"
              className={cn(
                'w-[240px] justify-start text-left font-normal',
                !selectedDate && 'text-muted-foreground',
              )}
            >
              <CalendarIcon className="mr-2 h-4 w-4" />
              {selectedDate ? (
                format(selectedDate, 'PPP', { locale: ko })
              ) : (
                <span>날짜를 선택하세요</span>
              )}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="end">
            <Calendar
              mode="single"
              selected={selectedDate}
              onSelect={setSelectedDate}
              disabled={date =>
                date > new Date() || date < new Date('2024-01-01')
              }
              initialFocus
              locale={ko}
            />
          </PopoverContent>
        </Popover>
      </div>

      {/* 일별 개요 카드들 */}
      <DailyOverviewCards salesOverview={dailyData.salesOverview} />

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
        <HighInventoryAlert
          highInventoryItems={dailyData.highInventoryDdipBoxes}
        />

        {/* 수익률 분석 */}
        <ProfitMarginChart profitAnalysis={dailyData.profitMarginAnalysis} />
      </div>
    </div>
  );
};

export default DailyAnalyticsTab;
