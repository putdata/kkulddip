/**
 * 주문 음식 아이템 인터페이스
 */
export interface OrderFoodItem {
  storeInfo: {
    storeName: string;
    description: string;
    ratingAverage?: number;
  };
  img: {
    src: string;
    alt: string;
  };
  price: {
    original: number;
    discount: number;
    discountAmount: number;
  };
  date: string;
  items: {
    name: string;
    quantity: number;
  }[];
  isFavorited?: boolean;
  orderId: number;
}

/**
 * 주문 카드 컴포넌트 Props 인터페이스
 */
export interface OrderCardProps {
  item: OrderFoodItem;
}
