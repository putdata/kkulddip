import LikeFoodCard from '@/components/pages/likes/LikeFoodCard/LikeFoodCard';
import { useLikes } from '@/hooks/useLikes';

const Likes = () => {
  const { stores } = useLikes(5);

  const transformedStores = stores.map(store => ({
    img: {
      src: store.storeProfileImage,
      alt: store.storeName,
    },
    storeInfo: {
      storeName: store.storeName,
      ratingAverage: store.reviewRating,
    },
    distance: store.distanceFromCustomer,
    price: {
      discount: 0,
    },
    discountRate: 0,
  }));

  return (
    <div>
      <div className="space-y-2 px-2 pb-16 pt-16">
        {transformedStores.map((item, idx) => {
          return (
            <LikeFoodCard
              key={stores[idx]?.storeId}
              item={item}
              onClick={() => console.log('매장 클릭')}
            />
          );
        })}
      </div>
    </div>
  );
};

export default Likes;
