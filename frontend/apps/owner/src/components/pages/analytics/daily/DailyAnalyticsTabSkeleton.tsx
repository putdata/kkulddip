import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';

const DailyAnalyticsTabSkeleton = () => {
  return (
    <div className="space-y-6">
      {/* 날짜 선택 스켈레톤 */}
      <div className="flex items-center justify-between">
        <Skeleton className="h-7 w-24" />
        <Skeleton className="h-10 w-60" />
      </div>

      {/* 개요 카드 스켈레톤 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {[1, 2, 3].map(i => (
          <Card key={i} className="border-l-4">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-10 w-10 rounded-full" />
            </CardHeader>
            <CardContent>
              <Skeleton className="mb-2 h-8 w-32" />
              <Skeleton className="h-3 w-28" />
            </CardContent>
          </Card>
        ))}
      </div>

      {/* 테이블과 차트 스켈레톤 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader>
            <Skeleton className="mb-2 h-6 w-40" />
            <Skeleton className="h-4 w-64" />
          </CardHeader>
          <CardContent>
            <Skeleton className="h-64 w-full" />
          </CardContent>
        </Card>
        <Card>
          <CardHeader>
            <Skeleton className="mb-2 h-6 w-32" />
            <Skeleton className="h-4 w-48" />
          </CardHeader>
          <CardContent>
            <Skeleton className="h-64 w-full" />
          </CardContent>
        </Card>
      </div>

      {/* 하단 차트 스켈레톤 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {[1, 2].map(i => (
          <Card key={i}>
            <CardHeader>
              <Skeleton className="mb-2 h-6 w-32" />
              <Skeleton className="h-4 w-56" />
            </CardHeader>
            <CardContent>
              <Skeleton className="h-80 w-full" />
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default DailyAnalyticsTabSkeleton;
