import OrderCard from '@/components/pages/orders/OrderCard/OrderCard';
import { useOrders } from '@/hooks/useOrders';

const Orders = () => {
  const { data: orders } = useOrders();

  return (
    <div className="px-4 py-4 pb-16 pt-16">
      <div className="space-y-4">
        {orders?.map(order => (
          <OrderCard key={order.orderId} item={order} />
        ))}
      </div>
    </div>
  );
};

export default Orders;
