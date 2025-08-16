import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { CreditCard, AlertTriangle, Calendar } from 'lucide-react';
import { useStoreSettlement } from '@/queries/settlement';

interface SettlementSectionProps {
  storeId: number;
}

const SettlementSection = ({ storeId }: SettlementSectionProps) => {
  const currentDate = new Date();
  const currentYear = currentDate.getFullYear();
  const currentMonth = currentDate.getMonth() + 1;

  const {
    data: settlementData,
    isLoading,
    error,
  } = useStoreSettlement(storeId, currentYear, currentMonth);

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CreditCard className="h-5 w-5 text-green-500" />
            이번 달 정산 요약
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[1, 2, 3].map(i => (
              <div key={i} className="animate-pulse">
                <div className="h-12 rounded bg-gray-200"></div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CreditCard className="h-5 w-5 text-green-500" />
            이번 달 정산 요약
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-center py-8">
            <div className="text-muted-foreground text-center">
              <AlertTriangle className="mx-auto mb-2 h-8 w-8 text-red-500" />
              <p>정산 데이터를 불러올 수 없습니다</p>
            </div>
          </div>
        </CardContent>
      </Card>
    );
  }

  const settlement = settlementData;

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <CreditCard className="h-5 w-5 text-green-500" />
          이번 달 정산 요약
          <Badge
            variant="secondary"
            className="ml-auto flex items-center gap-1"
          >
            <Calendar className="h-3 w-3" />
            {currentYear}.{currentMonth.toString().padStart(2, '0')}
          </Badge>
        </CardTitle>
      </CardHeader>
      <CardContent>
        {settlement ? (
          <div className="space-y-4">
            {/* 주요 정산 정보 */}
            <div className="grid grid-cols-1 gap-3">
              <div className="rounded-lg bg-green-50 p-4 text-center">
                <div className="text-xl font-bold text-green-600">
                  ₩{settlement.totalRevenue?.toLocaleString() || '0'}
                </div>
                <div className="text-muted-foreground text-sm">총 매출액</div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="rounded-lg bg-blue-50 p-3 text-center">
                  <div className="text-lg font-semibold text-blue-600">
                    {settlement.orderCount?.toLocaleString() || '0'}건
                  </div>
                  <div className="text-muted-foreground text-sm">주문 수</div>
                </div>
                <div className="rounded-lg bg-purple-50 p-3 text-center">
                  <div className="text-lg font-semibold text-purple-600">
                    ₩{settlement.avgOrderAmount?.toLocaleString() || '0'}
                  </div>
                  <div className="text-muted-foreground text-sm">
                    평균 주문액
                  </div>
                </div>
              </div>
            </div>

            {/* 추가 정보 */}
            <div className="border-t pt-3">
              <div className="grid grid-cols-2 gap-3 text-sm">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">전월 대비:</span>
                  <span
                    className={`font-medium ${settlement.revenueGrowthRate >= 0 ? 'text-green-600' : 'text-red-600'}`}
                  >
                    {settlement.revenueGrowthRate >= 0 ? '+' : ''}
                    {settlement.revenueGrowthRate?.toFixed(1) || '0'}%
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">기간:</span>
                  <span className="font-medium">
                    {settlement.period ||
                      `${currentYear}.${currentMonth.toString().padStart(2, '0')}`}
                  </span>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="flex items-center justify-center py-8">
            <div className="text-muted-foreground text-center">
              <CreditCard className="mx-auto mb-2 h-8 w-8" />
              <p>이번 달 정산 데이터가 없습니다</p>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default SettlementSection;
