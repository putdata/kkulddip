import { StoreDetailHeader } from '@/components/pages/storeDetail/StoreDetailHeader/StoreDetailHeader';
import { StoreDetailContainer } from '@/components/pages/storeDetail/StoreDetailContainer/StoreDetailContainer';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { StoreReviewsContainer } from '@/components/pages/storeDetail/StoreDetailContainer/StoreReviews/StoreReviewsContainer';

import { useStoreDetail } from '@/hooks/useStoreDetail';
import { useStoreDdipBoxes } from '@/hooks/useStoreDdipBoxes';
import { useStoreReviews } from '@/hooks/useStoreReviews';

import { useParams } from 'react-router-dom';
import { useCustomerProfile } from '@/hooks/useProfile';
import { Loader2 } from 'lucide-react';

const StoreDetail = () => {
  const params = useParams();
  const storeId = params.storeId!;

  // customerId 받기
  const { data: profile } = useCustomerProfile();
  const customerId = profile?.customerId;

  const {
    data: store,
    isLoading: storeLoading,
    error: storeError,
  } = useStoreDetail(storeId);

  const {
    data: ddipBoxes,
    isLoading: ddipBoxLoading,
    error: ddipBoxError,
  } = useStoreDdipBoxes(storeId);

  const { data: reviewResponse } = useStoreReviews(storeId);

  // 둘 중 하나라도 로딩 중이면 로딩 표시
  if (storeLoading || ddipBoxLoading) {
    return (
      <div className="flex min-h-dvh items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">가게 정보를 불러오고 있어요...</div>
        </div>
      </div>
    );
  }

  // 에러 처리
  if (storeError || ddipBoxError || !store || !ddipBoxes) {
    return <div>에러가 발생했습니다.</div>;
  }

  // -----여기서부터 모든 store 정보 받은 상태 -----

  const reviewTotalCount = store.reviewCount;

  return (
    <div className="bg-gray-100 font-[segoe_ui]">
      <StoreDetailHeader
        store={store}
        ddipboxes={ddipBoxes}
        customerId={customerId}
      />

      <Tabs className="w-full gap-0" defaultValue="details">
        <TabsList className="bg-background w-full justify-start rounded-none border-b p-0">
          <TabsTrigger
            value="details"
            className="bg-background data-[state=active]:border-b-primary h-full rounded-none border border-b-[3px] border-transparent data-[state=active]:text-amber-500 data-[state=active]:shadow-none"
          >
            가게 정보
          </TabsTrigger>
          <TabsTrigger
            value="reviews"
            className="bg-background data-[state=active]:border-b-primary h-full rounded-none border border-b-[3px] border-transparent data-[state=active]:text-amber-500 data-[state=active]:shadow-none"
          >
            가게 리뷰
          </TabsTrigger>
        </TabsList>

        <TabsContent value="details">
          <StoreDetailContainer store={store} ddipBoxes={ddipBoxes || []} />
        </TabsContent>
        <TabsContent value="reviews">
          <StoreReviewsContainer
            storeId={storeId}
            reviewResponse={reviewResponse}
            reviewTotalCount={reviewTotalCount}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default StoreDetail;
