/**
 * 카드 아이템 인터페이스 (실제 사용 필드만)
 */
export interface CardItem {
  img: {
    src: string;
    alt: string;
  };
  storeInfo: {
    storeName: string;
    ratingAverage: number;
  };
  distance: number;
  price: {
    discount: number;
  };
  discountRate?: number;
}

/**
 * 카드 컴포넌트 Props 인터페이스
 */
export interface CardItemProps {
  item: CardItem;
  onClick?: () => void;
}
