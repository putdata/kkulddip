import { Skeleton } from '@/components/ui/skeleton';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

const NotificationTableSkeleton = () => {
  return (
    <div className="w-full overflow-auto">
      <div className="min-w-[320px] rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="text-center">타입</TableHead>
              <TableHead className="text-center">제목</TableHead>
              <TableHead className="hidden text-center sm:table-cell">
                내용
              </TableHead>
              <TableHead className="text-center">발송일</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {Array.from({ length: 5 }).map((_, index) => (
              <TableRow key={index}>
                <TableCell className="text-center">
                  <Skeleton className="mx-auto h-5 w-16" />
                </TableCell>
                <TableCell className="text-center">
                  <Skeleton className="mx-auto h-4 w-32" />
                </TableCell>
                <TableCell className="hidden text-center sm:table-cell">
                  <Skeleton className="mx-auto h-4 w-48" />
                </TableCell>
                <TableCell className="text-center">
                  <Skeleton className="mx-auto h-4 w-24" />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
};

export default NotificationTableSkeleton;
