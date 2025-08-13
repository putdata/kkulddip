import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Power, MapPin, Phone, Clock, Edit } from 'lucide-react';
import { useToggleStoreStatus, useDeleteStore } from '@/queries/store';
import DeleteStoreDialog from './pages/settings/DeleteStoreDialog';
import type { Store } from '@/types/store';

interface StoreCardProps {
  store: Store;
  onEdit?: () => void;
}

const StoreCard = ({ store, onEdit }: StoreCardProps) => {
  const toggleStatusMutation = useToggleStoreStatus();
  const deleteStoreMutation = useDeleteStore();

  const handleToggleStatus = () => {
    toggleStatusMutation.mutate({
      storeId: store.storeId,
      data: { isActive: !store.isActive },
    });
  };

  const handleDeleteStore = () => {
    deleteStoreMutation.mutate(store.storeId);
  };
  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="mb-2 flex items-center gap-2">
              <CardTitle className="text-base">{store.storeName}</CardTitle>
              <Badge
                variant={store.isActive ? 'default' : 'secondary'}
                className="text-xs"
              >
                {store.isActive ? '운영중' : '운영중지'}
              </Badge>
            </div>
            {store.description && (
              <CardDescription className="text-sm">
                {store.description}
              </CardDescription>
            )}
          </div>
          <div className="flex gap-2">
            {onEdit && (
              <Button variant="outline" size="sm" onClick={onEdit}>
                <Edit className="h-4 w-4" />
              </Button>
            )}
            <Button
              variant="outline"
              size="sm"
              onClick={handleToggleStatus}
              disabled={toggleStatusMutation.isPending}
            >
              <Power className="h-4 w-4" />
            </Button>
            <DeleteStoreDialog
              storeName={store.storeName}
              onDelete={handleDeleteStore}
              isDeleting={deleteStoreMutation.isPending}
            />
          </div>
        </div>
      </CardHeader>
      <CardContent className="pt-0">
        <div className="grid grid-cols-1 gap-2 text-sm text-gray-600">
          <div className="flex items-center gap-2">
            <MapPin className="h-4 w-4" />
            <span>{store.storeAddress}</span>
          </div>
          {store.phone && (
            <div className="flex items-center gap-2">
              <Phone className="h-4 w-4" />
              <span>{store.phone}</span>
            </div>
          )}
          {store.operatingHours && (
            <div className="flex items-center gap-2">
              <Clock className="h-4 w-4" />
              <span>{store.operatingHours}</span>
            </div>
          )}
          {store.businessNumber && (
            <div className="flex items-center gap-2">
              <span className="font-medium">사업자번호:</span>
              <span>{store.businessNumber}</span>
            </div>
          )}
        </div>
        <div className="mt-3 flex gap-4 text-xs text-gray-500">
          <span>평점: {store.ratingAverage.toFixed(1)}점</span>
          <span>리뷰: {store.reviewCount}개</span>
          <span>총 주문: {store.totalOrderCount}건</span>
          <span>총 매출: {store.totalRevenue.toLocaleString()}원</span>
        </div>
      </CardContent>
    </Card>
  );
};

export default StoreCard;
