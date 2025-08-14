from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime, date, timedelta
import pandas as pd
import numpy as np
from collections import Counter
import logging
from fastapi.middleware.cors import CORSMiddleware
import warnings
warnings.filterwarnings('ignore')

# Prophet 설치 체크 및 대체 로직
try:
    from prophet import Prophet
    PROPHET_AVAILABLE = True
except ImportError:
    PROPHET_AVAILABLE = False
    logging.warning("Prophet not available. Using simple linear regression for prediction.")

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="Analytics API", version="1.0.0")

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Pydantic 모델들 (동일)
class DdipBoxItemDto(BaseModel):
    itemId: int
    ddipboxItemName: str
    originalPrice: int
    itemQuantity: int

class OrderItemDataDto(BaseModel):
    orderItemId: int
    productId: int
    productName: str
    quantity: int
    unitPrice: int
    totalPrice: int
    unitCostPrice: int
    totalCostPrice: int
    ddipBoxItems: List[DdipBoxItemDto]

class OrderDataDto(BaseModel):
    orderId: int
    orderDate: date
    totalAmount: int
    orderItems: List[OrderItemDataDto]

class DailyInventoryDto(BaseModel):
    date: date
    totalDailyQuantity: int
    totalRemainingQuantity: int

class AnalyticsRequestDto(BaseModel):
    storeId: int
    startDate: date
    endDate: date
    orders: List[OrderDataDto]
    dailyInventoryData: Optional[List[DailyInventoryDto]] = None

class TopSellingItemDto(BaseModel):
    itemName: str
    totalQuantity: int
    percentage: float

class TopSellingProductDto(BaseModel):
    productName: str
    totalQuantity: int
    percentage: float

class DiscountRangeDto(BaseModel):
    discountRange: str
    count: int
    percentage: float

class SalesPredictionDto(BaseModel):
    date: date
    predictedRevenue: float
    confidence: float

class InventoryPredictionDto(BaseModel):
    date: date
    predictedDailyQuantity: float
    predictedRemainingQuantity: float
    inventoryRatio: float
    confidence: float

class AnalyticsResponseDto(BaseModel):
    topSellingItems: List[TopSellingItemDto]
    topSellingProducts: List[TopSellingProductDto]
    topDiscountRanges: List[DiscountRangeDto]
    salesPrediction: List[SalesPredictionDto]
    inventoryPrediction: Optional[List[InventoryPredictionDto]]
    totalRevenue: float
    totalOrders: int
    totalWeight: Optional[float]

