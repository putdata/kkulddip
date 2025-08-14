// Settlement API 관련 타입 정의

export interface StoreSettlement {
  storeId: number;
  storeName: string;
  period: string;
  totalRevenue: number;
  orderCount: number;
  avgOrderAmount: number;
  previousMonthRevenue: number;
  revenueGrowthRate: number;
  previousMonthOrderCount: number;
  orderCountGrowthRate: number;
}

export interface SettlementSummaryResponse {
  period: string;
  totalRevenue: number;
  totalOrderCount: number;
  avgOrderAmount: number;
  storeCount: number;
  storeSettlements: StoreSettlement[];
}

export interface StoreSettlementResponse {
  storeId: number;
  storeName: string;
  period: string;
  totalRevenue: number;
  orderCount: number;
  avgOrderAmount: number;
  previousMonthRevenue: number;
  revenueGrowthRate: number;
  previousMonthOrderCount: number;
  orderCountGrowthRate: number;
}

export interface MonthlySettlementData {
  period: string;
  totalRevenue: number;
  orderCount: number;
  avgOrderAmount: number;
  previousMonthRevenue: number;
  previousMonthOrderCount: number;
  revenueGrowthRate: number;
  orderCountGrowthRate: number;
}

export interface MonthlySettlementResponse {
  storeId: number;
  storeName: string;
  monthlyData: MonthlySettlementData[];
  startPeriod: string;
  endPeriod: string;
  totalMonths: number;
  totalRevenue: number;
  totalOrderCount: number;
  averageMonthlyRevenue: number;
  overallGrowthRate: number;
}
