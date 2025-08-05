import { formatPrice } from '@/utils/priceFormat';

export interface PaymentButtonProps {
  total: number;
  buttonSuffix: string;
  onNext: () => void;
}

const PaymentButton = ({ total, buttonSuffix, onNext }: PaymentButtonProps) => {
  return (
    <div className="sticky bottom-0 border-t bg-white p-4">
      <button
        onClick={onNext}
        className="w-full rounded-lg bg-blue-600 py-4 font-medium text-white"
      >
        {formatPrice(total)} {buttonSuffix}
      </button>
    </div>
  );
};

export default PaymentButton;
