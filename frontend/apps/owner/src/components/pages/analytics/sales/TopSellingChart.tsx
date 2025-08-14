import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Legend,
  Tooltip,
} from 'recharts';
import type { TopSellingItem, TopSellingProduct } from '@/types/analytics';
import { useSalesAnalytics } from '../hooks/useSalesAnalytics';

interface TopSellingChartProps {
  items: TopSellingItem[];
  products: TopSellingProduct[];
}

const COLORS = [
  '#3b82f6', // blue-500
  '#10b981', // emerald-500
  '#f59e0b', // amber-500
  '#ef4444', // red-500
  '#8b5cf6', // violet-500
  '#06b6d4', // cyan-500
];

const TopSellingChart = ({ items, products }: TopSellingChartProps) => {
  const { itemsChartData: itemsData, productsChartData: productsData } =
    useSalesAnalytics(undefined, items, products);

  // 커스텀 툴팁
  const CustomTooltip = ({
    active,
    payload,
  }: {
    active?: boolean;
    payload?: Array<{
      payload: { name: string; value: number; percentage: number };
    }>;
  }) => {
    if (active && payload && payload.length) {
      const data = payload[0]?.payload;
      if (!data) {
        return null;
      }
      return (
        <div className="bg-background rounded-lg border p-3 shadow-md">
          <p className="font-medium">{data.name}</p>
          <p className="text-sm text-blue-600">
            판매량: {data.value}개 ({data.percentage.toFixed(2)}%)
          </p>
        </div>
      );
    }
    return null;
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle>인기 상품 분석</CardTitle>
        <CardDescription>
          가장 많이 팔린 상품들의 판매량과 비중을 확인해보세요
        </CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col">
        <Tabs defaultValue="items" className="flex w-full flex-1 flex-col">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="items">상품별</TabsTrigger>
            <TabsTrigger value="products">제품별</TabsTrigger>
          </TabsList>

          {/* 상품별 차트 */}
          <TabsContent value="items" className="mt-6 flex-1">
            {itemsData.length > 0 ? (
              <div className="min-h-80 flex-1">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={itemsData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={100}
                      paddingAngle={2}
                      dataKey="value"
                    >
                      {itemsData.map((entry, index) => (
                        <Cell
                          key={`cell-${index}`}
                          fill={COLORS[index % COLORS.length]}
                        />
                      ))}
                    </Pie>
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            ) : (
              <div className="text-muted-foreground flex min-h-80 flex-1 items-center justify-center">
                상품별 판매 데이터가 없습니다
              </div>
            )}
          </TabsContent>

          {/* 제품별 차트 */}
          <TabsContent value="products" className="mt-6 flex-1">
            {productsData.length > 0 ? (
              <div className="min-h-80 flex-1">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={productsData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={100}
                      paddingAngle={2}
                      dataKey="value"
                    >
                      {productsData.map((entry, index) => (
                        <Cell
                          key={`cell-${index}`}
                          fill={COLORS[index % COLORS.length]}
                        />
                      ))}
                    </Pie>
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            ) : (
              <div className="text-muted-foreground flex min-h-80 flex-1 items-center justify-center">
                제품별 판매 데이터가 없습니다
              </div>
            )}
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  );
};

export default TopSellingChart;
