import { Skeleton } from '@/components/ui/skeleton';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';

const SettlementTableSkeleton = () => {
  return (
    <div className="overflow-x-auto">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="text-center">정산 월</TableHead>
            <TableHead className="text-center">총 매출액</TableHead>
            <TableHead className="text-center">수수료</TableHead>
            <TableHead className="text-center">수수료율</TableHead>
            <TableHead className="text-center">정산 금액</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {Array.from({ length: 6 }).map((_, index) => (
            <TableRow key={index}>
              <TableCell className="text-center">
                <Skeleton className="mx-auto h-4 w-16" />
              </TableCell>
              <TableCell className="text-center">
                <Skeleton className="mx-auto h-4 w-24" />
              </TableCell>
              <TableCell className="text-center">
                <Skeleton className="mx-auto h-4 w-20" />
              </TableCell>
              <TableCell className="text-center">
                <Skeleton className="mx-auto h-4 w-12" />
              </TableCell>
              <TableCell className="text-center">
                <Skeleton className="mx-auto h-4 w-24" />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};

export default SettlementTableSkeleton;
