// TODO : 임시 더미 페이지
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { formatTime } from '@/utils/dateUtils';
import {
  mockDashboardStats,
  mockDiscountSalesData,
  mockHourlySalesData,
  mockPopularMenus,
  mockExpiringMenus,
  mockRecentOrders,
  mockWasteReductionEffect,
} from '@/data/dashboardMockData';

const Dashboard = () => {
  return (
    <div className="space-y-6 p-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold">대시보드</h1>
        <Badge variant="secondary" className="text-sm">
          오늘 {new Date().toLocaleDateString('ko-KR')}
        </Badge>
      </div>

      {/* 매출 통계 카드들 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-muted-foreground text-sm font-medium">
              오늘 총 매출
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              ₩{mockDashboardStats.totalSales.toLocaleString()}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-muted-foreground text-sm font-medium">
              총 주문 수
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {mockDashboardStats.totalOrders}건
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-muted-foreground text-sm font-medium">
              평균 주문 금액
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              ₩{mockDashboardStats.averageOrderAmount.toLocaleString()}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-muted-foreground text-sm font-medium">
              폐기율 감소
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {mockDashboardStats.wasteReductionRate}%
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 할인율별 판매 현황 */}
        <Card>
          <CardHeader>
            <CardTitle>할인율별 판매 현황</CardTitle>
            <CardDescription>각 할인율에 따른 판매량과 매출</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {mockDiscountSalesData.map(item => (
                <div
                  key={item.discountRate}
                  className="flex items-center justify-between"
                >
                  <div className="flex items-center space-x-3">
                    <Badge variant="outline">{item.discountRate}% 할인</Badge>
                    <span className="text-muted-foreground text-sm">
                      {item.salesCount}개 판매
                    </span>
                  </div>
                  <div className="font-medium">
                    ₩{item.revenue.toLocaleString()}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* 인기 메뉴 순위 */}
        <Card>
          <CardHeader>
            <CardTitle>인기 메뉴 Top 5</CardTitle>
            <CardDescription>오늘 가장 많이 팔린 메뉴</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {mockPopularMenus.slice(0, 5).map((menu, index) => (
                <div key={menu.id} className="flex items-center space-x-3">
                  <div className="bg-primary/10 flex h-8 w-8 items-center justify-center rounded-full text-sm font-medium">
                    {index + 1}
                  </div>
                  <div className="flex-1">
                    <div className="font-medium">{menu.name}</div>
                    <div className="text-muted-foreground text-sm">
                      {menu.salesCount}개 판매 • {menu.discountRate}% 할인
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="font-medium">
                      ₩{menu.revenue.toLocaleString()}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 시간대별 판매 통계와 폐기 임박 메뉴 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle>시간대별 판매 통계</CardTitle>
            <CardDescription>오늘 시간대별 주문량과 매출</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {mockHourlySalesData.map(data => (
                <div key={data.hour} className="flex items-center space-x-4">
                  <div className="text-muted-foreground w-16 text-sm">
                    {data.hour}:00
                  </div>
                  <div className="flex-1">
                    <Progress
                      value={
                        (data.orders /
                          Math.max(...mockHourlySalesData.map(d => d.orders))) *
                        100
                      }
                      className="h-2"
                    />
                  </div>
                  <div className="w-20 text-right text-sm">{data.orders}건</div>
                  <div className="w-24 text-right text-sm font-medium">
                    ₩{(data.revenue / 1000).toFixed(0)}K
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* 폐기 임박 메뉴 */}
        <Card>
          <CardHeader>
            <CardTitle>폐기 임박 메뉴</CardTitle>
            <CardDescription>할인 적용 권장</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {mockExpiringMenus.map(item => (
                <div key={item.id} className="rounded-lg border p-3">
                  <div className="mb-2 flex items-center justify-between">
                    <div className="text-sm font-medium">{item.menuName}</div>
                    <Badge
                      variant={
                        item.urgencyLevel === 'high'
                          ? 'destructive'
                          : item.urgencyLevel === 'medium'
                            ? 'default'
                            : 'secondary'
                      }
                      className="text-xs"
                    >
                      {item.urgencyLevel === 'high'
                        ? '긴급'
                        : item.urgencyLevel === 'medium'
                          ? '주의'
                          : '보통'}
                    </Badge>
                  </div>
                  <div className="text-muted-foreground mb-2 text-xs">
                    재고 {item.currentStock}개 • {item.suggestedDiscountRate}%
                    할인 권장
                  </div>
                  <div className="text-muted-foreground text-xs">
                    {formatTime(item.expiryTime)}까지
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 환경 효과와 최근 주문 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 환경 효과 */}
        <Card>
          <CardHeader>
            <CardTitle className="text-green-700">오늘의 환경 기여도</CardTitle>
            <CardDescription>음식물 폐기 방지로 인한 환경 효과</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm">구해낸 음식</span>
                <span className="font-bold text-green-600">
                  {mockWasteReductionEffect.totalItemsSaved}개
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm">CO₂ 절약량</span>
                <span className="font-bold text-green-600">
                  {mockWasteReductionEffect.co2Reduction}kg
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm">나무 심기 효과</span>
                <span className="font-bold text-green-600">
                  {mockWasteReductionEffect.equivalentTrees}그루
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm">물 절약량</span>
                <span className="font-bold text-green-600">
                  {mockWasteReductionEffect.waterSaved}L
                </span>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 최근 주문 현황 */}
        <Card>
          <CardHeader>
            <CardTitle>최근 주문 현황</CardTitle>
            <CardDescription>최근 4건의 주문</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {mockRecentOrders.slice(0, 4).map(order => (
                <div key={order.id} className="rounded-lg border p-3">
                  <div className="mb-2 flex items-center justify-between">
                    <div className="text-sm font-medium">
                      {order.customerName}
                    </div>
                    <Badge
                      variant={
                        order.status === 'completed'
                          ? 'default'
                          : order.status === 'ready'
                            ? 'secondary'
                            : 'outline'
                      }
                      className="text-xs"
                    >
                      {order.status === 'completed'
                        ? '완료'
                        : order.status === 'ready'
                          ? '준비완료'
                          : order.status === 'preparing'
                            ? '준비중'
                            : '취소'}
                    </Badge>
                  </div>
                  <div className="text-muted-foreground mb-1 text-xs">
                    {order.items
                      .map(item => `${item.menuName} x${item.quantity}`)
                      .join(', ')}
                  </div>
                  <div className="flex items-center justify-between text-xs">
                    <span className="text-muted-foreground">
                      {formatTime(order.orderTime)}
                    </span>
                    <span className="font-medium">
                      ₩{order.totalAmount.toLocaleString()}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default Dashboard;
