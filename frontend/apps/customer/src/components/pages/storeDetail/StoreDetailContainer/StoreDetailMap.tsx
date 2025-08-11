import type { StoreDetailDto } from '@/types/store';

interface StoreDetailMapProps {
  store: StoreDetailDto;
}

const StoreDetailMap = ({ store }: StoreDetailMapProps) => {
  return (
    <div className="flex flex-col items-start gap-2 overflow-hidden p-3">
      <h3 className="font-bold">위치</h3>
      {/* TODO: 외부 위치 API 받아와야 함 */}
      <div>{store.storeName}</div>
      <img
        className="h-60"
        src="https://media.istockphoto.com/id/1398268546/ko/%EB%B2%A1%ED%84%B0/%EB%8F%84%EC%8B%9C-%EC%9C%84%EC%B9%98-%EB%B2%A1%ED%84%B0-%EC%9D%BC%EB%9F%AC%EC%8A%A4%ED%8A%B8%EB%A0%88%EC%9D%B4%EC%85%98-%EC%9E%90%EC%84%B8%ED%95%9C-%EC%83%81%EB%8B%A8-%EB%B3%B4%EA%B8%B0-%EC%9C%84%EC%B9%98-%EB%B0%8F-%EB%82%B4%EB%B9%84%EA%B2%8C%EC%9D%B4%EC%85%98-%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B0%9C%EB%85%90-%EB%8F%84%EC%8B%9C-%EB%8F%84%EC%8B%9C-%EA%B1%B0%EB%A6%AC-%EB%8F%84%EB%A1%9C-%EC%B6%94%EC%83%81%EC%A7%80%EB%8F%84.jpg?s=612x612&w=0&k=20&c=61B2_pcvBwLu0L-JhZpERSv6H5aiEmFj8Gwnzus01k0="
      />
    </div>
  );
};

export default StoreDetailMap;
