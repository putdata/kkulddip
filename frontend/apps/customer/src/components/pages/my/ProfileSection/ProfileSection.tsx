import { User, ChevronRight } from 'lucide-react';

export interface ProfileData {
  name: string;
  level: string;
  orderCount: number;
  points: number;
  couponCount: number;
}

export interface ProfileSectionProps {
  profile: ProfileData;
}

const ProfileSection = ({ profile }: ProfileSectionProps) => {
  return (
    <div className="bg-white px-4 py-6">
      {/* Profile Info */}
      <div className="mb-6 flex items-center space-x-4">
        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-amber-100">
          <User className="h-8 w-8 text-gray-600" />
        </div>
        <div className="flex-1">
          <h2 className="text-lg font-semibold text-gray-900">
            {profile.name}님
          </h2>
          <p className="text-sm text-gray-500">{profile.level} 회원</p>
        </div>
        <ChevronRight className="h-5 w-5 text-gray-400" />
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-3 gap-3">
        <div className="rounded-lg bg-amber-50 p-3 text-center">
          <div className="text-lg font-bold text-gray-600">
            {profile.orderCount}
          </div>
          <div className="text-xs text-gray-600">주문</div>
        </div>
        <div className="rounded-lg bg-amber-50 p-3 text-center">
          <div className="text-lg font-bold text-gray-600">
            {profile.points.toLocaleString()}P
          </div>
          <div className="text-xs text-gray-600">포인트</div>
        </div>
        <div className="rounded-lg bg-amber-50 p-3 text-center">
          <div className="text-lg font-bold text-gray-600">
            {profile.couponCount}장
          </div>
          <div className="text-xs text-gray-600">쿠폰</div>
        </div>
      </div>
    </div>
  );
};

export default ProfileSection;
