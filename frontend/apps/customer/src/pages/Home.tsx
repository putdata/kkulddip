import FilterBar from '@/components/pages/home/FilterBar';
import HomeMainContainer from '@/components/pages/home/HomeMainContainer';
import SearchBar from '@/components/pages/home/SearchBar';
import { dummyStoreData } from '@/constants/homeMockData';

const Home = () => {
  const stores = dummyStoreData.body.content;

  return (
    <div className="">
      <div className="sticky">
        <SearchBar />
      </div>
      <div className="sticky">
        <FilterBar />
      </div>
      <HomeMainContainer stores={stores}></HomeMainContainer>
    </div>
  );
};

export default Home;
