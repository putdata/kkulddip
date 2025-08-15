import { Plus, Minus } from 'lucide-react';

export interface QuantitySelectorProps {
  quantity: number;
  onQuantityChange: (change: number) => void;
  initialQuantity: number;
  label: string;
}

const QuantitySelector = ({
  quantity,
  onQuantityChange,
  initialQuantity = 1,
  label,
}: QuantitySelectorProps) => {
  return (
    <div className="rounded-lg bg-gray-50 p-3">
      <div className="flex items-center justify-between">
        <span className="ml-3 text-sm font-medium text-gray-900">{label}</span>
        <div className="flex items-center space-x-4">
          <button
            onClick={() => onQuantityChange(-1)}
            disabled={quantity <= initialQuantity}
            className="flex h-8 w-8 items-center justify-center rounded-full border border-gray-300 bg-white disabled:cursor-not-allowed disabled:opacity-50"
          >
            <Minus className="h-4 w-4" />
          </button>
          <span className="w-8 text-center text-sm font-semibold">
            {quantity}
          </span>
          <button
            onClick={() => onQuantityChange(1)}
            className="flex h-8 w-8 items-center justify-center rounded-full border border-gray-300 bg-white"
          >
            <Plus className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuantitySelector;
