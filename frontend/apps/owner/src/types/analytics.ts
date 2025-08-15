// Analytics API 관련 타입 정의

export interface TopSellingItem {
  itemName: string;
  totalQuantity: number;
  percentage: number;
}

export interface TopSellingProduct {
  productName: string;
  totalQuantity: number;
  percentage: number;
}

export interface TopDiscountRange {
  discountRange: string;
  count: number;
  percentage: number;
}

export interface SalesPrediction {
  date: string;
  predictedRevenue: number;
  confidence: number;
}

export interface InventoryPrediction {
  date: string;
  predictedDailyQuantity: number;
  predictedRemainingQuantity: number;
  inventoryRatio: number;
  confidence: number;
}

export interface SalesAnalyticsResponse {
  topSellingItems: TopSellingItem[];
  topSellingProducts: TopSellingProduct[];
  topDiscountRanges: TopDiscountRange[];
  salesPrediction: SalesPrediction[];
  inventoryPrediction: InventoryPrediction[] | null;
  totalRevenue: number;
  totalOrders: number;
  totalWeight: number | null;
}

export interface SalesOverview {
  totalSales: number;
  totalOrderCount: number;
  averageOrderAmount: number;
}

export interface TopSellingDdipBox {
  ddipBoxId: number;
  ddipBoxName: string;
  quantitySold: number;
  totalSalesAmount: number;
}

export interface InventoryStatus {
  totalDailyCount: number;
  totalRemainingCount: number;
  remainingPercentage: number;
}

export interface HighInventoryDdipBox {
  ddipBoxId: number;
  ddipBoxName: string;
  remainingCount: number;
  dailyCount: number;
}

export interface ProfitByMarginRange {
  marginRange: string;
  salesAmount: number;
  productCount: number;
}

export interface ProfitMarginAnalysis {
  totalRevenue: number;
  totalCost: number;
  totalProfit: number;
  profitMarginPercentage: number;
  profitByMarginRanges: ProfitByMarginRange[];
}

export interface DailyAnalyticsResponse {
  analysisDate: string;
  storeId: number;
  salesOverview: SalesOverview;
  topSellingDdipBoxes: TopSellingDdipBox[];
  inventoryStatus: InventoryStatus;
  highInventoryDdipBoxes: HighInventoryDdipBox[];
  profitMarginAnalysis: ProfitMarginAnalysis;
}
