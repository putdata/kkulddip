import { useState } from 'react';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import { usePendingOrders } from '@/queries/order';
import type { Order } from '@/types/order';
import {
  AutoRefreshController,
  OrderStatisticsCards,
  OrderCard,
  OrdersManagementSkeleton,
  OrdersManagementError,
  EmptyOrdersState,
  OrderAcceptDialog,
  OrderRejectDialog,
} from '@/components/pages/orderManagement';

const OrdersManagement = () => {
  const { storeId } = useStoreSelection();
  const [autoRefreshEnabled, setAutoRefreshEnabled] = useState(true);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [showAcceptDialog, setShowAcceptDialog] = useState(false);
  const [showRejectDialog, setShowRejectDialog] = useState(false);

  const {
    data: ordersData,
    isLoading,
    error,
    refetch,
  } = usePendingOrders(storeId);

  if (isLoading) {
    return <OrdersManagementSkeleton />;
  }
  if (error) {
    return <OrdersManagementError error={error} onRetry={refetch} />;
  }

  const orders = ordersData || [];

  const handleAcceptClick = (order: Order) => {
    setSelectedOrder(order);
    setShowAcceptDialog(true);
  };

  const handleRejectClick = (order: Order) => {
    setSelectedOrder(order);
    setShowRejectDialog(true);
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
        <AutoRefreshController
          onRefresh={refetch}
          intervalMs={30000}
          isEnabled={autoRefreshEnabled}
          onToggle={setAutoRefreshEnabled}
        />
      </div>

      {/* 통계 카드 */}
      <OrderStatisticsCards orders={orders} />

      {/* 주문 목록 */}
      {orders.length > 0 ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {orders.map(order => (
            <OrderCard
              key={order.orderId}
              order={order}
              onAcceptClick={() => handleAcceptClick(order)}
              onRejectClick={() => handleRejectClick(order)}
            />
          ))}
        </div>
      ) : (
        <EmptyOrdersState />
      )}

      {/* Dialog 중앙 관리 - 애니메이션을 위해 항상 렌더링 */}
      {selectedOrder && (
        <>
          <OrderAcceptDialog
            order={selectedOrder}
            storeId={storeId}
            open={showAcceptDialog}
            onOpenChange={setShowAcceptDialog}
          />
          <OrderRejectDialog
            order={selectedOrder}
            storeId={storeId}
            open={showRejectDialog}
            onOpenChange={setShowRejectDialog}
          />
        </>
      )}
    </div>
  );
};

export default OrdersManagement;
