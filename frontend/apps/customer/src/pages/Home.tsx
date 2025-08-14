import FilterBar from '@/components/pages/home/FilterBar';
import HomeMainContainer from '@/components/pages/home/HomeMainContainer';
import { dummyStoreData } from '@/constants/homeMockData';

const Home = () => {
  const stores = dummyStoreData.body.content;

  return (
    <div>
      <div className="sticky pt-5">
        <FilterBar />
      </div>
      <HomeMainContainer stores={stores}></HomeMainContainer>
    </div>
  );
};

export default Home;
