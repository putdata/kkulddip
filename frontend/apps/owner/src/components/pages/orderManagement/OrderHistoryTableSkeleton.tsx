import { Skeleton } from '@/components/ui/skeleton';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

const OrderHistoryTableSkeleton = () => {
  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-[100px] sm:w-[140px]">주문번호</TableHead>
            <TableHead className="hidden sm:table-cell">고객정보</TableHead>
            <TableHead>주문상품</TableHead>
            <TableHead className="hidden text-center md:table-cell">
              수량
            </TableHead>
            <TableHead className="text-right">금액</TableHead>
            <TableHead className="text-center">상태</TableHead>
            <TableHead className="hidden lg:table-cell">주문일시</TableHead>
            <TableHead className="hidden lg:table-cell">픽업시간</TableHead>
            <TableHead className="text-center">액션</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {Array.from({ length: 8 }).map((_, index) => (
            <TableRow key={index}>
              <TableCell>
                <Skeleton className="h-4 w-16 sm:w-20" />
              </TableCell>
              <TableCell className="hidden sm:table-cell">
                <div className="space-y-1">
                  <Skeleton className="h-4 w-24" />
                  <Skeleton className="h-3 w-16" />
                </div>
              </TableCell>
              <TableCell>
                <div className="space-y-1">
                  <Skeleton className="h-4 w-24 sm:w-32" />
                  <div className="space-y-1 sm:hidden">
                    <Skeleton className="h-3 w-20" />
                    <Skeleton className="h-3 w-16" />
                  </div>
                </div>
              </TableCell>
              <TableCell className="hidden text-center md:table-cell">
                <Skeleton className="mx-auto h-4 w-8" />
              </TableCell>
              <TableCell className="text-right">
                <Skeleton className="ml-auto h-4 w-12 sm:w-16" />
              </TableCell>
              <TableCell className="text-center">
                <Skeleton className="mx-auto h-6 w-14 sm:w-16" />
              </TableCell>
              <TableCell className="hidden lg:table-cell">
                <Skeleton className="h-4 w-24" />
              </TableCell>
              <TableCell className="hidden lg:table-cell">
                <Skeleton className="h-4 w-20" />
              </TableCell>
              <TableCell className="text-center">
                <Skeleton className="mx-auto h-8 w-12 sm:w-16" />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};

export default OrderHistoryTableSkeleton;
