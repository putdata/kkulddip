// TODO: dashboard 임시 데이터
import type {
  DashboardStats,
  DiscountSalesData,
  HourlySalesData,
  MenuRankingItem,
  RecentOrder,
  InventoryItem,
  WasteReductionEffect,
} from '@/types/dashboard';

export const mockDashboardStats: DashboardStats = {
  totalSales: 1248000, // 오늘 총 매출 (원)
  totalOrders: 89, // 오늘 총 주문 수
  averageOrderAmount: 14022, // 평균 주문 금액 (원)
  wasteReductionRate: 73, // 폐기율 감소 비율 (%)
  co2Saved: 45.6, // CO2 절약량 (kg)
};

export const mockDiscountSalesData: DiscountSalesData[] = [
  { discountRate: 30, salesCount: 28, revenue: 420000 },
  { discountRate: 40, salesCount: 35, revenue: 525000 },
  { discountRate: 50, salesCount: 18, revenue: 180000 },
  { discountRate: 60, salesCount: 8, revenue: 123000 },
];

export const mockHourlySalesData: HourlySalesData[] = [
  { hour: 10, orders: 2, revenue: 28000 },
  { hour: 11, orders: 5, revenue: 71000 },
  { hour: 12, orders: 12, revenue: 168000 },
  { hour: 13, orders: 8, revenue: 112000 },
  { hour: 14, orders: 6, revenue: 84000 },
  { hour: 15, orders: 4, revenue: 56000 },
  { hour: 16, orders: 7, revenue: 98000 },
  { hour: 17, orders: 15, revenue: 210000 },
  { hour: 18, orders: 18, revenue: 252000 },
  { hour: 19, orders: 9, revenue: 126000 },
  { hour: 20, orders: 3, revenue: 42000 },
];

export const mockPopularMenus: MenuRankingItem[] = [
  {
    id: '1',
    name: '치킨 까스 도시락',
    originalPrice: 12000,
    discountedPrice: 7200,
    discountRate: 40,
    category: '도시락',
    salesCount: 23,
    revenue: 165600,
    wasteReductionAmount: 23,
  },
  {
    id: '2',
    name: '연어 초밥 세트',
    originalPrice: 18000,
    discountedPrice: 10800,
    discountRate: 40,
    category: '초밥',
    salesCount: 18,
    revenue: 194400,
    wasteReductionAmount: 18,
  },
  {
    id: '3',
    name: '불고기 김밥',
    originalPrice: 8000,
    discountedPrice: 5600,
    discountRate: 30,
    category: '김밥',
    salesCount: 15,
    revenue: 84000,
    wasteReductionAmount: 15,
  },
  {
    id: '4',
    name: '새우 샐러드',
    originalPrice: 15000,
    discountedPrice: 7500,
    discountRate: 50,
    category: '샐러드',
    salesCount: 12,
    revenue: 90000,
    wasteReductionAmount: 12,
  },
  {
    id: '5',
    name: '치킨 텐더',
    originalPrice: 16000,
    discountedPrice: 9600,
    discountRate: 40,
    category: '튀김',
    salesCount: 11,
    revenue: 105600,
    wasteReductionAmount: 11,
  },
];

export const mockExpiringMenus: InventoryItem[] = [
  {
    id: '1',
    menuName: '크로와상 샌드위치',
    category: '베이커리',
    currentStock: 8,
    expiryTime: new Date(Date.now() + 2 * 60 * 60 * 1000).toISOString(), // 2시간 후
    urgencyLevel: 'high',
    suggestedDiscountRate: 60,
  },
  {
    id: '2',
    menuName: '참치 마요 삼각김밥',
    category: '김밥',
    currentStock: 12,
    expiryTime: new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString(), // 4시간 후
    urgencyLevel: 'medium',
    suggestedDiscountRate: 40,
  },
  {
    id: '3',
    menuName: '딸기 케이크',
    category: '디저트',
    currentStock: 5,
    expiryTime: new Date(Date.now() + 1.5 * 60 * 60 * 1000).toISOString(), // 1.5시간 후
    urgencyLevel: 'high',
    suggestedDiscountRate: 70,
  },
  {
    id: '4',
    menuName: '치킨 랩',
    category: '랩',
    currentStock: 6,
    expiryTime: new Date(Date.now() + 6 * 60 * 60 * 1000).toISOString(), // 6시간 후
    urgencyLevel: 'low',
    suggestedDiscountRate: 30,
  },
];

export const mockRecentOrders: RecentOrder[] = [
  {
    id: 'ORD-001',
    customerName: '김○○',
    items: [
      {
        menuId: '1',
        menuName: '치킨 까스 도시락',
        quantity: 2,
        originalPrice: 12000,
        discountedPrice: 7200,
      },
    ],
    totalAmount: 14400,
    totalDiscount: 9600,
    orderTime: new Date(Date.now() - 10 * 60 * 1000).toISOString(), // 10분 전
    status: 'completed',
  },
  {
    id: 'ORD-002',
    customerName: '이○○',
    items: [
      {
        menuId: '2',
        menuName: '연어 초밥 세트',
        quantity: 1,
        originalPrice: 18000,
        discountedPrice: 10800,
      },
      {
        menuId: '4',
        menuName: '새우 샐러드',
        quantity: 1,
        originalPrice: 15000,
        discountedPrice: 7500,
      },
    ],
    totalAmount: 18300,
    totalDiscount: 14700,
    orderTime: new Date(Date.now() - 25 * 60 * 1000).toISOString(), // 25분 전
    status: 'ready',
  },
  {
    id: 'ORD-003',
    customerName: '박○○',
    items: [
      {
        menuId: '3',
        menuName: '불고기 김밥',
        quantity: 3,
        originalPrice: 8000,
        discountedPrice: 5600,
      },
    ],
    totalAmount: 16800,
    totalDiscount: 7200,
    orderTime: new Date(Date.now() - 35 * 60 * 1000).toISOString(), // 35분 전
    status: 'completed',
  },
  {
    id: 'ORD-004',
    customerName: '최○○',
    items: [
      {
        menuId: '5',
        menuName: '치킨 텐더',
        quantity: 1,
        originalPrice: 16000,
        discountedPrice: 9600,
      },
    ],
    totalAmount: 9600,
    totalDiscount: 6400,
    orderTime: new Date(Date.now() - 50 * 60 * 1000).toISOString(), // 50분 전
    status: 'preparing',
  },
];

export const mockWasteReductionEffect: WasteReductionEffect = {
  totalItemsSaved: 89, // 오늘 폐기에서 구해낸 음식 개수
  co2Reduction: 45.6, // CO2 절약량 (kg)
  equivalentTrees: 2.1, // 나무 그루 수 환산
  waterSaved: 1250, // 절약된 물 (L)
};
