import { StoreDetailHeader } from '@/components/pages/storeDetail/StoreDetailHeader/StoreDetailHeader';
import { StoreDetailContainer } from '@/components/pages/storeDetail/StoreDetailContainer/StoreDetailContainer';
import { mockStoreDetail } from '@/dummies/storeDetailDummy';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { StoreReviews } from '@/components/pages/storeDetail/StoreDetailContainer/StoreReviews';
// import { useParams } from 'react-router-dom';
// 커밋 실수입니다. 집에서 이어서 작업하겠습니다.

const StoreDetail = () => {
  //   const params = useParams();
  //   const storeId = params.storeId;

  const store = mockStoreDetail;

  return (
    <div className="gap-2 bg-gray-300">
      <StoreDetailHeader store={store} />

      <div className="flex w-full max-w-sm flex-col gap-6">
        <Tabs className="bg-white" defaultValue="details">
          <TabsList className="bg-amber-100">
            <TabsTrigger value="details">details</TabsTrigger>
            <TabsTrigger value="reviews">reviews</TabsTrigger>
          </TabsList>
          <TabsContent value="details">
            <StoreDetailContainer />
          </TabsContent>
          <TabsContent value="reviews">
            <StoreReviews />
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default StoreDetail;
