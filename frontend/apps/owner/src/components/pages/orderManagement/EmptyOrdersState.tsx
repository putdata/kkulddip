import { Bell } from 'lucide-react';

const EmptyOrdersState = () => (
  <div className="col-span-full py-16 text-center">
    <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-gray-100">
      <Bell className="h-10 w-10 text-gray-400" />
    </div>
    <div className="text-muted-foreground mb-2 text-lg font-medium">
      대기 중인 주문이 없습니다
    </div>
    <div className="text-muted-foreground text-sm">
      새로운 주문이 들어오면 여기에 표시됩니다
    </div>
  </div>
);

export default EmptyOrdersState;
