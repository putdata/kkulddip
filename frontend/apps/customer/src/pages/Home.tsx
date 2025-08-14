import FilterBar from '@/components/pages/home/FilterBar';
import HomeMainContainer from '@/components/pages/home/HomeMainContainer';
import { useStores } from '@/hooks/useStores';

const Home = () => {
  const {
    data: storesResponse,
    isLoading: storeLoading,
    error: storeError,
  } = useStores();

  // 둘 중 하나라도 로딩 중이면 로딩 표시
  if (storeLoading) {
    return <div>로딩 중...</div>;
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
