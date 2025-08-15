import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { formatCurrency } from '@/utils/settlementUtils';
import type { StoreSettlementResponse } from '@/types/settlement';

interface SettlementCardsProps {
  settlement?: StoreSettlementResponse;
  isLoading?: boolean;
}

const SettlementCards = ({ settlement, isLoading }: SettlementCardsProps) => {
  if (isLoading) {
    return (
      <div className="mb-6 grid grid-cols-2 gap-4 lg:grid-cols-4">
        {Array.from({ length: 4 }).map((_, index) => (
          <Card key={index} className="animate-pulse">
            <CardHeader className="pb-2">
              <div className="h-4 w-20 rounded bg-gray-200"></div>
            </CardHeader>
            <CardContent>
              <div className="h-8 w-24 rounded bg-gray-200"></div>
            </CardContent>
          </Card>
        ))}
      </div>
    );
  }

  if (!settlement) {
    return null;
  }

  const cards = [
    {
      title: '이번 달 매출',
      value: formatCurrency(settlement.totalRevenue),
      className: 'text-blue-600',
    },
    {
      title: '총 주문 수',
      value: `${settlement.orderCount}건`,
      className: 'text-green-600',
    },
    {
      title: '평균 주문 금액',
      value: formatCurrency(settlement.avgOrderAmount),
      className: 'text-orange-600',
    },
    {
      title: '전월 매출',
      value: formatCurrency(settlement.previousMonthRevenue),
      className: 'text-gray-600',
    },
  ];

  return (
    <div className="mb-6 grid grid-cols-2 gap-4 lg:grid-cols-4">
      {cards.map((card, index) => (
        <Card key={index}>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-600">
              {card.title}
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className={`text-lg font-bold lg:text-2xl ${card.className}`}>
              {card.value}
            </p>
          </CardContent>
        </Card>
      ))}
    </div>
  );
};

export default SettlementCards;
