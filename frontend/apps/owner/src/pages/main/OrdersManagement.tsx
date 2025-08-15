import { useState } from 'react';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import {
  usePendingOrders,
  useStoreOrderHistory,
  usePickupOrder,
} from '@/queries/order';
import type { Order } from '@/types/order';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  OrderStatisticsCards,
  OrderCard,
  OrdersManagementSkeleton,
  OrdersManagementError,
  EmptyOrdersState,
  OrderAcceptDialog,
  OrderRejectDialog,
} from '@/components/pages/orderManagement';
import OrderHistoryTable from '@/components/pages/orderManagement/OrderHistoryTable';
import OrderHistoryTableSkeleton from '@/components/pages/orderManagement/OrderHistoryTableSkeleton';

const OrdersManagement = () => {
  const { storeId } = useStoreSelection();
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [showAcceptDialog, setShowAcceptDialog] = useState(false);
  const [showRejectDialog, setShowRejectDialog] = useState(false);
  const [activeTab, setActiveTab] = useState('pending');

  const {
    data: pendingOrdersData,
    isLoading: isPendingLoading,
    error: pendingError,
    refetch: refetchPending,
  } = usePendingOrders(storeId);

  const {
    data: historyOrdersData,
    isLoading: isHistoryLoading,
    error: historyError,
    refetch: refetchHistory,
  } = useStoreOrderHistory(storeId, {
    enabled: activeTab === 'history',
  });

  const pickupMutation = usePickupOrder(storeId);

  const pendingOrders = pendingOrdersData || [];
  const historyOrders = historyOrdersData || [];

  const handleAcceptClick = (order: Order) => {
    setSelectedOrder(order);
    setShowAcceptDialog(true);
  };

  const handleRejectClick = (order: Order) => {
    setSelectedOrder(order);
    setShowRejectDialog(true);
  };

  const handlePickupClick = (order: Order) => {
    pickupMutation.mutate(order.orderId);
  };

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between px-4">
        <div>
          <h1 className="text-2xl font-bold">주문 관리</h1>
          <p className="text-muted-foreground">주문을 확인하고 처리하세요</p>
        </div>
      </div>

      {/* 탭 네비게이션 */}
      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="pending">대기 주문</TabsTrigger>
          <TabsTrigger value="history">주문 내역</TabsTrigger>
        </TabsList>

        {/* 대기 주문 탭 */}
        <TabsContent value="pending" className="space-y-6">
          {isPendingLoading ? (
            <OrdersManagementSkeleton />
          ) : pendingError ? (
            <OrdersManagementError
              error={pendingError}
              onRetry={refetchPending}
            />
          ) : (
            <>
              {/* 통계 카드 */}
              <OrderStatisticsCards orders={pendingOrders} />

              {/* 주문 목록 */}
              {pendingOrders.length > 0 ? (
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {pendingOrders.map(order => (
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
            </>
          )}
        </TabsContent>

        {/* 주문 내역 탭 */}
        <TabsContent value="history" className="space-y-6">
          {isHistoryLoading ? (
            <OrderHistoryTableSkeleton />
          ) : historyError ? (
            <OrdersManagementError
              error={historyError}
              onRetry={refetchHistory}
            />
          ) : (
            <OrderHistoryTable
              orders={historyOrders || []}
              onPickupClick={handlePickupClick}
            />
          )}
        </TabsContent>
      </Tabs>

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
