import { useState, useEffect } from 'react';
import { useStoreIdParam } from '@/hooks/useStoreIdParam';
import { Bell, Clock, Search, RefreshCw } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { usePendingOrders } from '@/queries/order';
import OrderCard from '@/components/OrderCard';
import { toast } from 'sonner';

const OrdersManagement = () => {
  const storeId = useStoreIdParam();
  const [searchTerm, setSearchTerm] = useState('');
  const [autoRefresh, setAutoRefresh] = useState(true);

  const {
    data: ordersData,
    isLoading,
    error,
    refetch,
  } = usePendingOrders(storeId);

  // 자동 새로고침 (30초마다)
  useEffect(() => {
    if (!autoRefresh) {
      return;
    }

    const interval = setInterval(() => {
      refetch();
    }, 30000); // 30초

    return () => clearInterval(interval);
  }, [autoRefresh, refetch]);

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="text-muted-foreground">주문 내역을 불러오는 중...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="text-destructive">
          주문을 불러오는 중 오류가 발생했습니다.
        </div>
      </div>
    );
  }

  const orders = ordersData || [];

  // 검색 필터링
  const filteredOrders = orders.filter(order => {
    const searchLower = searchTerm.toLowerCase();
    return (
      order.orderId.toLowerCase().includes(searchLower) ||
      order.orderItems.some(item =>
        item.productName.toLowerCase().includes(searchLower),
      ) ||
      order.customerId.toString().includes(searchTerm)
    );
  });

  const handleManualRefresh = () => {
    refetch();
    toast.success('주문 목록을 새로고침했습니다.');
  };

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">주문 관리</h1>
          <p className="text-muted-foreground">
            대기 중인 주문을 확인하고 처리하세요
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="icon"
            onClick={handleManualRefresh}
            title="새로고침"
          >
            <RefreshCw className="h-4 w-4" />
          </Button>
          <Button
            variant={autoRefresh ? 'default' : 'outline'}
            size="sm"
            onClick={() => setAutoRefresh(!autoRefresh)}
          >
            <Clock className="mr-2 h-4 w-4" />
            {autoRefresh ? '자동 새로고침 ON' : '자동 새로고침 OFF'}
          </Button>
        </div>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <div className="bg-card rounded-lg border p-4">
          <div className="flex items-center justify-between">
            <div>
              <div className="text-2xl font-bold">{orders.length}</div>
              <div className="text-muted-foreground text-sm">
                대기 중인 주문
              </div>
            </div>
            <Bell className="h-8 w-8 text-yellow-500" />
          </div>
        </div>
        <div className="bg-card rounded-lg border p-4">
          <div className="text-2xl font-bold">
            {orders.reduce(
              (sum, order) =>
                sum +
                order.orderItems.reduce(
                  (itemSum, item) => itemSum + item.quantity,
                  0,
                ),
              0,
            )}
          </div>
          <div className="text-muted-foreground text-sm">총 상품 수량</div>
        </div>
        <div className="bg-card rounded-lg border p-4">
          <div className="text-2xl font-bold">
            {orders
              .reduce((sum, order) => sum + order.originalPrice, 0)
              .toLocaleString()}
            원
          </div>
          <div className="text-muted-foreground text-sm">총 주문 금액</div>
        </div>
      </div>

      {/* 검색 */}
      <div className="flex flex-col gap-4 sm:flex-row">
        <div className="relative flex-1">
          <Search className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
          <Input
            placeholder="주문번호, 상품명, 고객ID로 검색..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {/* 주문 목록 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {filteredOrders.length === 0 ? (
          <div className="col-span-full py-12 text-center">
            <Bell className="text-muted-foreground mx-auto mb-4 h-12 w-12" />
            <div className="text-muted-foreground mb-2">
              {orders.length === 0
                ? '대기 중인 주문이 없습니다.'
                : '검색 결과가 없습니다.'}
            </div>
            <div className="text-muted-foreground text-sm">
              새로운 주문이 들어오면 여기에 표시됩니다.
            </div>
          </div>
        ) : (
          filteredOrders.map(order => (
            <OrderCard key={order.orderId} order={order} storeId={storeId} />
          ))
        )}
      </div>
    </div>
  );
};

export default OrdersManagement;
