import { Plus } from 'lucide-react';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import { useDdipboxDialog } from '@/hooks/useDdipboxDialog';
import { useDdipboxFilter } from '@/hooks/useDdipboxFilter';
import { useDdipboxList } from '@/queries/ddipbox';
import { Button } from '@/components/ui/button';
import {
  DdipboxStatisticsCards,
  DdipboxSearchFilter,
  DdipboxCard,
  DdipboxManagementSkeleton,
  DdipboxManagementError,
  EmptyDdipboxState,
  AddDdipboxDialog,
  EditDdipboxDialog,
  DeleteDdipboxDialog,
  DdipboxQuantityDialog,
} from '@/components/pages/ddipboxManagement';

const DdipboxManagement = () => {
  const { storeId } = useStoreSelection();

  const {
    data: ddipboxData,
    isLoading,
    error,
    refetch,
  } = useDdipboxList(storeId);

  const ddipboxes = ddipboxData || [];
  
  const {
    selectedDdipbox,
    showAddDialog,
    showEditDialog,
    showDeleteDialog,
    showQuantityDialog,
    setShowAddDialog,
    setShowEditDialog,
    setShowDeleteDialog,
    setShowQuantityDialog,
    handleEditClick,
    handleDeleteClick,
    handleQuantityClick,
  } = useDdipboxDialog();

  const {
    searchTerm,
    statusFilter,
    filteredDdipboxes,
    setSearchTerm,
    setStatusFilter,
  } = useDdipboxFilter(ddipboxes);

  if (isLoading) {
    return <DdipboxManagementSkeleton />;
  }
  if (error) {
    return <DdipboxManagementError error={error} onRetry={refetch} />;
  }


  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">띱박스 관리</h1>
          <p className="text-muted-foreground">띱박스를 등록하고 관리하세요</p>
        </div>
        <AddDdipboxDialog
          storeId={storeId}
          open={showAddDialog}
          onOpenChange={setShowAddDialog}
        >
          <Button>
            <Plus className="mr-2 h-4 w-4" />
            띱박스 추가
          </Button>
        </AddDdipboxDialog>
      </div>

      {/* 통계 카드 */}
      <DdipboxStatisticsCards ddipboxes={ddipboxes} />

      {/* 검색 및 필터 */}
      <DdipboxSearchFilter
        searchTerm={searchTerm}
        onSearchChange={setSearchTerm}
        statusFilter={statusFilter}
        onStatusFilterChange={setStatusFilter}
      />

      {/* 띱박스 목록 */}
      {filteredDdipboxes.length === 0 ? (
        <EmptyDdipboxState
          type={ddipboxes.length === 0 ? 'empty' : 'no-results'}
          onAddClick={
            ddipboxes.length === 0 ? () => setShowAddDialog(true) : undefined
          }
        />
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {filteredDdipboxes.map(ddipbox => (
            <DdipboxCard
              key={ddipbox.ddipboxId}
              ddipbox={ddipbox}
              storeId={storeId}
              onEditClick={() => handleEditClick(ddipbox)}
              onDeleteClick={() => handleDeleteClick(ddipbox)}
              onQuantityClick={() => handleQuantityClick(ddipbox)}
            />
          ))}
        </div>
      )}

      {/* Dialog 중앙 관리 */}
      {selectedDdipbox && (
        <>
          <EditDdipboxDialog
            open={showEditDialog}
            onOpenChange={setShowEditDialog}
            ddipbox={selectedDdipbox}
            storeId={storeId}
          />
          <DeleteDdipboxDialog
            open={showDeleteDialog}
            onOpenChange={setShowDeleteDialog}
            ddipbox={selectedDdipbox}
            storeId={storeId}
          />
          <DdipboxQuantityDialog
            open={showQuantityDialog}
            onOpenChange={setShowQuantityDialog}
            ddipbox={selectedDdipbox}
            storeId={storeId}
          />
        </>
      )}
    </div>
  );
};

export default DdipboxManagement;
