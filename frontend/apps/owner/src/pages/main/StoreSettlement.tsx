import { useStoreSelection } from '@/hooks/useStoreSelection';
import { useSettlementData } from '@/hooks/useSettlementData';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  SettlementCards,
  SettlementTable,
  SettlementTableSkeleton,
} from '@/components/pages/settlement';

const StoreSettlement = () => {
  const { storeId } = useStoreSelection();
  const {
    selectedYear,
    selectedMonth,
    setSelectedYear,
    setSelectedMonth,
    currentSettlement,
    monthlySettlement,
    isCurrentLoading,
    isMonthlyLoading,
  } = useSettlementData(storeId);

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">매장 정산</h1>
          <p className="text-muted-foreground">
            매장의 정산 내역을 확인하고 관리하세요
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Select
            value={selectedYear.toString()}
            onValueChange={value => setSelectedYear(Number(value))}
          >
            <SelectTrigger className="w-[100px]">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {[2024, 2025, 2026].map(year => (
                <SelectItem key={year} value={year.toString()}>
                  {year}년
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Select
            value={selectedMonth.toString()}
            onValueChange={value => setSelectedMonth(Number(value))}
          >
            <SelectTrigger className="w-[80px]">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {Array.from({ length: 12 }, (_, i) => i + 1).map(month => (
                <SelectItem key={month} value={month.toString()}>
                  {month}월
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      <SettlementCards
        settlement={currentSettlement}
        isLoading={isCurrentLoading}
      />

      <Card>
        <CardHeader>
          <CardTitle>월별 정산 내역</CardTitle>
          <p className="text-muted-foreground text-sm">
            최근 6개월간 정산 데이터
          </p>
        </CardHeader>
        <CardContent>
          {isMonthlyLoading ? (
            <SettlementTableSkeleton />
          ) : (
            <SettlementTable monthlyData={monthlySettlement} />
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default StoreSettlement;
