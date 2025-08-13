import { useState } from 'react';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import { Card, CardContent } from '@/components/ui/card';
import StoreCard from '@/components/StoreCard';
import StoreEditDialog from '@/components/StoreEditDialog';

const StoreManagement = () => {
  const { selectedStore } = useStoreSelection();
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);

  const handleEditStore = () => {
    setIsEditDialogOpen(true);
  };

  if (!selectedStore) {
    return (
      <Card>
        <CardContent className="pt-6">
          <div className="py-8 text-center text-gray-500">
            관리할 가게를 먼저 선택해주세요.
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      <StoreCard store={selectedStore} onEdit={handleEditStore} />

      <StoreEditDialog
        store={selectedStore}
        open={isEditDialogOpen}
        onOpenChange={setIsEditDialogOpen}
      />
    </div>
  );
};

export default StoreManagement;
