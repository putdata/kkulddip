import type { Product } from '@/types/cart';
import { formatPrice } from '@/utils/priceFormat';

export interface ProductCardProps {
  product: Product;
}

const ProductCard = ({ product }: ProductCardProps) => {
  return (
    <div className="flex space-x-4">
      <div className="h-20 w-20 flex-shrink-0 overflow-hidden rounded-lg bg-orange-100">
        <img
          src={product.image}
          alt={product.name}
          className="h-full w-full object-cover"
        />
      </div>
      <div className="flex-1">
        <h3 className="mb-1 font-semibold text-gray-900">{product.name}</h3>
        <p className="mb-2 text-sm text-gray-500">{product.description}</p>
        <p className="text-lg font-bold text-gray-900">
          {formatPrice(product.price)}
        </p>
      </div>
    </div>
  );
};

export default ProductCard;
