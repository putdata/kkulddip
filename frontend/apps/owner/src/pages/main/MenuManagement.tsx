import { useState } from 'react';
import { useStoreIdParam } from '@/hooks/useStoreIdParam';
import { Plus, Search, Filter } from 'lucide-react';
import type { DdipBox } from '@/types/ddipbox';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useDdipboxList } from '@/queries/ddipbox';
import DdipboxListItem from '@/components/DdipboxListItem';
import AddDdipboxDialog from '@/components/AddDdipboxDialog';

const MenuManagement = () => {
  const storeId = useStoreIdParam();
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<
    'all' | 'active' | 'inactive'
  >('all');

  const { data: ddipboxData, isLoading, error } = useDdipboxList(storeId);

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="text-muted-foreground">로딩 중...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="text-destructive">오류가 발생했습니다.</div>
      </div>
    );
  }

  const ddipboxes = ddipboxData || [];

  const totalCount = ddipboxes.length;
  const activeCount = ddipboxes.filter((item: DdipBox) => item.isActive).length;
  const inactiveCount = ddipboxes.filter((item: DdipBox) => !item.isActive).length;

  // 검색 및 필터링
  const filteredDdipboxes = ddipboxes.filter((ddipbox: DdipBox) => {
    const matchesSearch =
      ddipbox.ddipboxName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      ddipbox.category.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus =
      statusFilter === 'all' ||
      (statusFilter === 'active' && ddipbox.isActive) ||
      (statusFilter === 'inactive' && !ddipbox.isActive);
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">메뉴 관리</h1>
          <p className="text-muted-foreground">띱박스를 등록하고 관리하세요</p>
        </div>
        <AddDdipboxDialog storeId={storeId} />
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <div className="bg-card rounded-lg border p-4">
          <div className="text-2xl font-bold">{totalCount}</div>
          <div className="text-muted-foreground text-sm">전체 띱박스</div>
        </div>
        <div className="bg-card rounded-lg border p-4">
          <div className="text-2xl font-bold text-green-600">{activeCount}</div>
          <div className="text-muted-foreground text-sm">활성 띱박스</div>
        </div>
        <div className="bg-card rounded-lg border p-4">
          <div className="text-2xl font-bold text-red-600">{inactiveCount}</div>
          <div className="text-muted-foreground text-sm">비활성 띱박스</div>
        </div>
      </div>

      {/* 검색 및 필터 */}
      <div className="flex flex-col gap-4 sm:flex-row">
        <div className="relative flex-1">
          <Search className="text-muted-foreground absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform" />
          <Input
            placeholder="띱박스 이름 또는 카테고리로 검색..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
        <Select
          value={statusFilter}
          onValueChange={(value: 'all' | 'active' | 'inactive') =>
            setStatusFilter(value)
          }
        >
          <SelectTrigger className="w-full sm:w-48">
            <Filter className="mr-2 h-4 w-4" />
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">전체 상태</SelectItem>
            <SelectItem value="active">활성</SelectItem>
            <SelectItem value="inactive">비활성</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* 띱박스 목록 */}
      <div className="space-y-4">
        {filteredDdipboxes.length === 0 ? (
          <div className="py-12 text-center">
            <div className="text-muted-foreground mb-4">
              {ddipboxes.length === 0
                ? '등록된 띱박스가 없습니다.'
                : '검색 결과가 없습니다.'}
            </div>
            {ddipboxes.length === 0 && (
              <AddDdipboxDialog storeId={storeId}>
                <Button>
                  <Plus className="mr-2 h-4 w-4" />첫 번째 띱박스 등록하기
                </Button>
              </AddDdipboxDialog>
            )}
          </div>
        ) : (
          filteredDdipboxes.map((ddipbox: DdipBox) => (
            <DdipboxListItem
              key={ddipbox.ddipboxId}
              ddipbox={ddipbox}
              storeId={storeId}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default MenuManagement;
