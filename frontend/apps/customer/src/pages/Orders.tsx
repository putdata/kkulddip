import OrderCard from '@/components/pages/orders/OrderCard/OrderCard';
import { orderDummyData } from '@/dummies/orderDummy';

const Orders = () => {
  return (
    <div className="px-4 py-4 pb-16 pt-16">
      <div className="space-y-4">
        {orderDummyData.map(order => (
          <OrderCard key={order.orderId} item={order} />
        ))}
      </div>
    </div>
  );
};

export default Orders;
