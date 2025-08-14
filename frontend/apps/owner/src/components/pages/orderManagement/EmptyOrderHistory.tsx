import { Package } from 'lucide-react';

const EmptyOrderHistory = () => {
  return (
    <div className="flex min-h-[400px] flex-col items-center justify-center text-center">
      <Package className="text-muted-foreground mb-4 h-12 w-12" />
      <h3 className="mb-2 text-lg font-semibold">주문 내역이 없습니다</h3>
      <p className="text-muted-foreground">아직 처리된 주문이 없습니다.</p>
    </div>
  );
};

export default EmptyOrderHistory;
