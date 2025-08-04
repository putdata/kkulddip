import { Package, Shield } from 'lucide-react';

type MyPageStatsData = {
  totalOrder: number;
  level: string;
};

type MyPageStatCardProps = {
  data?: MyPageStatsData;
  isLoading?: boolean;
  isError?: boolean;
};

const MyPageStatCard = ({
  data,
  isLoading = false,
  isError = false,
}: MyPageStatCardProps) => {
  if (isLoading || isError || !data) {
    return (
      <div className="mx-auto mt-4 w-[90%] rounded-xl border px-4 py-6 text-center text-gray-600 shadow-sm">
        {isLoading ? (
          <div className="animate-pulse">
            <div className="text-sm font-semibold">
              통계 정보를 불러오는 중입니다...
            </div>
          </div>
        ) : (
          <div className="text-sm font-semibold text-yellow-500">
            통계 정보를 불러올 수 없어요.
          </div>
        )}
      </div>
    );
  }

  const stats = [
    {
      icon: <Package size={24} />,
      value: `${data.totalOrder}회`,
      label: '총 주문 수',
    },
    {
      icon: <Shield size={24} />,
      value: data.level,
      label: '회원 등급',
    },
  ];

  return (
    <div className="mx-auto mt-4 w-[90%] rounded-xl border px-4 py-3 shadow-sm">
      <div className="flex justify-between text-center text-gray-800">
        {stats.map(({ icon, value, label }, idx) => (
          <div key={label} className="relative flex-1">
            <div className="mb-1 flex justify-center">{icon}</div>
            <div className="text-sm font-bold">{value}</div>
            <div className="text-xs text-gray-500">{label}</div>
            {idx < stats.length - 1 && (
              <div className="absolute right-0 top-1/2 h-6 w-px -translate-y-1/2 bg-gray-200" />
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default MyPageStatCard;
