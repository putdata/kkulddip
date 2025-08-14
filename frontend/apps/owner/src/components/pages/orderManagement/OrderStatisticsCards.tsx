import { useMemo, type ReactNode } from 'react';
import { Clock, ShoppingCart, DollarSign, TrendingUp } from 'lucide-react';
import type { Order } from '@/types/order';

interface OrderStatisticsCardsProps {
  orders: Order[];
}

interface StatisticItemProps {
  label: string;
  value: string | number;
  icon: ReactNode;
  trend?: 'up' | 'down' | 'neutral';
}

const StatisticItem = ({
  label,
  value,
  icon,
  trend = 'neutral',
}: StatisticItemProps) => {
  const trendColors = {
    up: 'text-green-600',
    down: 'text-red-600',
    neutral: 'text-gray-600',
  };

  return (
    <div className="flex items-center gap-3">
      <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-gray-100">
        <div className="h-5 w-5 text-gray-600">{icon}</div>
      </div>
      <div className="min-w-0 flex-1">
        <div className="text-sm font-medium text-gray-900">{label}</div>
        <div className={`text-lg font-semibold ${trendColors[trend]}`}>
          {value}
        </div>
      </div>
    </div>
  );
};

const OrderStatisticsCards = ({ orders }: OrderStatisticsCardsProps) => {
  // 통계 데이터 계산
  const statistics = useMemo(() => {
    const totalOrders = orders.length;

    const totalQuantity = orders.reduce(
      (sum, order) =>
        sum +
        order.orderItems.reduce((itemSum, item) => itemSum + item.quantity, 0),
      0,
    );

    const totalAmount = orders.reduce(
      (sum, order) => sum + order.originalPrice,
      0,
    );

    return {
      totalOrders,
      totalQuantity,
      totalAmount: totalAmount.toLocaleString(),
    };
  }, [orders]);

  return (
    <div className="rounded-lg border bg-white p-4">
      <div className="mb-4 flex items-center gap-2">
        <Clock className="h-5 w-5 text-amber-500" />
        <h2 className="text-lg font-semibold text-gray-900">
          대기 중인 주문 현황
        </h2>
        {orders.length > 0 && (
          <span className="ml-auto flex h-6 w-6 items-center justify-center rounded-full bg-amber-100 text-xs font-medium text-amber-800">
            {orders.length}
          </span>
        )}
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <StatisticItem
          label="총 주문 수"
          value={`${statistics.totalOrders}건`}
          icon={<ShoppingCart className="h-full w-full" />}
          trend={statistics.totalOrders > 0 ? 'up' : 'neutral'}
        />

        <StatisticItem
          label="총 상품 수량"
          value={`${statistics.totalQuantity}개`}
          icon={<TrendingUp className="h-full w-full" />}
          trend={statistics.totalQuantity > 0 ? 'up' : 'neutral'}
        />

        <StatisticItem
          label="총 주문 금액"
          value={`${statistics.totalAmount}원`}
          icon={<DollarSign className="h-full w-full" />}
          trend={statistics.totalOrders > 0 ? 'up' : 'neutral'}
        />
      </div>

      {orders.length > 0 && (
        <div className="mt-4 rounded-md bg-amber-50 p-3">
          <div className="flex items-start gap-2">
            <Clock className="mt-0.5 h-4 w-4 flex-shrink-0 text-amber-600" />
            <div>
              <p className="text-sm font-medium text-amber-800">
                처리 대기 중인 주문이 있습니다
              </p>
              <p className="mt-1 text-xs text-amber-700">
                고객이 기다리고 있습니다. 빠른 처리를 위해 아래에서 주문을
                확인하고 승인 또는 거절해 주세요.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default OrderStatisticsCards;
