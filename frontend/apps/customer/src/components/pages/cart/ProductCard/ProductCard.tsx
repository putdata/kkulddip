import type { Product } from '@/types/cart';
import { formatPrice } from '@/utils/priceFormat';

export interface ProductCardProps {
  product: Product;
}

const ProductCard = ({ product }: ProductCardProps) => {
  return (
    <div className="flex space-x-3">
      <div className="h-10 w-10 flex-shrink-0 overflow-hidden rounded-lg bg-orange-100">
        <img
          src={product.image}
          alt={product.name}
          className="h-full w-full object-cover"
        />
      </div>
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
