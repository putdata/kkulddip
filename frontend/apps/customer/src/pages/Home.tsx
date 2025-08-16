import FilterBar from '@/components/pages/home/FilterBar';
import HomeMainContainer from '@/components/pages/home/HomeMainContainer';
import { useStores } from '@/hooks/useStores';
import { Loader2 } from 'lucide-react';

const Home = () => {
  const {
    data: storesResponse,
    isLoading: storeLoading,
    error: storeError,
  } = useStores();

  if (storeLoading) {
    return (
      <div className="flex min-h-dvh items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">주변 가게들을 불러오고 있어요...</div>
        </div>
      </div>
    );
  }

  // 에러 처리
  if (storeError) {
    return <div>에러가 발생했습니다.</div>;
  }

  const storelist = storesResponse?.content;

  if (storelist) {
    return (
      <div>
        <div className="pt-5">
          <FilterBar />
        </div>
        <HomeMainContainer stores={storelist}></HomeMainContainer>
      </div>
    );
  }
};

export default Home;
