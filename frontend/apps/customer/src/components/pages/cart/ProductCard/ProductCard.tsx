import type { CartItem } from '@/types/cart';
import { formatPrice } from '@/utils/priceFormat';

export interface ProductCardProps {
  product: CartItem;
}

const ProductCard = ({ product }: ProductCardProps) => {
  return (
    <div className="flex px-3 py-0">
      <div className="flex-1">
        <p className="mb-1 text-sm font-semibold text-gray-900">
          {product.name}
        </p>
        <p className="mb-2 text-xs text-gray-500">{product.description}</p>
        <p className="text-xs font-bold text-gray-900">
          {formatPrice(product.price)}
        </p>
      </div>
    </div>
  );
};

export default ProductCard;
