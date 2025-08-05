import { Pencil, PiggyBank, Cloudy } from 'lucide-react';

interface MyPageProfileCardProps {
  name: string;
  savedAmount: number;
  savedCO2: number;
}

const ProfileCard = ({
  name,
  savedAmount,
  savedCO2,
}: MyPageProfileCardProps) => {
  return (
    <div className="relative mx-auto mt-11 w-[90%] rounded-2xl bg-yellow-100 px-4 pb-4 pt-10 text-gray-800 shadow">
      {/* 프로필 이미지 */}
      <div className="absolute left-1/2 top-0 -translate-x-1/2 -translate-y-1/2">
        <div className="relative h-20 w-20 rounded-full bg-white shadow">
          <div className="flex h-full w-full items-center justify-center rounded-full bg-gradient-to-br from-yellow-300 to-yellow-400">
            <span className="text-3xl">😊</span>
          </div>
          <button className="absolute right-0 top-0 flex h-5 w-5 items-center justify-center rounded-full bg-gray-200">
            <Pencil size={10} />
          </button>
        </div>
      </div>

      {/* 이름 */}
      <div className="mb-3 mt-2 flex items-center justify-center gap-1 text-lg font-semibold">
        <span>{name}</span>
        <Pencil size={12} className="text-gray-500" />
      </div>

      {/* 절약 정보 */}
      <div className="rounded-xl bg-white/60 px-2 py-2">
        <div className="flex justify-around text-center text-sm text-gray-700">
          <div className="flex flex-col items-center p-1">
            <PiggyBank className="mb-1" />
            <div className="font-bold">{savedAmount.toLocaleString()}원</div>
            <div className="text-[0.6rem] text-gray-500">아낀 금액</div>
          </div>
          <div className="flex flex-col items-center p-1">
            <Cloudy className="mb-1" />
            <div className="font-bold">{savedCO2}kg</div>
            <div className="text-[0.6rem] text-gray-500">아낀 CO₂</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfileCard;
