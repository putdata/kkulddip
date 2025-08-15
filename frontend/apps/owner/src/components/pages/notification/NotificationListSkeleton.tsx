import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';

const NotificationListSkeleton = () => {
  return (
    <div className="space-y-3">
      {Array.from({ length: 5 }).map((_, index) => (
        <Card key={index} className="w-full">
          <CardContent className="p-4">
            <div className="flex items-start justify-between gap-4">
              <div className="min-w-0 flex-1">
                <div className="mb-2 flex items-center gap-2">
                  <Skeleton className="h-5 w-16" />
                  <Skeleton className="h-3 w-20" />
                </div>
                <Skeleton className="mb-1 h-4 w-3/4" />
                <div className="space-y-1">
                  <Skeleton className="h-3 w-full" />
                  <Skeleton className="h-3 w-2/3" />
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
};

export default NotificationListSkeleton;
