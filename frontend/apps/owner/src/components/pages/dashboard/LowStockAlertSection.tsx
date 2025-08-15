import { Alert, AlertDescription } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import { AlertTriangle, Package } from 'lucide-react';
import { useDdipboxList } from '@/queries/ddipbox';

interface LowStockAlertSectionProps {
  storeId: number;
}

const LowStockAlertSection = ({ storeId }: LowStockAlertSectionProps) => {
  const { data: ddipboxData, isLoading } = useDdipboxList(storeId);

  if (isLoading || !ddipboxData) {
    return null;
  }

  const lowStockDdipboxes = ddipboxData.filter(
    box => box.remainingQuantity <= Math.floor(box.dailyQuantity * 0.2),
  );

  if (lowStockDdipboxes.length === 0) {
    return null;
  }

  return (
    <Alert variant="destructive" className="border-orange-200 bg-orange-50">
      <AlertTriangle className="h-4 w-4 text-orange-600" />
      <AlertDescription className="text-orange-800">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="font-medium">재고 부족 알림</span>
            <Badge variant="outline" className="text-orange-600">
              {lowStockDdipboxes.length}개 항목
            </Badge>
          </div>
        </div>

        <div className="mt-3 grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {lowStockDdipboxes.slice(0, 4).map(ddipbox => (
            <div
              key={ddipbox.ddipboxId}
              className="flex items-center justify-between rounded border bg-white p-3 shadow-sm"
            >
              <div className="flex items-center gap-2">
                <Package className="h-4 w-4 text-orange-500" />
                <div>
                  <div className="text-sm font-medium">
                    {ddipbox.ddipboxName}
                  </div>
                  <div className="text-xs text-orange-600">
                    남은 재고: {ddipbox.remainingQuantity}개
                  </div>
                </div>
              </div>
              <Badge variant="outline" className="text-xs text-orange-600">
                {Math.round(
                  (ddipbox.remainingQuantity / ddipbox.dailyQuantity) * 100,
                )}
                %
              </Badge>
            </div>
          ))}
        </div>

        {lowStockDdipboxes.length > 4 && (
          <div className="mt-2 text-xs text-orange-600">
            +{lowStockDdipboxes.length - 4}개 더 있음
          </div>
        )}
      </AlertDescription>
    </Alert>
  );
};

export default LowStockAlertSection;
