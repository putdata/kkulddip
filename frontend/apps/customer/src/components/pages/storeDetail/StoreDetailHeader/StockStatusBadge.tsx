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

export const OperatingHours = () => {
  return (
    <div className="w-full">
      <Badge
        variant="outline"
        className="flex w-full items-center justify-around gap-3 bg-gray-50 p-2 px-5 text-base font-bold"
      >
        <div> 오늘 픽업 가능 시간</div>
        <div className="text-amber-500">18:00 ~ 20:00</div>
      </Badge>
    </div>
  );
};
