/**
 * 카드 아이템 인터페이스
 */
export interface CardItem {
  storeInfo: {
    storeName: string;
    description: string;
    ratingAverage: number;
  };
  img: {
    src: string;
    alt: string;
  };
  price: {
    original?: number;
    discount: number;
  };
  timeLeftHour?: number;
  discountRate?: number;
  distance: number;
  remainingQuantity?: number;
}

/**
 * 카드 컴포넌트 Props 인터페이스
 */
export interface CardItemProps {
  item: CardItem;
  onClick?: () => void;
}
