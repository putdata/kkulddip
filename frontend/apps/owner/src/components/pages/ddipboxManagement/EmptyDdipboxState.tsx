import { Package, Plus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';

interface EmptyDdipboxStateProps {
  type: 'empty' | 'no-results';
  onAddClick?: () => void;
}

const EmptyDdipboxState = ({ type, onAddClick }: EmptyDdipboxStateProps) => {
  if (type === 'no-results') {
    return (
      <Card className="mx-auto max-w-md">
        <CardContent className="flex flex-col items-center p-8 text-center">
          <div className="bg-muted mb-4 rounded-full p-4">
            <Package className="text-muted-foreground h-8 w-8" />
          </div>
          <h3 className="mb-2 text-lg font-semibold">검색 결과가 없습니다</h3>
          <p className="text-muted-foreground text-sm">
            다른 검색어를 입력하거나 필터를 변경해보세요.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="mx-auto max-w-md">
      <CardContent className="flex flex-col items-center p-8 text-center">
        <div className="bg-muted mb-4 rounded-full p-4">
          <Package className="text-muted-foreground h-8 w-8" />
        </div>
        <h3 className="mb-2 text-lg font-semibold">등록된 띱박스가 없습니다</h3>
        <p className="text-muted-foreground mb-4 text-sm">
          첫 번째 띱박스를 등록하여 판매를 시작해보세요.
        </p>
        {onAddClick && (
          <Button onClick={onAddClick} className="gap-2">
            <Plus className="h-4 w-4" />첫 번째 띱박스 등록하기
          </Button>
        )}
      </CardContent>
    </Card>
  );
};

export default EmptyDdipboxState;
