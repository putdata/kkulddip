export interface DashboardStats {
  totalSales: number;
  totalOrders: number;
  averageOrderAmount: number;
  wasteReductionRate: number;
  co2Saved: number; // kg 단위
}

export interface DiscountSalesData {
  discountRate: number; // 할인율 (0-100)
  salesCount: number; // 판매량
  revenue: number; // 매출
}

export interface HourlySalesData {
  hour: number; // 시간 (0-23)
  orders: number; // 주문 수
  revenue: number; // 매출
}

export interface MenuItem {
  id: string;
  name: string;
  originalPrice: number;
  discountedPrice: number;
  discountRate: number;
  category: string;
  image?: string;
}

export interface MenuRankingItem extends MenuItem {
  salesCount: number;
  revenue: number;
  wasteReductionAmount: number; // 절약된 폐기량 (개)
}

export interface RecentOrder {
  id: string;
  customerName: string;
  items: Array<{
    menuId: string;
    menuName: string;
    quantity: number;
    originalPrice: number;
    discountedPrice: number;
  }>;
  totalAmount: number;
  totalDiscount: number;
  orderTime: string; // ISO date string
  status: 'completed' | 'preparing' | 'ready' | 'cancelled';
}

export interface InventoryItem {
  id: string;
  menuName: string;
  category: string;
  currentStock: number;
  expiryTime: string; // ISO date string
  urgencyLevel: 'high' | 'medium' | 'low'; // 폐기 임박도
  suggestedDiscountRate: number;
}

export interface WasteReductionEffect {
  totalItemsSaved: number; // 폐기에서 구해낸 음식 개수
  co2Reduction: number; // CO2 절약량 (kg)
  equivalentTrees: number; // 나무 몇 그루에 해당하는지
  waterSaved: number; // 절약된 물 (L)
}
