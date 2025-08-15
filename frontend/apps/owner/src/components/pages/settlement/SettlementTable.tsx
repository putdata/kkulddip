import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { formatCurrency, formatPercentage } from '@/utils/settlementUtils';
import EmptySettlement from './EmptySettlement';
import type { MonthlySettlementResponse } from '@/types/settlement';

interface SettlementTableProps {
  monthlyData?: MonthlySettlementResponse;
}

const SettlementTable = ({ monthlyData }: SettlementTableProps) => {
  if (
    !monthlyData ||
    !monthlyData.monthlyData ||
    monthlyData.monthlyData.length === 0
  ) {
    return <EmptySettlement />;
  }

  return (
    <div className="overflow-x-auto">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="text-center">정산 월</TableHead>
            <TableHead className="text-center">총 매출액</TableHead>
            <TableHead className="text-center">주문수</TableHead>
            <TableHead className="text-center">평균 주문금액</TableHead>
            <TableHead className="text-center">전월 대비</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {monthlyData.monthlyData.map(month => (
            <TableRow key={month.period}>
              <TableCell className="text-center font-medium">
                {month.period}
              </TableCell>
              <TableCell className="text-center">
                {formatCurrency(month.totalRevenue)}
              </TableCell>
              <TableCell className="text-center">
                {month.orderCount}건
              </TableCell>
              <TableCell className="text-center">
                {formatCurrency(month.avgOrderAmount)}
              </TableCell>
              <TableCell className="text-center">
                {month.revenueGrowthRate !== undefined ? (
                  formatPercentage(month.revenueGrowthRate)
                ) : (
                  <span className="text-muted-foreground">-</span>
                )}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
};

export default SettlementTable;
