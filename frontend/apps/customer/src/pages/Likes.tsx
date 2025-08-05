import LikeFoodCard from '@/components/pages/likes/LikeFoodCard/LikeFoodCard';
import { dummyLikeFoodData } from '@/dummies/dummyFoodData';

const Likes = () => {
  return (
    <div>
      <div className="space-y-2 px-2 pb-16 pt-16">
        {dummyLikeFoodData.map((item, idx) => (
          <LikeFoodCard key={idx} item={item} />
        ))}
      </div>
    </div>
  );
};

export default Likes;
