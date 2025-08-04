import ProfileCard from '@/components/pages/my/ProfileCard/ProfileCard';
import MyPageStatCard from '@/components/pages/my/MyPageStatCard/MyPageStatCard';
import EventCarousel from '@/components/pages/my/EventCarousel/EventCarousel';

const MyPage = () => {
  return (
    <div className="pt-16">
      <div className="flex flex-col items-center justify-between">
        <div className="w-full max-w-md space-y-4">
          <ProfileCard name={'혜린'} savedAmount={0} savedCO2={30} />
          <MyPageStatCard />
          <EventCarousel />
        </div>
      </div>
    </div>
  );
};

export default MyPage;
