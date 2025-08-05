import { type CardItemProps } from '@/types/likeFoodCard';
import { formatPrice } from '@/utils/priceFormat';

const LikeFoodCard = ({ item, onClick }: CardItemProps) => {
  return (
    <div
      onClick={onClick}
      className="flex max-w-full cursor-pointer items-start gap-3 rounded-md border border-gray-200 bg-white p-3 shadow-sm hover:shadow-md"
    >
      {/* 이미지 */}
      <div className="relative h-20 w-20 flex-shrink-0 overflow-hidden rounded bg-gray-100">
        <img
          src={item.img.src}
          alt={item.img.alt}
          className="h-full w-full object-cover"
        />
        {item.price.discount > 0 && (
          <div className="absolute bottom-1 left-1 rounded bg-blue-600 px-1 py-[2px] text-[10px] font-medium text-white">
            {formatPrice(item.price.discount)} 할인
          </div>
        )}
      </div>

      <div className="flex flex-1 flex-col gap-[4px]">
        <h3 className="line-clamp-1 text-sm font-semibold leading-snug text-gray-900">
          {item.storeInfo.storeName}
        </h3>

        <p className="line-clamp-1 text-xs leading-tight text-gray-500">
          {item.storeInfo.description}
        </p>

        <div className="flex items-center gap-1 text-xs">
          {item.price.original && (
            <span className="text-gray-400 line-through">
              {formatPrice(item.price.original)}{' '}
            </span>
          )}
          <span className="font-bold text-green-600">
            {formatPrice(item.price.discount)}{' '}
          </span>
        </div>

        <div className="flex flex-wrap gap-1 text-[10px]">
          {item.timeLeftHour && (
            <span className="rounded-full bg-red-500 px-1.5 py-[2px] font-semibold text-white">
              {item.timeLeftHour}시간
            </span>
          )}
          {item.discountRate && (
            <span className="rounded bg-yellow-100 px-1.5 py-[2px] font-semibold text-yellow-800">
              {item.discountRate}% 할인
            </span>
          )}
        </div>

        <div className="flex items-center justify-between text-[10px] text-gray-600">
          <div className="flex items-center gap-1">
            <span>⭐ {item.storeInfo.ratingAverage.toFixed(1)}</span>
            <span>📍 {item.distance}km</span>
          </div>

          {item.remainingQuantity !== undefined && (
            <div className="rounded-md border border-red-200 bg-red-50 px-1.5 py-[2px] text-[10px] text-red-500">
              남은 수량 {item.remainingQuantity}개
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default LikeFoodCard;
