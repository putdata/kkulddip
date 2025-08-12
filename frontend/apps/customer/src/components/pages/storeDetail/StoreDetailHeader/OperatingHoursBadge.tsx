import { Badge } from '@/components/ui/badge';

interface OperatingHoursProps {
  operatingHours: string;
}

export const OperatingHoursBadge = ({
  operatingHours,
}: OperatingHoursProps) => {
  return (
    <div className="w-full">
      <Badge
        variant="outline"
        className="flex w-full items-center justify-around gap-3 bg-gray-50 p-2 px-5 text-base font-bold"
      >
        <div> 오늘 픽업 가능 시간</div>
        <div className="text-amber-500">{operatingHours}</div>
      </Badge>
    </div>
  );
};
