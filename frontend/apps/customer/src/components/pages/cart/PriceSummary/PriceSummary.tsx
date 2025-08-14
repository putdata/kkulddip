import { formatPrice } from '@/utils/priceFormat';

export interface PriceSummaryProps {
  productName: string;
  quantity: number;
  total: number;
  totalLabel: string;
}

const PriceSummary = ({
  productName,
  quantity,
  total,
  totalLabel,
}: PriceSummaryProps) => {
  return (
    <div className="space-y-2 rounded-lg bg-gray-50 p-4">
      <div className="flex justify-between text-xs">
        <span>
          {productName} × {quantity}
        </span>
        <span>{formatPrice(total)}</span>
      </div>
      <div className="flex justify-between border-t pt-2 text-sm font-semibold">
        <span>{totalLabel}</span>
        <span>{formatPrice(total)}</span>
      </div>
    </div>
  );
};

export default PriceSummary;
