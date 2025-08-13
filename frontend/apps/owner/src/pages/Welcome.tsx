import { useNavigate } from 'react-router-dom';
import { useUserStore, useAuthStore } from 'common';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { ROUTE_PATH } from '@/router/route-path';
import {
  useScrollHeader,
  useIntersectionObserver,
} from '@/hooks/useScrollAnimation';
import {
  Store,
  Users,
  BarChart3,
  Video,
  ArrowRight,
  Package,
  TrendingUp,
  Leaf,
  TreePine,
  Recycle,
  Globe,
  Heart,
  ChevronDown,
  Sparkles,
  Target,
} from 'lucide-react';

const Welcome = () => {
  const navigate = useNavigate();
  const user = useUserStore(state => state.user);
  const { clearAuth } = useAuthStore();
  const { clearUser } = useUserStore();
  const isScrolled = useScrollHeader();

  const handleLogout = () => {
    clearAuth();
    clearUser();
    navigate(ROUTE_PATH.LOGIN);
  };

  const heroSection = useIntersectionObserver();
  const feature1Section = useIntersectionObserver();
  const feature2Section = useIntersectionObserver();
  const feature3Section = useIntersectionObserver();
  const howItWorksSection = useIntersectionObserver();
  const additionalFeaturesSection = useIntersectionObserver();
  const ctaSection = useIntersectionObserver();

  return (
    <div className="min-h-screen bg-gradient-to-br from-amber-50 via-white to-orange-50">
      {/* Header */}
      <header
        className={`fixed left-0 right-0 top-0 z-50 transition-all duration-300 ${
          isScrolled
            ? 'bg-white/90 py-3 shadow-md backdrop-blur-md'
            : 'bg-transparent py-5'
        }`}
      >
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4">
          <div className="flex items-center gap-3">
            <div
              className={`rounded-lg p-2 transition-all duration-300 ${
                isScrolled
                  ? 'bg-gradient-to-r from-amber-500 to-orange-500 shadow-md'
                  : 'bg-gradient-to-r from-amber-500/90 to-orange-500/90'
              }`}
            >
              <Store className="h-6 w-6 text-white" />
            </div>
            <div>
              <h1
                className={`text-2xl font-bold transition-all duration-300 ${
                  isScrolled
                    ? 'bg-gradient-to-r from-amber-600 to-orange-600 bg-clip-text text-transparent'
                    : 'text-amber-700'
                }`}
              >
                꿀띱
              </h1>
            </div>
          </div>

          <div className="flex items-center gap-4">
            <Button
              size="sm"
              className="hidden bg-gradient-to-r from-amber-500 to-orange-500 font-semibold text-white shadow-md transition-all duration-300 hover:from-amber-600 hover:to-orange-600 hover:shadow-lg sm:inline-flex"
              onClick={() => navigate(ROUTE_PATH.LOGIN)}
            >
              가게 등록하기
              <ArrowRight className="ml-1 h-4 w-4" />
            </Button>

            {user && (
              <div className="flex items-center gap-3">
                <div className="hidden text-right md:block">
                  <p
                    className={`text-sm font-medium transition-colors duration-300 ${
                      isScrolled ? 'text-gray-900' : 'text-gray-700'
                    }`}
                  >
                    {user.name}
                  </p>
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleLogout}
                  className={`transition-all duration-300 ${
                    isScrolled
                      ? 'border-amber-300 text-amber-700 hover:bg-amber-50'
                      : 'border-amber-400 bg-white/50 text-amber-800 hover:bg-white/70'
                  }`}
                >
                  로그아웃
                </Button>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Hero Section - Simplified */}
      <section
        ref={heroSection.ref as React.RefObject<HTMLDivElement>}
        className="relative flex min-h-screen items-center justify-center overflow-hidden pt-20"
      >
        {/* Animated Background Elements */}
        <div className="absolute inset-0 overflow-hidden">
          <div className="animate-blob absolute -left-48 top-1/4 h-96 w-96 rounded-full bg-amber-300 opacity-20 mix-blend-multiply blur-xl filter"></div>
          <div className="animate-blob animation-delay-2000 absolute -right-48 top-1/3 h-96 w-96 rounded-full bg-orange-300 opacity-20 mix-blend-multiply blur-xl filter"></div>
          <div className="animate-blob animation-delay-4000 absolute bottom-1/4 left-1/2 h-96 w-96 -translate-x-1/2 rounded-full bg-yellow-300 opacity-20 mix-blend-multiply blur-xl filter"></div>
        </div>

        <div
          className={`relative z-10 mx-auto max-w-6xl px-4 text-center transition-all duration-1000 ${
            heroSection.isVisible
              ? 'translate-y-0 opacity-100'
              : 'translate-y-10 opacity-0'
          }`}
        >
          <div
            className={`transition-all delay-300 duration-1000 ${
              heroSection.isVisible
                ? 'scale-100 opacity-100'
                : 'scale-95 opacity-0'
            }`}
          >
            <h1 className="mb-8 text-5xl font-bold leading-tight lg:text-7xl">
              <span className="bg-gradient-to-r from-amber-600 via-orange-600 to-amber-700 bg-clip-text text-transparent">
                남는 음식이
              </span>
              <br />
              <span className="bg-gradient-to-r from-orange-600 to-red-600 bg-clip-text text-transparent">
                돈이 되는 마법
              </span>
            </h1>

            <p className="mx-auto mb-12 max-w-3xl text-xl text-gray-700 lg:text-2xl">
              매일 버려지는 재료들을{' '}
              <span className="font-bold text-amber-700">띱박스</span>로 만들어
              <br />
              특가로 판매하고 새로운 수익을 창출하세요
            </p>

            <div className="flex flex-col justify-center gap-4 sm:flex-row">
              <Button
                size="lg"
                className="group transform bg-gradient-to-r from-amber-500 to-orange-500 px-10 py-6 text-lg font-bold text-white shadow-xl transition-all duration-300 hover:scale-105 hover:from-amber-600 hover:to-orange-600 hover:shadow-2xl"
                onClick={() => navigate(ROUTE_PATH.LOGIN)}
              >
                지금 시작하기
                <ArrowRight className="ml-2 h-5 w-5 transition-transform group-hover:translate-x-1" />
              </Button>
            </div>
          </div>
        </div>

        {/* Scroll Indicator - Fixed positioning */}
        <div className="absolute bottom-16 left-1/2 -translate-x-1/2">
          <div className="flex flex-col items-center gap-2">
            <span className="text-sm text-gray-500">더 알아보기</span>
            <ChevronDown className="h-6 w-6 animate-bounce text-amber-500" />
          </div>
        </div>
      </section>

      {/* Feature 1 - 띱박스 (핵심) */}
      <section
        ref={feature1Section.ref as React.RefObject<HTMLDivElement>}
        className="flex min-h-screen items-center py-20"
      >
        <div className="mx-auto max-w-7xl px-4">
          <div className="grid items-center gap-16 lg:grid-cols-2">
            <div
              className={`transition-all duration-1000 ${
                feature1Section.isVisible
                  ? 'translate-x-0 opacity-100'
                  : '-translate-x-10 opacity-0'
              }`}
            >
              <div className="mb-4 inline-flex items-center gap-2 rounded-full bg-amber-100 px-4 py-2 text-sm font-medium text-amber-800">
                <Package className="h-4 w-4" />
                핵심 기능
              </div>
              <h2 className="mb-6 text-4xl font-bold leading-normal text-gray-900 lg:text-5xl lg:leading-normal">
                남는 재료를
                <br />
                <span className="bg-gradient-to-r from-amber-600 to-orange-600 bg-clip-text text-transparent">
                  띱박스로 만들어보세요
                </span>
              </h2>
              <p className="mb-8 text-xl leading-relaxed text-gray-600">
                마감 시간이 다가올수록 버려지는 재료들, 이제는 특별한 할인
                박스로 만들어 판매하세요. 고객은 저렴한 가격에, 사장님은 추가
                수익을!
              </p>
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <div className="mt-1 flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-green-500">
                    <svg
                      className="h-4 w-4 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M5 13l4 4L19 7"
                      />
                    </svg>
                  </div>
                  <div>
                    <p className="font-semibold text-gray-900">자유로운 구성</p>
                    <p className="text-gray-600">
                      랜덤, 고정, 개별판매 등 원하는 방식으로 구성
                    </p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <div className="mt-1 flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-green-500">
                    <svg
                      className="h-4 w-4 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M5 13l4 4L19 7"
                      />
                    </svg>
                  </div>
                  <div>
                    <p className="font-semibold text-gray-900">
                      픽업 시간 설정
                    </p>
                    <p className="text-gray-600">
                      원하는 시간에 픽업 가능하도록 설정하여 부담 없이
                    </p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <div className="mt-1 flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-green-500">
                    <svg
                      className="h-4 w-4 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M5 13l4 4L19 7"
                      />
                    </svg>
                  </div>
                  <div>
                    <p className="font-semibold text-gray-900">
                      즉시 판매 가능
                    </p>
                    <p className="text-gray-600">
                      등록 즉시 고객에게 노출되어 빠른 판매
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div
              className={`transition-all delay-300 duration-1000 ${
                feature1Section.isVisible
                  ? 'translate-x-0 opacity-100'
                  : 'translate-x-10 opacity-0'
              }`}
            >
              <Card className="border-0 bg-gradient-to-br from-amber-50 to-orange-50 shadow-2xl">
                <CardContent className="p-12">
                  <div className="space-y-6 text-center">
                    <Package className="mx-auto h-20 w-20 text-amber-500" />
                    <h3 className="text-2xl font-bold text-gray-900">
                      띱박스 예시
                    </h3>
                    <div className="space-y-3 rounded-lg bg-white p-6 text-left">
                      <div className="flex items-center justify-between border-b pb-2">
                        <span className="text-gray-600">김밥 3줄</span>
                        <span className="text-gray-400 line-through">
                          ₩9,000
                        </span>
                      </div>
                      <div className="flex items-center justify-between border-b pb-2">
                        <span className="text-gray-600">떡볶이 1인분</span>
                        <span className="text-gray-400 line-through">
                          ₩4,000
                        </span>
                      </div>
                      <div className="flex items-center justify-between border-b pb-2">
                        <span className="text-gray-600">순대 1인분</span>
                        <span className="text-gray-400 line-through">
                          ₩3,000
                        </span>
                      </div>
                      <div className="flex items-center justify-between pt-2">
                        <span className="text-lg font-bold">띱박스 특가</span>
                        <span className="text-2xl font-bold text-amber-600">
                          ₩7,900
                        </span>
                      </div>
                      <div className="pt-4 text-center">
                        <span className="inline-flex items-center gap-1 font-semibold text-green-600">
                          <TrendingUp className="h-4 w-4" />
                          50% 할인!
                        </span>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </section>

      {/* Feature 2 - 환경 보호 */}
      <section
        ref={feature2Section.ref as React.RefObject<HTMLDivElement>}
        className="flex min-h-screen items-center bg-gradient-to-br from-green-50 to-emerald-50 py-20"
      >
        <div className="mx-auto max-w-7xl px-4">
          <div className="grid items-center gap-16 lg:grid-cols-2">
            <div
              className={`order-2 transition-all duration-1000 lg:order-1 ${
                feature2Section.isVisible
                  ? 'translate-x-0 opacity-100'
                  : '-translate-x-10 opacity-0'
              }`}
            >
              <Card className="border-0 bg-white shadow-2xl">
                <CardContent className="p-12">
                  <div className="space-y-6 text-center">
                    <Globe className="mx-auto h-20 w-20 text-green-500" />
                    <h3 className="text-2xl font-bold text-gray-900">
                      환경 보호 효과
                    </h3>
                    <div className="grid grid-cols-2 gap-4">
                      <div className="rounded-lg bg-green-50 p-4">
                        <TreePine className="mx-auto mb-2 h-8 w-8 text-green-600" />
                        <p className="text-2xl font-bold text-green-600">
                          500kg
                        </p>
                        <p className="text-sm text-gray-600">
                          월간 음식물 쓰레기 감소
                        </p>
                      </div>
                      <div className="rounded-lg bg-blue-50 p-4">
                        <Recycle className="mx-auto mb-2 h-8 w-8 text-blue-600" />
                        <p className="text-2xl font-bold text-blue-600">
                          1.2톤
                        </p>
                        <p className="text-sm text-gray-600">연간 CO₂ 감소량</p>
                      </div>
                      <div className="rounded-lg bg-emerald-50 p-4">
                        <Leaf className="mx-auto mb-2 h-8 w-8 text-emerald-600" />
                        <p className="text-2xl font-bold text-emerald-600">
                          85%
                        </p>
                        <p className="text-sm text-gray-600">
                          재고 활용률 증가
                        </p>
                      </div>
                      <div className="rounded-lg bg-amber-50 p-4">
                        <Heart className="mx-auto mb-2 h-8 w-8 text-amber-600" />
                        <p className="text-2xl font-bold text-amber-600">
                          2,000+
                        </p>
                        <p className="text-sm text-gray-600">함께하는 고객들</p>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>

            <div
              className={`order-1 transition-all delay-300 duration-1000 lg:order-2 ${
                feature2Section.isVisible
                  ? 'translate-x-0 opacity-100'
                  : 'translate-x-10 opacity-0'
              }`}
            >
              <div className="mb-4 inline-flex items-center gap-2 rounded-full bg-green-100 px-4 py-2 text-sm font-medium text-green-800">
                <Leaf className="h-4 w-4" />
                사회적 가치
              </div>
              <h2 className="mb-6 text-4xl font-bold leading-normal text-gray-900 lg:text-5xl lg:leading-normal">
                지구를 위한
                <br />
                <span className="bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">
                  지속 가능한 선택
                </span>
              </h2>
              <p className="mb-8 text-xl leading-relaxed text-gray-600">
                꿀띱은 단순한 판매 플랫폼이 아닙니다. 음식물 쓰레기를 줄이고,
                자원을 아끼며, 더 나은 미래를 만들어가는 환경 보호 운동입니다.
              </p>
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <Globe className="mt-1 h-6 w-6 flex-shrink-0 text-green-500" />
                  <div>
                    <p className="font-semibold text-gray-900">
                      탄소 발자국 감소
                    </p>
                    <p className="text-gray-600">
                      음식물 처리 과정에서 발생하는 온실가스 절감
                    </p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <Recycle className="mt-1 h-6 w-6 flex-shrink-0 text-blue-500" />
                  <div>
                    <p className="font-semibold text-gray-900">
                      자원 순환 경제
                    </p>
                    <p className="text-gray-600">
                      버려질 음식에 새로운 가치를 부여
                    </p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <Heart className="mt-1 h-6 w-6 flex-shrink-0 text-red-500" />
                  <div>
                    <p className="font-semibold text-gray-900">사회적 기여</p>
                    <p className="text-gray-600">
                      합리적 소비 문화 확산과 지역 커뮤니티 활성화
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Feature 3 - 데이터 분석 */}
      <section
        ref={feature3Section.ref as React.RefObject<HTMLDivElement>}
        className="flex min-h-screen items-center py-20"
      >
        <div className="mx-auto max-w-7xl px-4">
          <div className="grid items-center gap-16 lg:grid-cols-2">
            <div
              className={`transition-all duration-1000 ${
                feature3Section.isVisible
                  ? 'translate-x-0 opacity-100'
                  : '-translate-x-10 opacity-0'
              }`}
            >
              <div className="mb-4 inline-flex items-center gap-2 rounded-full bg-purple-100 px-4 py-2 text-sm font-medium text-purple-800">
                <BarChart3 className="h-4 w-4" />
                비즈니스 인사이트
              </div>
              <h2 className="mb-6 text-4xl font-bold leading-normal text-gray-900 lg:text-5xl lg:leading-normal">
                데이터로 보는
                <br />
                <span className="bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
                  스마트한 경영
                </span>
              </h2>
              <p className="mb-8 text-xl leading-relaxed text-gray-600">
                어떤 띱박스가 인기 있는지, 언제 가장 많이 팔리는지, 모든
                데이터를 한눈에 확인하세요. 데이터 기반의 스마트한 의사결정을
                도와드립니다.
              </p>
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <div className="mt-1 flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-purple-500">
                    <svg
                      className="h-4 w-4 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M5 13l4 4L19 7"
                      />
                    </svg>
                  </div>
                  <div>
                    <p className="font-semibold text-gray-900">
                      실시간 매출 분석
                    </p>
                    <p className="text-gray-600">
                      띱박스별 판매량과 수익을 실시간으로 확인
                    </p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <div className="mt-1 flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-purple-500">
                    <svg
                      className="h-4 w-4 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M5 13l4 4L19 7"
                      />
                    </svg>
                  </div>
                  <div>
                    <p className="font-semibold text-gray-900">
                      고객 선호도 분석
                    </p>
                    <p className="text-gray-600">
                      어떤 구성의 띱박스가 인기 있는지 파악
                    </p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <div className="mt-1 flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-purple-500">
                    <svg
                      className="h-4 w-4 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M5 13l4 4L19 7"
                      />
                    </svg>
                  </div>
                  <div>
                    <p className="font-semibold text-gray-900">
                      최적 판매 시간 제안
                    </p>
                    <p className="text-gray-600">
                      데이터 기반으로 가장 효과적인 판매 시간대 추천
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div
              className={`transition-all delay-300 duration-1000 ${
                feature3Section.isVisible
                  ? 'translate-x-0 opacity-100'
                  : 'translate-x-10 opacity-0'
              }`}
            >
              <Card className="border-0 bg-gradient-to-br from-purple-50 to-pink-50 shadow-2xl">
                <CardContent className="p-12">
                  <BarChart3 className="mx-auto mb-6 h-16 w-16 text-purple-500" />
                  <h3 className="mb-6 text-center text-2xl font-bold text-gray-900">
                    실시간 대시보드
                  </h3>
                  <div className="space-y-4">
                    <div className="rounded-lg bg-white p-4">
                      <div className="mb-2 flex items-center justify-between">
                        <span className="text-sm text-gray-600">
                          오늘의 띱박스 판매
                        </span>
                        <span className="text-sm font-semibold text-green-600">
                          +45%
                        </span>
                      </div>
                      <div className="h-2 w-full rounded-full bg-gray-200">
                        <div
                          className="h-2 rounded-full bg-gradient-to-r from-purple-500 to-pink-500"
                          style={{ width: '78%' }}
                        ></div>
                      </div>
                    </div>
                    <div className="rounded-lg bg-white p-4">
                      <div className="mb-2 flex items-center justify-between">
                        <span className="text-sm text-gray-600">
                          이번 주 수익
                        </span>
                        <span className="text-sm font-semibold text-green-600">
                          +127%
                        </span>
                      </div>
                      <div className="h-2 w-full rounded-full bg-gray-200">
                        <div
                          className="h-2 rounded-full bg-gradient-to-r from-blue-500 to-purple-500"
                          style={{ width: '92%' }}
                        ></div>
                      </div>
                    </div>
                    <div className="rounded-lg bg-white p-4">
                      <div className="mb-2 flex items-center justify-between">
                        <span className="text-sm text-gray-600">재구매율</span>
                        <span className="text-sm font-semibold text-green-600">
                          85%
                        </span>
                      </div>
                      <div className="h-2 w-full rounded-full bg-gray-200">
                        <div
                          className="h-2 rounded-full bg-gradient-to-r from-green-500 to-blue-500"
                          style={{ width: '85%' }}
                        ></div>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section
        ref={howItWorksSection.ref as React.RefObject<HTMLDivElement>}
        className="bg-gradient-to-br from-amber-50 to-orange-50 py-20"
      >
        <div className="mx-auto w-full max-w-7xl px-4">
          <div
            className={`duration-1500 mb-16 text-center transition-all ${
              howItWorksSection.isVisible
                ? 'translate-y-0 opacity-100'
                : 'translate-y-10 opacity-0'
            }`}
          >
            <div className="mb-4 inline-flex items-center gap-2 rounded-full bg-amber-100 px-4 py-2 text-sm font-medium text-amber-800">
              <Sparkles className="h-4 w-4" />
              시작 가이드
            </div>
            <h2 className="mb-6 bg-gradient-to-r from-amber-600 to-orange-600 bg-clip-text text-4xl font-bold text-transparent lg:text-5xl">
              이렇게 간단해요
            </h2>
            <p className="mx-auto max-w-3xl text-xl text-gray-600 lg:text-2xl">
              3단계로 시작하는 꿀띱, 5분이면 충분합니다
            </p>
          </div>

          <div className="relative mx-auto max-w-6xl">
            {/* Large Background decoration */}
            <div className="from-amber-500/8 via-orange-500/8 to-red-500/8 absolute inset-0 rounded-3xl bg-gradient-to-r shadow-2xl"></div>

            <div className="relative z-10 p-8 lg:p-12">
              <div className="grid gap-12 lg:grid-cols-3">
                {[
                  {
                    step: 1,
                    title: '가게 등록',
                    desc: '기본 정보와 메뉴를 간단히 등록하면 준비 완료',
                    details: '사업자 정보, 가게 위치, 주요 메뉴만 입력하면 끝',
                    icon: Store,
                    color: 'from-amber-400 to-orange-400',
                    bgColor: 'from-amber-50 to-orange-50',
                  },
                  {
                    step: 2,
                    title: '띱박스 생성',
                    desc: '남는 재료를 선택해서 매력적인 띱박스로 구성',
                    details: '가격 설정부터 픽업 시간까지 자유롭게 설정 가능',
                    icon: Package,
                    color: 'from-orange-400 to-red-400',
                    bgColor: 'from-orange-50 to-red-50',
                  },
                  {
                    step: 3,
                    title: '판매 시작',
                    desc: '등록 즉시 고객에게 노출되어 빠른 판매 시작',
                    details: '실시간 알림으로 주문 접수부터 픽업까지 관리',
                    icon: TrendingUp,
                    color: 'from-red-400 to-pink-400',
                    bgColor: 'from-red-50 to-pink-50',
                  },
                ].map((item, index) => (
                  <div
                    key={index}
                    className={`relative transition-all duration-700 ${
                      howItWorksSection.isVisible
                        ? 'translate-y-0 opacity-100'
                        : 'translate-y-10 opacity-0'
                    }`}
                    style={{
                      transitionDelay: howItWorksSection.isVisible
                        ? `${index * 200}ms`
                        : '0ms',
                    }}
                  >
                    {/* Content Card */}
                    <div
                      className={`bg-gradient-to-br ${item.bgColor} rounded-2xl border border-white/50 p-8 shadow-lg`}
                    >
                      <div className="space-y-6">
                        <div
                          className={`h-20 w-20 bg-gradient-to-r ${item.color} flex items-center justify-center rounded-3xl shadow-lg`}
                        >
                          <item.icon className="h-10 w-10 text-white" />
                        </div>

                        <div className="space-y-3">
                          <h3 className="text-2xl font-bold text-gray-900">
                            {item.title}
                          </h3>
                          <p className="text-lg leading-relaxed text-gray-700">
                            {item.desc}
                          </p>
                          <p className="text-sm leading-relaxed text-gray-600">
                            {item.details}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Enhanced Decorative elements */}
            <div className="absolute -left-6 -top-6 h-12 w-12 rounded-full bg-amber-300 opacity-20"></div>
            <div className="absolute -bottom-6 -right-6 h-10 w-10 rounded-full bg-orange-300 opacity-30"></div>
            <div className="absolute -left-4 top-1/4 h-6 w-6 rounded-full bg-yellow-300 opacity-25"></div>
            <div className="absolute -right-4 bottom-1/4 h-8 w-8 rounded-full bg-red-300 opacity-20"></div>
          </div>
        </div>
      </section>

      {/* Additional Features Section */}
      <section
        ref={additionalFeaturesSection.ref as React.RefObject<HTMLDivElement>}
        className="bg-gradient-to-br from-slate-50 to-gray-100 py-20"
      >
        <div className="mx-auto max-w-6xl px-4">
          <div
            className={`duration-1500 mb-12 text-center transition-all ${
              additionalFeaturesSection.isVisible
                ? 'translate-y-0 opacity-100'
                : 'translate-y-10 opacity-0'
            }`}
          >
            <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-gray-200 bg-gradient-to-r from-gray-100 to-slate-100 px-4 py-2 text-sm font-medium text-gray-700">
              <Sparkles className="h-4 w-4" />
              추가 기능
            </div>
            <h2 className="mb-4 text-4xl font-bold text-gray-900">
              더욱 강력한 기능들
            </h2>
            <p className="text-xl text-gray-600">
              꿀띱과 함께 성장하는 비즈니스
            </p>
          </div>

          <div className="space-y-6">
            {[
              {
                title: '라이브 방송',
                desc: '실시간 방송으로 띱박스를 생생하게 소개하고 고객과 직접 소통하여 신뢰도를 높이세요',
                icon: Video,
                bgColor: 'bg-red-100',
                iconColor: 'text-red-600',
              },
              {
                title: '타겟 마케팅',
                desc: '고객층 분석을 통한 맞춤형 마케팅 전략으로 효과적인 홍보와 매출 증대를 경험하세요',
                icon: Target,
                bgColor: 'bg-blue-100',
                iconColor: 'text-blue-600',
              },
              {
                title: '고객 관리',
                desc: '단골 고객 관리 시스템으로 재방문율을 높이고 장기적인 고객 관계를 구축하세요',
                icon: Users,
                bgColor: 'bg-emerald-100',
                iconColor: 'text-emerald-600',
              },
            ].map((feature, index) => (
              <div
                key={index}
                className={`transition-all duration-700 ${
                  additionalFeaturesSection.isVisible
                    ? 'translate-x-0 opacity-100'
                    : 'translate-x-10 opacity-0'
                }`}
                style={{
                  transitionDelay: additionalFeaturesSection.isVisible
                    ? `${index * 150}ms`
                    : '0ms',
                }}
              >
                <div className="rounded-xl border border-gray-200 bg-white p-8 shadow-sm">
                  <div className="flex items-start gap-6">
                    <div
                      className={`h-14 w-14 flex-shrink-0 ${feature.bgColor} flex items-center justify-center rounded-xl`}
                    >
                      <feature.icon
                        className={`h-7 w-7 ${feature.iconColor}`}
                      />
                    </div>
                    <div className="flex-grow">
                      <h3 className="mb-3 text-2xl font-bold text-gray-900">
                        {feature.title}
                      </h3>
                      <p className="text-lg leading-relaxed text-gray-600">
                        {feature.desc}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section
        ref={ctaSection.ref as React.RefObject<HTMLDivElement>}
        className="py-20"
      >
        <div className="mx-auto max-w-4xl px-4 text-center">
          <div
            className={`transition-all duration-1000 ${
              ctaSection.isVisible
                ? 'scale-100 opacity-100'
                : 'scale-95 opacity-0'
            }`}
          >
            <Card className="overflow-hidden border-0 bg-gradient-to-r from-amber-500 to-orange-500 shadow-2xl">
              <CardContent className="p-12">
                <h2 className="mb-6 text-3xl font-bold text-white">
                  지금 바로 시작하세요
                </h2>
                <p className="mb-8 text-xl text-amber-50">
                  5분이면 충분해요. 첫 띱박스를 만들고
                  <br />
                  오늘부터 새로운 수익을 창출하세요.
                </p>
                <div className="mb-8 flex justify-center gap-8">
                  <div className="text-white">
                    <Leaf className="mx-auto mb-2 h-8 w-8" />
                    <span className="text-sm">환경 보호</span>
                  </div>
                  <div className="text-white">
                    <TrendingUp className="mx-auto mb-2 h-8 w-8" />
                    <span className="text-sm">수익 창출</span>
                  </div>
                  <div className="text-white">
                    <Heart className="mx-auto mb-2 h-8 w-8" />
                    <span className="text-sm">사회 기여</span>
                  </div>
                </div>
                <Button
                  size="lg"
                  className="bg-white px-10 py-4 text-lg font-bold text-amber-600 shadow-lg transition-all duration-300 hover:bg-amber-50 hover:shadow-xl"
                  onClick={() => navigate(ROUTE_PATH.LOGIN)}
                >
                  무료로 시작하기
                  <Package className="ml-2 h-5 w-5" />
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Simple Footer */}
      <footer className="py-8 text-center">
        <p className="text-gray-600">
          © 2024 <span className="font-bold text-amber-600">꿀띱</span>. All
          rights reserved.
        </p>
      </footer>
    </div>
  );
};

export default Welcome;
