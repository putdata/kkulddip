import { Plus, Minus } from 'lucide-react';

export interface QuantitySelectorProps {
  quantity: number;
  onQuantityChange: (change: number) => void;
  minQuantity: number;
  label: string;
}

const QuantitySelector = ({
  quantity,
  onQuantityChange,
  minQuantity,
  label,
}: QuantitySelectorProps) => {
  return (
    <div className="rounded-lg bg-gray-50 p-4">
      <div className="flex items-center justify-between">
        <span className="font-medium text-gray-900">{label}</span>
        <div className="flex items-center space-x-4">
          <button
            onClick={() => onQuantityChange(-1)}
            disabled={quantity <= minQuantity}
            className="flex h-10 w-10 items-center justify-center rounded-full border border-gray-300 bg-white disabled:cursor-not-allowed disabled:opacity-50"
          >
            <Minus className="h-4 w-4" />
          </button>
          <span className="w-8 text-center text-xl font-semibold">
            {quantity}
          </span>
          <button
            onClick={() => onQuantityChange(1)}
            className="flex h-10 w-10 items-center justify-center rounded-full border border-gray-300 bg-white"
          >
            <Plus className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuantitySelector;
