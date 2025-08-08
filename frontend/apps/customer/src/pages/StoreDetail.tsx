import { StoreDetailHeader } from '@/components/pages/storeDetail/StoreDetailHeader/StoreDetailHeader';
import { StoreDetailContainer } from '@/components/pages/storeDetail/StoreDetailContainer/StoreDetailContainer';
import { mockStoreDetail } from '@/dummies/storeDetailDummy';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { StoreReviewsContainer } from '@/components/pages/storeDetail/StoreDetailContainer/StoreReviews/StoreReviewsContainer';
import { reviewMockData } from '@/constants/reviewMockData';
// import { useParams } from 'react-router-dom';

const StoreDetail = () => {
  // const params = useParams();
  // const storeId = params.storeId;

  const store = mockStoreDetail;
  const reviews = reviewMockData;

  return (
    <div className="bg-gray-100 font-[segoe_ui]">
      <StoreDetailHeader store={store} />

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
          <StoreDetailContainer store={store} />
        </TabsContent>
        <TabsContent value="reviews">
          <StoreReviewsContainer reviews={reviews} />
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default StoreDetail;
