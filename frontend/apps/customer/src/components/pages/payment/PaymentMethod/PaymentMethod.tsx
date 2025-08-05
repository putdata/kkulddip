import { Check } from 'lucide-react';
import { PAYMENT_METHOD } from '@/constants/payment';

const PaymentMethod = () => {
  return (
    <div className="space-y-3">
      <h3 className="font-medium text-gray-900">결제 수단</h3>
      <div className="flex w-full items-center space-x-3 rounded-lg border-2 border-blue-600 bg-blue-50 p-4">
        <div className="flex h-5 w-5 items-center justify-center rounded bg-blue-600">
          <span className="text-xs font-bold text-white">
            {PAYMENT_METHOD.TOSS_PAY.icon}
          </span>
        </div>
        <span className="flex-1 text-left font-medium text-blue-900">
          {PAYMENT_METHOD.TOSS_PAY.name}
        </span>
        <Check className="h-5 w-5 text-blue-600" />
      </div>
    </div>
  );
};

export default PaymentMethod;
