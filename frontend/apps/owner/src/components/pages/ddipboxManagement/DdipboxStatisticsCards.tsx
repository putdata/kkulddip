import { useMemo } from 'react';
import { Package, CheckCircle, XCircle, AlertTriangle } from 'lucide-react';
import type { DdipBox } from '@/types/ddipbox';
import StatisticItem from './StatisticItem';

interface DdipboxStatisticsCardsProps {
  ddipboxes: DdipBox[];
}

const DdipboxStatisticsCards = ({ ddipboxes }: DdipboxStatisticsCardsProps) => {
  const statistics = useMemo(() => {
    const totalCount = ddipboxes.length;
    const activeCount = ddipboxes.filter(ddipbox => ddipbox.isActive).length;
    const inactiveCount = ddipboxes.filter(ddipbox => !ddipbox.isActive).length;
    const lowStockCount = ddipboxes.filter(ddipbox => {
      const percentage =
        (ddipbox.remainingQuantity / ddipbox.dailyQuantity) * 100;
      return percentage <= 20 && ddipbox.isActive;
    }).length;

    return {
      totalCount,
      activeCount,
      inactiveCount,
      lowStockCount,
    };
  }, [ddipboxes]);

  return (
    <div className="rounded-lg border bg-white p-4">
      <div className="mb-4 flex items-center gap-2">
        <Package className="h-5 w-5 text-blue-500" />
        <h2 className="text-lg font-semibold text-gray-900">
          띱박스 관리 현황
        </h2>
        {statistics.totalCount > 0 && (
          <span className="ml-auto flex h-6 w-6 items-center justify-center rounded-full bg-blue-100 text-xs font-medium text-blue-800">
            {statistics.totalCount}
          </span>
        )}
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <StatisticItem
          label="전체 띱박스"
          value={`${statistics.totalCount}개`}
          icon={<Package className="h-full w-full" />}
          trend={statistics.totalCount > 0 ? 'up' : 'neutral'}
        />

        <StatisticItem
          label="활성 띱박스"
          value={`${statistics.activeCount}개`}
          icon={<CheckCircle className="h-full w-full" />}
          trend={statistics.activeCount > 0 ? 'up' : 'neutral'}
        />

        <StatisticItem
          label="비활성 띱박스"
          value={`${statistics.inactiveCount}개`}
          icon={<XCircle className="h-full w-full" />}
          trend={statistics.inactiveCount > 0 ? 'down' : 'neutral'}
        />
      </div>

      {statistics.lowStockCount > 0 && (
        <div className="mt-4 rounded-md bg-orange-50 p-3">
          <div className="flex items-start gap-2">
            <AlertTriangle className="mt-0.5 h-4 w-4 flex-shrink-0 text-orange-600" />
            <div>
              <p className="text-sm font-medium text-orange-800">
                재고 부족 띱박스 {statistics.lowStockCount}개
              </p>
              <p className="mt-1 text-xs text-orange-700">
                일부 띱박스의 재고가 부족합니다. 빠른 보충을 위해 아래에서 재고
                관리를 확인해 주세요.
              </p>
            </div>
          </div>
        </div>
      )}

      {statistics.totalCount === 0 && (
        <div className="mt-4 rounded-md bg-gray-50 p-3">
          <div className="flex items-start gap-2">
            <Package className="mt-0.5 h-4 w-4 flex-shrink-0 text-gray-600" />
            <div>
              <p className="text-sm font-medium text-gray-800">
                등록된 띱박스가 없습니다
              </p>
              <p className="mt-1 text-xs text-gray-700">
                첫 번째 띱박스를 등록하여 판매를 시작해보세요.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DdipboxStatisticsCards;
