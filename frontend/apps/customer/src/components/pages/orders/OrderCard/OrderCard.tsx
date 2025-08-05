import { Heart, ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { formatPrice } from '@/utils/priceFormat';

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

export interface OrderCardProps {
  item: OrderFoodItem;
}

const OrderCard = ({ item }: OrderCardProps) => {
  const { date, storeInfo, img, price, items, isFavorited, orderId } = item;
  const navigate = useNavigate();
  return (
    <div className="w-full max-w-sm overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm transition-shadow">
      {/* Header */}
      <div className="flex items-center justify-between bg-gray-50 px-3 py-2">
        <span className="text-xs font-medium text-gray-600">{date}</span>
        <button
          className="rounded border border-gray-300 bg-white px-2 py-0.5 text-xs font-medium text-gray-700 transition-colors hover:bg-gray-50"
          onClick={() => navigate(`/order/${orderId}`)}
        >
          주문상세
        </button>
      </div>

      <div className="space-y-3 p-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="flex h-10 w-10 items-center justify-center overflow-hidden rounded-lg bg-gray-100">
              <img
                src={img.src}
                alt={img.alt}
                className="h-full w-full object-cover"
              />
            </div>
            <h3 className="max-w-[120px] truncate text-sm font-semibold text-gray-900">
              {storeInfo.storeName}
            </h3>
          </div>
          <div className="flex items-center gap-1">
            <ChevronRight className="h-3 w-3 text-gray-400" />
            <button className="p-0.5">
              <Heart
                className="h-4 w-4 transition-colors"
                color={isFavorited ? '#ef4444' : '#d1d5db'}
                fill={isFavorited ? '#ef4444' : 'none'}
              />
            </button>
          </div>
        </div>

        <div className="space-y-1">
          {items.map((item, idx) => (
            <div
              key={idx}
              className="flex items-center justify-between text-xs"
            >
              <span className="flex-1 truncate pr-2 text-gray-700">
                {item.name}
              </span>
              <span className="font-medium text-gray-500">
                {item.quantity}개
              </span>
            </div>
          ))}
        </div>

        <div className="space-y-1 border-t border-gray-100 pt-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-gray-800">결제금액</span>
            <div className="text-right">
              <div className="text-xs text-gray-400 line-through">
                {formatPrice(price.original)}
              </div>
              <div className="text-sm font-bold text-gray-900">
                {formatPrice(price.discount)}
              </div>
            </div>
          </div>
          <div className="text-right">
            <span className="text-xs font-medium text-blue-600">
              꿀띱에서만 {formatPrice(price.discountAmount)} 할인
            </span>
          </div>
        </div>

        <button className="border-1 mt-2 w-full rounded-lg border-gray-400 bg-white py-2 text-xs font-semibold text-gray-700 transition-colors active:bg-gray-100">
          이 띱박스 또 담기
        </button>
      </div>
    </div>
  );
};

export default OrderCard;
