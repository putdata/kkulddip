import { Skeleton } from '@/components/ui/skeleton';

const OrdersManagementSkeleton = () => {
  return (
    <div className="space-y-6">
      {/* 헤더 스켈래톤 */}
      <div className="flex items-center justify-between">
        <div>
          <Skeleton className="mb-2 h-8 w-32" />
          <Skeleton className="h-4 w-48" />
        </div>
        <div className="flex items-center gap-2">
          <Skeleton className="h-9 w-9" />
          <Skeleton className="h-9 w-32" />
        </div>
      </div>

      {/* 통계 카드 스켈래톤 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {Array.from({ length: 3 }).map((_, index) => (
          <div key={index} className="rounded-lg border p-4">
            <div className="flex items-center justify-between">
              <div>
                <Skeleton className="mb-2 h-8 w-16" />
                <Skeleton className="h-4 w-24" />
              </div>
              <Skeleton className="h-8 w-8 rounded-full" />
            </div>
          </div>
        ))}
      </div>

      {/* 주문 카드 스켈래톤 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 6 }).map((_, index) => (
          <div key={index} className="rounded-lg border p-4">
            <div className="space-y-3">
              {/* 주문 헤더 */}
              <div className="flex items-center justify-between">
                <Skeleton className="h-5 w-20" />
                <Skeleton className="h-6 w-16 rounded-full" />
              </div>

              {/* 고객 정보 */}
              <div>
                <Skeleton className="mb-2 h-4 w-16" />
                <Skeleton className="h-4 w-24" />
              </div>

              {/* 주문 항목들 */}
              <div className="space-y-2">
                {Array.from({ length: 2 }).map((_, itemIndex) => (
                  <div key={itemIndex} className="flex justify-between">
                    <Skeleton className="h-4 w-32" />
                    <Skeleton className="h-4 w-12" />
                  </div>
                ))}
              </div>

              {/* 총 금액 */}
              <div className="border-t pt-2">
                <div className="flex justify-between">
                  <Skeleton className="h-5 w-12" />
                  <Skeleton className="h-5 w-20" />
                </div>
              </div>

              {/* 버튼들 */}
              <div className="flex gap-2">
                <Skeleton className="h-9 flex-1" />
                <Skeleton className="h-9 flex-1" />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default OrdersManagementSkeleton;
