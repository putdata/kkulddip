import { Badge } from '@/components/ui/badge';

export const OutOfStockBadge = () => {
  return (
    <div className="w-full">
      <Badge
        variant="secondary"
        className="h-10 w-full text-lg font-[segoe_ui] font-bold text-gray-600"
      >
        현재 재고 없음
      </Badge>
    </div>
  );
};
