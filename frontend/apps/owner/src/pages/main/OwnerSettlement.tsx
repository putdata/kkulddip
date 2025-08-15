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
import { formatCurrency } from '@/utils/settlementUtils';

const OwnerSettlement = () => {
  // TODO: 사장용 전체 매장 통합 정산 API 연결 필요
  // 현재는 임시로 첫 번째 매장 데이터 사용
  const defaultStoreId = '1';

  const {
    selectedYear,
    selectedMonth,
    setSelectedYear,
    setSelectedMonth,
    currentSettlement,
    monthlySettlement,
    isCurrentLoading,
    isMonthlyLoading,
  } = useSettlementData(defaultStoreId);

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">사장 정산</h1>
          <p className="text-muted-foreground">
            전체 매장의 통합 정산 내역을 확인하고 관리하세요
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

      {monthlySettlement && (
        <Card>
          <CardHeader>
            <CardTitle>기간 총계</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
              <div>
                <p className="text-muted-foreground text-sm">총 매출</p>
                <p className="text-xl font-bold">
                  {formatCurrency(monthlySettlement.totalRevenue)}
                </p>
              </div>
              <div>
                <p className="text-muted-foreground text-sm">총 주문수</p>
                <p className="text-xl font-bold">
                  {monthlySettlement.totalOrderCount}건
                </p>
              </div>
              <div>
                <p className="text-muted-foreground text-sm">월 평균 매출</p>
                <p className="text-xl font-bold">
                  {formatCurrency(monthlySettlement.averageMonthlyRevenue)}
                </p>
              </div>
              <div>
                <p className="text-muted-foreground text-sm">전체 성장률</p>
                <p className="text-xl font-bold">
                  {formatCurrency(monthlySettlement.overallGrowthRate)}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
};

export default OwnerSettlement;
