import { Card, CardContent } from '@/components/ui/card';
import { Receipt } from 'lucide-react';

const EmptySettlement = () => {
  return (
    <Card className="border-2 border-dashed border-gray-200">
      <CardContent className="flex flex-col items-center justify-center py-12">
        <Receipt className="mb-4 h-12 w-12 text-gray-400" />
        <h3 className="mb-2 text-lg font-medium text-gray-900">
          정산 내역이 없습니다
        </h3>
        <p className="text-center text-sm text-gray-500">
          선택한 기간에 정산 내역이 없습니다.
          <br />
          다른 기간을 선택해보세요.
        </p>
      </CardContent>
    </Card>
  );
};

export default EmptySettlement;
