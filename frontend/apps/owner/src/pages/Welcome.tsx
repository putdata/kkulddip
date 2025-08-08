// TODO: 임시 더미 페이지
import { useNavigate } from 'react-router-dom';
import { useUserStore, useAuthStore } from 'common';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { ROUTE_PATH } from '@/router/route-path';
import {
  Store,
  Users,
  BarChart3,
  Video,
  ShoppingBag,
  Star,
  ArrowRight,
  ChefHat,
} from 'lucide-react';

const Welcome = () => {
  const navigate = useNavigate();
  const user = useUserStore(state => state.user);
  const { clearAuth } = useAuthStore();
  const { clearUser } = useUserStore();

  const handleLogout = () => {
    clearAuth();
    clearUser();
    navigate(ROUTE_PATH.LOGIN);
  };

  const features = [
    {
      icon: Store,
      title: '매장 관리',
      description: '매장 정보, 메뉴, 운영시간을 한 곳에서 관리하세요',
    },
    {
      icon: ShoppingBag,
      title: '주문 관리',
      description: '실시간 주문 현황을 확인하고 효율적으로 처리하세요',
    },
    {
      icon: Video,
      title: '라이브 방송',
      description: '고객과 소통하며 메뉴를 생생하게 소개하세요',
    },
    {
      icon: BarChart3,
      title: '매출 분석',
      description: '매출 데이터를 분석해 더 나은 경영 전략을 세우세요',
    },
    {
      icon: Users,
      title: '고객 관리',
      description: '고객 리뷰와 피드백을 관리하고 서비스를 개선하세요',
    },
    {
      icon: ChefHat,
      title: '메뉴 추천',
      description: 'AI 기반 메뉴 추천으로 매출을 늘려보세요',
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4">
          <div className="flex items-center gap-3">
            <div className="rounded-lg bg-blue-600 p-2">
              <Store className="h-6 w-6 text-white" />
            </div>
            <h1 className="text-xl font-bold text-gray-900">
              사장님 전용 관리 시스템
            </h1>
          </div>

          <div className="flex items-center gap-4">
            {user && (
              <div className="flex items-center gap-3">
                <div className="text-right">
                  <p className="text-sm font-medium text-gray-900">
                    {user.name}
                  </p>
                  <p className="text-xs text-gray-500">{user.email}</p>
                </div>
                <Button variant="outline" size="sm" onClick={handleLogout}>
                  로그아웃
                </Button>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="py-16">
        <div className="mx-auto max-w-7xl px-4 text-center">
          <div className="mx-auto max-w-3xl">
            <h1 className="mb-6 text-4xl font-bold text-gray-900 md:text-6xl">
              사장님, 환영합니다! 🎉
            </h1>
            <p className="mb-8 text-lg text-gray-600 md:text-xl">
              아직 등록된 매장이 없습니다. <br />첫 매장을 등록하고 스마트한
              매장 관리를 시작해보세요!
            </p>

            <div className="flex flex-col items-center justify-center gap-4 sm:flex-row">
              <Button
                size="lg"
                className="bg-blue-600 px-8 py-3 text-lg hover:bg-blue-700"
              >
                매장 등록하기
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
              <Button variant="outline" size="lg" className="px-8 py-3 text-lg">
                서비스 둘러보기
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16">
        <div className="mx-auto max-w-7xl px-4">
          <div className="mb-12 text-center">
            <h2 className="mb-4 text-3xl font-bold text-gray-900">
              이런 기능들을 제공해드려요
            </h2>
            <p className="text-gray-600">
              매장 운영에 필요한 모든 기능을 하나의 플랫폼에서 만나보세요
            </p>
          </div>

          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {features.map((feature, index) => (
              <Card
                key={index}
                className="border-0 shadow-sm transition-shadow hover:shadow-md"
              >
                <CardContent className="p-6">
                  <div className="mb-4 flex items-center gap-3">
                    <div className="rounded-lg bg-blue-100 p-3">
                      <feature.icon className="h-6 w-6 text-blue-600" />
                    </div>
                    <h3 className="text-lg font-semibold text-gray-900">
                      {feature.title}
                    </h3>
                  </div>
                  <p className="text-gray-600">{feature.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="bg-white py-16">
        <div className="mx-auto max-w-7xl px-4">
          <div className="grid gap-8 md:grid-cols-3">
            <div className="text-center">
              <div className="mb-2 text-4xl font-bold text-blue-600">1000+</div>
              <div className="text-gray-600">만족한 사장님들</div>
            </div>
            <div className="text-center">
              <div className="mb-2 text-4xl font-bold text-green-600">95%</div>
              <div className="text-gray-600">매출 증가율</div>
            </div>
            <div className="text-center">
              <div className="mb-2 text-4xl font-bold text-purple-600">
                24/7
              </div>
              <div className="text-gray-600">고객 지원</div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16">
        <div className="mx-auto max-w-4xl px-4 text-center">
          <Card className="border-0 bg-gradient-to-r from-blue-600 to-purple-600 text-white shadow-xl">
            <CardContent className="p-8">
              <div className="mb-4 flex justify-center">
                <div className="flex -space-x-2">
                  <Star className="h-8 w-8 fill-yellow-400 text-yellow-400" />
                  <Star className="h-8 w-8 fill-yellow-400 text-yellow-400" />
                  <Star className="h-8 w-8 fill-yellow-400 text-yellow-400" />
                  <Star className="h-8 w-8 fill-yellow-400 text-yellow-400" />
                  <Star className="h-8 w-8 fill-yellow-400 text-yellow-400" />
                </div>
              </div>
              <h3 className="mb-4 text-2xl font-bold">지금 시작하세요!</h3>
              <p className="mb-6 text-lg opacity-90">
                첫 매장 등록은 무료입니다. 스마트한 매장 관리의 첫걸음을
                내딛어보세요.
              </p>
              <Button
                size="lg"
                variant="secondary"
                className="px-8 py-3 text-lg font-semibold"
              >
                매장 등록하고 시작하기
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 py-8 text-white">
        <div className="mx-auto max-w-7xl px-4 text-center">
          <p className="text-gray-400">
            © 2024 사장님 전용 관리 시스템. All rights reserved.
          </p>
        </div>
      </footer>
    </div>
  );
};

export default Welcome;
