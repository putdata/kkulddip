import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableHeader, 
  TableRow 
} from '@/components/ui/table';
import { Trophy, TrendingUp } from 'lucide-react';
import type { TopSellingDdipBox } from '@/types/analytics';

interface BestSellerTableProps {
  ddipBoxes: TopSellingDdipBox[];
}

const BestSellerTable = ({ ddipBoxes }: BestSellerTableProps) => {
  // 순위별 뱃지 색상
  const getRankBadge = (index: number) => {
    if (index === 0) {
      return { variant: "default" as const, icon: "🥇" };
    }
    if (index === 1) {
      return { variant: "secondary" as const, icon: "🥈" };
    }
    if (index === 2) {
      return { variant: "outline" as const, icon: "🥉" };
    }
    return { variant: "outline" as const, icon: `${index + 1}위` };
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Trophy className="h-5 w-5" />
          베스트셀러 띱박스
        </CardTitle>
        <CardDescription>
          오늘 가장 많이 팔린 띱박스 순위를 확인해보세요
        </CardDescription>
      </CardHeader>
      <CardContent>
        {ddipBoxes.length > 0 ? (
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-20">순위</TableHead>
                  <TableHead>상품명</TableHead>
                  <TableHead className="text-center">판매량</TableHead>
                  <TableHead className="text-right">매출</TableHead>
                  <TableHead className="text-center">성과</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {ddipBoxes.map((ddipBox, index) => {
                  const rankInfo = getRankBadge(index);
                  const salesPerItem = ddipBox.quantitySold > 0 
                    ? ddipBox.totalSalesAmount / ddipBox.quantitySold 
                    : 0;

                  return (
                    <TableRow key={ddipBox.ddipBoxId}>
                      {/* 순위 */}
                      <TableCell>
                        <Badge variant={rankInfo.variant} className="text-xs">
                          {rankInfo.icon}
                        </Badge>
                      </TableCell>

                      {/* 상품명 */}
                      <TableCell>
                        <div className="flex flex-col">
                          <span className="font-medium">{ddipBox.ddipBoxName}</span>
                          <span className="text-xs text-muted-foreground">
                            ID: {ddipBox.ddipBoxId}
                          </span>
                        </div>
                      </TableCell>

                      {/* 판매량 */}
                      <TableCell className="text-center">
                        <div className="flex flex-col items-center">
                          <span className="font-medium text-blue-600">
                            {ddipBox.quantitySold}개
                          </span>
                          <span className="text-xs text-muted-foreground">
                            개당 ₩{Math.round(salesPerItem).toLocaleString()}
                          </span>
                        </div>
                      </TableCell>

                      {/* 총 매출 */}
                      <TableCell className="text-right">
                        <span className="font-bold text-green-600">
                          ₩{ddipBox.totalSalesAmount.toLocaleString()}
                        </span>
                      </TableCell>

                      {/* 성과 표시 */}
                      <TableCell className="text-center">
                        {index < 3 ? (
                          <Badge variant="secondary" className="flex items-center gap-1">
                            <TrendingUp className="h-3 w-3" />
                            인기
                          </Badge>
                        ) : (
                          <span className="text-muted-foreground text-sm">-</span>
                        )}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </div>
        ) : (
          <div className="flex h-32 items-center justify-center text-muted-foreground">
            베스트셀러 데이터가 없습니다
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default BestSellerTable;