class AnalyticsProcessor:
    
    def __init__(self):
        self.logger = logging.getLogger(__name__)
    
    def process_analytics(self, request: AnalyticsRequestDto) -> AnalyticsResponseDto:
        """메인 분석 처리 함수"""
        try:
            if not request.orders:
                return self._empty_response()
            
            # 1. 데이터 전처리 (수정됨)
            df_items, df_daily_sales = self._prepare_data_corrected(request.orders)
            
            # 2. 가장 많이 팔린 아이템 전체 (수정됨)
            top_items = self._get_top_selling_items_corrected(df_items)
            
            # 2-2. 가장 많이 팔린 프로덕트 전체
            top_products = self._get_top_selling_products(request.orders)
            
            # 3. 가장 많이 팔린 할인율대 통계 (수정됨)
            discount_ranges = self._get_discount_range_analysis(request.orders)
            
            # 4. Prophet으로 한달 매출 예측 (안전성 추가)
            sales_prediction = self._predict_sales_safe(df_daily_sales)
            
            # 4-2. Prophet으로 재고 예측
            inventory_prediction = self._predict_inventory_safe(request.dailyInventoryData)
            
            # 5. 전체 통계
            total_revenue = float(sum(order.totalAmount for order in request.orders))
            total_orders = len(request.orders)
            total_weight = self._calculate_total_weight(request.orders)
            
            return AnalyticsResponseDto(
                topSellingItems=top_items,
                topSellingProducts=top_products,
                topDiscountRanges=discount_ranges,
                salesPrediction=sales_prediction,
                inventoryPrediction=inventory_prediction,
                totalRevenue=total_revenue,
                totalOrders=total_orders,
                totalWeight=total_weight
            )
            
        except Exception as e:
            self.logger.error(f"Analytics processing error: {str(e)}", exc_info=True)
            return self._empty_response()
    
    def _prepare_data_corrected(self, orders: List[OrderDataDto]):
        """데이터 전처리 - 수정된 로직"""
        
        item_sales = []
        daily_sales = {}
        
        for order in orders:
            order_date = order.orderDate
            
            # 일별 매출 집계
            if order_date not in daily_sales:
                daily_sales[order_date] = 0
            daily_sales[order_date] += order.totalAmount
            
            # 아이템별 판매량 집계 (수정된 로직)
            for order_item in order.orderItems:
                ddip_box_quantity = order_item.quantity  # 띱박스 주문 수량
                
                for ddip_item in order_item.ddipBoxItems:
                    # 실제 아이템 판매량 = 띱박스 주문수량 × 띱박스당 해당 아이템 개수
                    actual_item_quantity = ddip_box_quantity * ddip_item.itemQuantity
                    
                    item_sales.append({
                        'item_name': ddip_item.ddipboxItemName,
                        'quantity_sold': actual_item_quantity,
                        'item_price': ddip_item.originalPrice,
                        'order_date': order_date,
                        'ddip_box_quantity': ddip_box_quantity,
                        'item_per_box': ddip_item.itemQuantity
                    })
        
        # 데이터프레임 생성
        df_items = pd.DataFrame(item_sales) if item_sales else pd.DataFrame()
        
        # 일별 매출 데이터프레임
        if daily_sales:
            df_daily_sales = pd.DataFrame([
                {'ds': date_key, 'y': revenue} 
                for date_key, revenue in daily_sales.items()
            ])
            df_daily_sales['ds'] = pd.to_datetime(df_daily_sales['ds'])
            df_daily_sales = df_daily_sales.sort_values('ds').reset_index(drop=True)
        else:
            df_daily_sales = pd.DataFrame(columns=['ds', 'y'])
        
        return df_items, df_daily_sales
    
    def _get_top_selling_items_corrected(self, df_items: pd.DataFrame) -> List[TopSellingItemDto]:
        """가장 많이 팔린 아이템 전체 - 수정된 로직"""
        
        if df_items.empty:
            return []
        
        try:
            # 아이템별 총 판매량 집계
            item_totals = df_items.groupby('item_name')['quantity_sold'].sum().sort_values(ascending=False)
            total_quantity = item_totals.sum()
            
            if total_quantity == 0:
                return []
            
            # 모든 아이템 반환
            all_items = item_totals
            
            result = []
            for item_name, quantity in all_items.items():
                percentage = (quantity / total_quantity * 100) if total_quantity > 0 else 0
                result.append(TopSellingItemDto(
                    itemName=str(item_name),
                    totalQuantity=int(quantity),
                    percentage=round(float(percentage), 2)
                ))
            
            return result
            
        except Exception as e:
            self.logger.error(f"Top selling items analysis error: {str(e)}")
            return []
    
    def _get_discount_range_analysis(self, orders: List[OrderDataDto]) -> List[DiscountRangeDto]:
        """할인율대별 판매 통계 - 원가와 판매가 비교"""
        
        if not orders:
            return []
        
        try:
            discount_data = []
            
            for order in orders:
                for order_item in order.orderItems:
                    # 할인율 계산: ((원가 - 판매가) / 원가) × 100
                    unit_cost = order_item.unitCostPrice
                    unit_price = order_item.unitPrice
                    
                    if unit_cost > 0:
                        discount_rate = ((unit_cost - unit_price) / unit_cost) * 100
                        discount_data.append({
                            'discount_rate': discount_rate,
                            'quantity': order_item.quantity
                        })
            
            if not discount_data:
                return []
            
            # 할인율대 구분 함수
            def categorize_discount(discount_rate):
                if discount_rate < 0:
                    return "마진 없음 (0% 미만)"
                elif discount_rate < 10:
                    return "낮은 할인 (0-10%)"
                elif discount_rate < 20:
                    return "중간 할인 (10-20%)"
                elif discount_rate < 30:
                    return "높은 할인 (20-30%)"
                else:
                    return "매우 높은 할인 (30% 이상)"
            
            # 할인율대별 판매량 집계
            discount_counts = {}
            total_sales = 0
            
            for item in discount_data:
                discount_range = categorize_discount(item['discount_rate'])
                quantity = item['quantity']
                
                if discount_range not in discount_counts:
                    discount_counts[discount_range] = 0
                discount_counts[discount_range] += quantity
                total_sales += quantity
            
            if total_sales == 0:
                return []
            
            # 할인율대 순서 정의
            discount_order = [
                "마진 없음 (0% 미만)",
                "낮은 할인 (0-10%)", 
                "중간 할인 (10-20%)", 
                "높은 할인 (20-30%)", 
                "매우 높은 할인 (30% 이상)"
            ]
            
            result = []
            for discount_range in discount_order:
                if discount_range in discount_counts and discount_counts[discount_range] > 0:
                    count = int(discount_counts[discount_range])
                    percentage = (count / total_sales * 100) if total_sales > 0 else 0
                    result.append(DiscountRangeDto(
                        discountRange=discount_range,
                        count=count,
                        percentage=round(float(percentage), 2)
                    ))
            
            return result
            
        except Exception as e:
            self.logger.error(f"Discount range analysis error: {str(e)}")
            return []
    
    def _get_top_selling_products(self, orders: List[OrderDataDto]) -> List[TopSellingProductDto]:
        """가장 많이 팔린 프로덕트 전체"""
        
        if not orders:
            return []
        
        try:
            # 프로덕트별 총 판매량 집계
            product_sales = {}
            
            for order in orders:
                for order_item in order.orderItems:
                    product_name = order_item.productName
                    quantity = order_item.quantity
                    
                    if product_name not in product_sales:
                        product_sales[product_name] = 0
                    product_sales[product_name] += quantity
            
            # 정렬 (판매량 기준 내림차순)
            sorted_products = sorted(product_sales.items(), key=lambda x: x[1], reverse=True)
            total_quantity = sum(product_sales.values())
            
            if total_quantity == 0:
                return []
            
            result = []
            for product_name, quantity in sorted_products:
                percentage = (quantity / total_quantity * 100) if total_quantity > 0 else 0
                result.append(TopSellingProductDto(
                    productName=str(product_name),
                    totalQuantity=int(quantity),
                    percentage=round(float(percentage), 2)
                ))
            
            return result
            
        except Exception as e:
            self.logger.error(f"Top selling products analysis error: {str(e)}")
            return []
    
    def _calculate_total_weight(self, orders: List[OrderDataDto]) -> Optional[float]:
        """전체 무게 계산"""
        
        if not orders:
            return 0.0
        
        try:
            total_weight = 0.0
            has_weight_data = False
            
            for order in orders:
                for order_item in order.orderItems:
                    ddip_box_quantity = order_item.quantity
                    
                    for ddip_item in order_item.ddipBoxItems:
                        if ddip_item.weight is not None:
                            has_weight_data = True
                            # 실제 아이템 개수 = 띱박스 주문수량 × 띱박스당 해당 아이템 개수
                            actual_item_quantity = ddip_box_quantity * ddip_item.itemQuantity
                            # 총 무게 = 실제 아이템 개수 × 개별 아이템 무게
                            total_weight += actual_item_quantity * ddip_item.weight
            
            # weight 데이터가 없으면 None 반환
            return total_weight if has_weight_data else None
            
        except Exception as e:
            self.logger.error(f"Weight calculation error: {str(e)}")
            return None
    
    def _predict_inventory_safe(self, inventory_data: Optional[List[DailyInventoryDto]]) -> Optional[List[InventoryPredictionDto]]:
        """안전한 재고 예측 함수"""
        
        if not inventory_data or len(inventory_data) < 2:
            return None
        
        try:
            # 재고 데이터를 DataFrame으로 변환
            df_inventory = pd.DataFrame([
                {
                    'ds': item.date,
                    'daily_quantity': item.totalDailyQuantity,
                    'remaining_quantity': item.totalRemainingQuantity
                } for item in inventory_data
            ])
            df_inventory['ds'] = pd.to_datetime(df_inventory['ds'])
            df_inventory = df_inventory.sort_values('ds').reset_index(drop=True)
            
            if PROPHET_AVAILABLE:
                return self._predict_inventory_with_prophet(df_inventory)
            else:
                return self._predict_inventory_simple(df_inventory)
                
        except Exception as e:
            self.logger.error(f"Inventory prediction error: {str(e)}")
            return None
    
    def _predict_inventory_with_prophet(self, df_inventory: pd.DataFrame) -> List[InventoryPredictionDto]:
        """Prophet을 이용한 재고 예측"""
        
        try:
            results = []
            
            # 1. 일일 총량 예측
            df_daily = df_inventory[['ds', 'daily_quantity']].rename(columns={'daily_quantity': 'y'})
            model_daily = Prophet(
                daily_seasonality=True,
                weekly_seasonality=True,
                yearly_seasonality=False,
                uncertainty_samples=100
            )
            model_daily.fit(df_daily)
            
            # 2. 잔여량 예측  
            df_remaining = df_inventory[['ds', 'remaining_quantity']].rename(columns={'remaining_quantity': 'y'})
            model_remaining = Prophet(
                daily_seasonality=True,
                weekly_seasonality=True,
                yearly_seasonality=False,
                uncertainty_samples=100
            )
            model_remaining.fit(df_remaining)
            
            # 30일 미래 예측
            future = model_daily.make_future_dataframe(periods=30)
            forecast_daily = model_daily.predict(future)
            forecast_remaining = model_remaining.predict(future)
            
            # 미래 예측 데이터만 추출
            future_daily = forecast_daily.tail(30)
            future_remaining = forecast_remaining.tail(30)
            
            for i in range(30):
                daily_pred = future_daily.iloc[i]
                remaining_pred = future_remaining.iloc[i]
                
                predicted_daily = max(0, float(daily_pred['yhat']))
                predicted_remaining = max(0, float(remaining_pred['yhat']))
                
                # 재고 비율 계산 (잔여량/총량)
                inventory_ratio = (predicted_remaining / predicted_daily * 100) if predicted_daily > 0 else 0
                
                # 신뢰도 계산
                daily_uncertainty = abs(daily_pred['yhat_upper'] - daily_pred['yhat_lower'])
                remaining_uncertainty = abs(remaining_pred['yhat_upper'] - remaining_pred['yhat_lower'])
                avg_confidence = max(0, min(100, 100 * (1 - (daily_uncertainty + remaining_uncertainty) / 2 / max(predicted_daily + predicted_remaining, 1))))
                
                results.append(InventoryPredictionDto(
                    date=daily_pred['ds'].date(),
                    predictedDailyQuantity=round(predicted_daily, 2),
                    predictedRemainingQuantity=round(predicted_remaining, 2),
                    inventoryRatio=round(inventory_ratio, 2),
                    confidence=round(float(avg_confidence), 2)
                ))
            
            return results
            
        except Exception as e:
            self.logger.error(f"Prophet inventory prediction error: {str(e)}")
            return self._predict_inventory_simple(df_inventory)
    
    def _predict_inventory_simple(self, df_inventory: pd.DataFrame) -> List[InventoryPredictionDto]:
        """간단한 재고 예측 (Prophet 대체)"""
        
        try:
            # 최근 7일 평균 계산
            recent_daily = df_inventory.tail(7)['daily_quantity'].mean()
            recent_remaining = df_inventory.tail(7)['remaining_quantity'].mean()
            
            # 간단한 트렌드 계산
            if len(df_inventory) >= 7:
                early_daily = df_inventory.head(7)['daily_quantity'].mean()
                late_daily = df_inventory.tail(7)['daily_quantity'].mean()
                daily_trend = (late_daily - early_daily) / max(len(df_inventory), 1)
                
                early_remaining = df_inventory.head(7)['remaining_quantity'].mean()
                late_remaining = df_inventory.tail(7)['remaining_quantity'].mean()
                remaining_trend = (late_remaining - early_remaining) / max(len(df_inventory), 1)
            else:
                daily_trend = 0
                remaining_trend = 0
            
            # 30일 예측
            results = []
            last_date = df_inventory['ds'].max().date()
            
            for i in range(1, 31):
                future_date = last_date + timedelta(days=i)
                
                predicted_daily = max(0, recent_daily + (daily_trend * i))
                predicted_remaining = max(0, recent_remaining + (remaining_trend * i))
                
                # 재고 비율
                inventory_ratio = (predicted_remaining / predicted_daily * 100) if predicted_daily > 0 else 0
                
                # 단순 신뢰도
                confidence = min(85, 40 + (len(df_inventory) * 2))
                
                results.append(InventoryPredictionDto(
                    date=future_date,
                    predictedDailyQuantity=round(predicted_daily, 2),
                    predictedRemainingQuantity=round(predicted_remaining, 2),
                    inventoryRatio=round(inventory_ratio, 2),
                    confidence=round(float(confidence), 2)
                ))
            
            return results
            
        except Exception as e:
            self.logger.error(f"Simple inventory prediction error: {str(e)}")
            return None
    
    def _predict_sales_safe(self, df_daily_sales: pd.DataFrame) -> List[SalesPredictionDto]:
        """안전한 매출 예측 함수"""
        
        if df_daily_sales.empty or len(df_daily_sales) < 2:
            return self._simple_prediction_fallback()
        
        if PROPHET_AVAILABLE:
            return self._predict_with_prophet(df_daily_sales)
        else:
            return self._predict_with_simple_method(df_daily_sales)
    
    def _predict_with_prophet(self, df_daily_sales: pd.DataFrame) -> List[SalesPredictionDto]:
        """Prophet을 이용한 매출 예측"""
        
        try:
            # Prophet 모델 생성
            model = Prophet(
                daily_seasonality=True,
                weekly_seasonality=True,
                yearly_seasonality=False,
                uncertainty_samples=100,
                changepoint_prior_scale=0.05
            )
            
            # 모델 학습
            model.fit(df_daily_sales)
            
            # 30일 미래 예측
            future = model.make_future_dataframe(periods=30)
            forecast = model.predict(future)
            
            # 미래 예측 데이터만 추출
            future_forecast = forecast.tail(30)
            
            result = []
            for _, row in future_forecast.iterrows():
                # 신뢰도 계산 (불확실성 구간 기반)
                uncertainty = abs(row['yhat_upper'] - row['yhat_lower'])
                confidence = max(0, min(100, 100 * (1 - uncertainty / max(abs(row['yhat']), 1))))
                
                result.append(SalesPredictionDto(
                    date=row['ds'].date(),
                    predictedRevenue=max(0, float(row['yhat'])),
                    confidence=round(float(confidence), 2)
                ))
            
            return result
            
        except Exception as e:
            self.logger.error(f"Prophet prediction error: {str(e)}")
            return self._predict_with_simple_method(df_daily_sales)
    
    def _predict_with_simple_method(self, df_daily_sales: pd.DataFrame) -> List[SalesPredictionDto]:
        """간단한 선형 회귀를 이용한 예측 (Prophet 대체)"""
        
        try:
            # 최근 7일 평균을 기준으로 예측
            recent_sales = df_daily_sales.tail(7)['y'].mean()
            
            # 간단한 트렌드 계산
            if len(df_daily_sales) >= 7:
                early_avg = df_daily_sales.head(7)['y'].mean()
                late_avg = df_daily_sales.tail(7)['y'].mean()
                trend = (late_avg - early_avg) / max(len(df_daily_sales), 1)
            else:
                trend = 0
            
            # 30일 예측
            result = []
            last_date = df_daily_sales['ds'].max().date()
            
            for i in range(1, 31):
                future_date = last_date + timedelta(days=i)
                predicted_revenue = max(0, recent_sales + (trend * i))
                
                # 단순 신뢰도 (데이터가 많을수록 높음)
                confidence = min(90, 50 + (len(df_daily_sales) * 2))
                
                result.append(SalesPredictionDto(
                    date=future_date,
                    predictedRevenue=round(float(predicted_revenue), 2),
                    confidence=round(float(confidence), 2)
                ))
            
            return result
            
        except Exception as e:
            self.logger.error(f"Simple prediction error: {str(e)}")
            return self._simple_prediction_fallback()
    
    def _simple_prediction_fallback(self) -> List[SalesPredictionDto]:
        """최후 대안 예측"""
        result = []
        today = date.today()
        
        for i in range(1, 31):
            future_date = today + timedelta(days=i)
            result.append(SalesPredictionDto(
                date=future_date,
                predictedRevenue=0.0,
                confidence=0.0
            ))
        
        return result
    
    def _empty_response(self) -> AnalyticsResponseDto:
        """빈 응답 생성"""
        return AnalyticsResponseDto(
            topSellingItems=[],
            topSellingProducts=[],
            topDiscountRanges=[],
            salesPrediction=self._simple_prediction_fallback(),
            inventoryPrediction=None,
            totalRevenue=0.0,
            totalOrders=0,
            totalWeight=None
        )

# 전역 인스턴스
analytics_processor = AnalyticsProcessor()

@app.post("/api/analytics/analyze", response_model=AnalyticsResponseDto)
async def analyze_data(request: AnalyticsRequestDto):
    """
    주문 데이터를 분석하여 통계 및 예측 결과를 반환합니다.
    """
    logger.info(f"Analytics request received for store {request.storeId}")
    logger.info(f"Date range: {request.startDate} to {request.endDate}")
    logger.info(f"Total orders: {len(request.orders)}")
    
    result = analytics_processor.process_analytics(request)
    
    logger.info("Analytics processing completed successfully")
    return result

@app.get("/health")
async def health_check():
    """헬스 체크 엔드포인트"""
    return {
        "status": "healthy", 
        "timestamp": datetime.now(),
        "prophet_available": PROPHET_AVAILABLE
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)