import { useOwnerSettlementData } from '@/hooks/useOwnerSettlementData';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { SettlementTableSkeleton } from '@/components/pages/settlement';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { formatCurrency } from '@/utils/settlementUtils';

const OwnerSettlement = () => {
  const {
    selectedYear,
    selectedMonth,
    setSelectedYear,
    setSelectedMonth,
    settlementSummary,
    isLoading,
  } = useOwnerSettlementData();

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between px-4">
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

      {/* 전체 매장 통합 카드 */}
      <div className="mb-6 grid grid-cols-2 gap-4 lg:grid-cols-4">
        {isLoading
          ? Array.from({ length: 4 }).map((_, index) => (
              <Card key={index} className="animate-pulse">
                <CardHeader className="pb-2">
                  <div className="h-4 w-20 rounded bg-gray-200"></div>
                </CardHeader>
                <CardContent>
                  <div className="h-8 w-24 rounded bg-gray-200"></div>
                </CardContent>
              </Card>
            ))
          : settlementSummary
            ? [
                {
                  title: '총 매출',
                  value: formatCurrency(settlementSummary.totalRevenue),
                  className: 'text-blue-600',
                },
                {
                  title: '총 주문 수',
                  value: `${settlementSummary.totalOrderCount}건`,
                  className: 'text-green-600',
                },
                {
                  title: '평균 주문 금액',
                  value: formatCurrency(settlementSummary.avgOrderAmount),
                  className: 'text-orange-600',
                },
                {
                  title: '운영 매장 수',
                  value: `${settlementSummary.storeCount}개`,
                  className: 'text-purple-600',
                },
              ].map((card, index) => (
                <Card key={index}>
                  <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium text-gray-600">
                      {card.title}
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <p
                      className={`text-lg font-bold lg:text-2xl ${card.className}`}
                    >
                      {card.value}
                    </p>
                  </CardContent>
                </Card>
              ))
            : null}
      </div>

      <Card>
        <CardHeader>
          <CardTitle>전체 매장별 정산 내역</CardTitle>
          <p className="text-muted-foreground text-sm">
            소유한 모든 매장의 정산 데이터
          </p>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <SettlementTableSkeleton />
          ) : settlementSummary?.storeSettlements &&
            settlementSummary.storeSettlements.length > 0 ? (
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="text-center">매장명</TableHead>
                    <TableHead className="text-center">총 매출액</TableHead>
                    <TableHead className="text-center">주문수</TableHead>
                    <TableHead className="text-center">평균 주문금액</TableHead>
                    <TableHead className="text-center">
                      전월 대비 매출
                    </TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {settlementSummary.storeSettlements.map(store => (
                    <TableRow key={store.storeId}>
                      <TableCell className="text-center font-medium">
                        {store.storeName}
                      </TableCell>
                      <TableCell className="text-center">
                        {formatCurrency(store.totalRevenue)}
                      </TableCell>
                      <TableCell className="text-center">
                        {store.orderCount}건
                      </TableCell>
                      <TableCell className="text-center">
                        {formatCurrency(store.avgOrderAmount)}
                      </TableCell>
                      <TableCell className="text-center">
                        {store.revenueGrowthRate !== undefined ? (
                          <span
                            className={
                              store.revenueGrowthRate >= 0
                                ? 'text-green-600'
                                : 'text-red-600'
                            }
                          >
                            {store.revenueGrowthRate >= 0 ? '+' : ''}
                            {store.revenueGrowthRate.toFixed(1)}%
                          </span>
                        ) : (
                          <span className="text-muted-foreground">-</span>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          ) : (
            <div className="text-muted-foreground py-8 text-center">
              정산 데이터가 없습니다.
            </div>
          )}
        </CardContent>
      </Card>

      {settlementSummary && (
        <Card>
          <CardHeader>
            <CardTitle>전체 매장 통합 요약</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
              <div>
                <p className="text-muted-foreground text-sm">총 매출</p>
                <p className="text-xl font-bold">
                  {formatCurrency(settlementSummary.totalRevenue)}
                </p>
              </div>
              <div>
                <p className="text-muted-foreground text-sm">총 주문수</p>
                <p className="text-xl font-bold">
                  {settlementSummary.totalOrderCount}건
                </p>
              </div>
              <div>
                <p className="text-muted-foreground text-sm">평균 주문 금액</p>
                <p className="text-xl font-bold">
                  {formatCurrency(settlementSummary.avgOrderAmount)}
                </p>
              </div>
              <div>
                <p className="text-muted-foreground text-sm">운영 매장 수</p>
                <p className="text-xl font-bold">
                  {settlementSummary.storeCount}개
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
