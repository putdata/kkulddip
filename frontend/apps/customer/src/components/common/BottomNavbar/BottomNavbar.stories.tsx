import type { Meta, StoryObj } from '@storybook/react-vite';
import BottomNavbar from './BottomNavbar';
import { Heart } from 'lucide-react';

const meta = {
  title: 'Components/common/BottomNavbar',
  component: BottomNavbar,
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component:
          '홈, 검색, 찜, 주문내역, 마이 메뉴로 구성된 하단 네비게이션 바 컴포넌트입니다.',
      },
    },
  },
  tags: ['autodocs'],
  argTypes: {},
  decorators: [
    Story => (
      <div className="relative min-h-screen bg-gray-50">
        <div className="p-6">
          <h1 className="mb-6 text-2xl font-bold">쇼핑몰 메인 페이지</h1>
          <div className="mb-6 grid grid-cols-2 gap-4">
            <div className="rounded-lg bg-white p-4 shadow">
              <h3 className="mb-2 font-semibold">인기 상품</h3>
              <p className="text-sm text-gray-600">
                오늘의 베스트 상품을 확인해보세요
              </p>
            </div>
            <div className="rounded-lg bg-white p-4 shadow">
              <h3 className="mb-2 font-semibold">특가 할인</h3>
              <p className="text-sm text-gray-600">한정 시간 특가 상품</p>
            </div>
          </div>
          <div className="space-y-4">
            {Array.from({ length: 8 }, (_, i) => (
              <div key={i} className="rounded-lg bg-white p-4 shadow">
                <h3 className="mb-2 font-medium">상품 {i + 1}</h3>
                <p className="mb-2 text-sm text-gray-500">상품 설명입니다.</p>
                <div className="flex items-center justify-between">
                  <span className="text-lg font-bold">29,900원</span>
                  <button className="rounded bg-blue-500 px-3 py-1 text-sm text-white">
                    장바구니
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
        <Story />
      </div>
    ),
  ],
} satisfies Meta<typeof BottomNavbar>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  name: '기본',
  args: {},
};

export const WithEcommerceContent: Story = {
  name: '쇼핑몰 콘텐츠와 함께',
  args: {},
  decorators: [
    Story => (
      <div className="relative min-h-screen bg-gray-50">
        <div className="p-4">
          <div className="mb-6 rounded-lg bg-gradient-to-r from-blue-500 to-purple-600 p-6 text-white">
            <h1 className="mb-2 text-2xl font-bold">🛍️ 슈퍼 세일</h1>
            <p>최대 70% 할인! 지금 바로 확인하세요</p>
          </div>

          <div className="mb-6">
            <h2 className="mb-3 text-lg font-semibold">카테고리</h2>
            <div className="grid grid-cols-4 gap-3">
              {['패션', '뷰티', '가전', '식품'].map(category => (
                <div
                  key={category}
                  className="rounded-lg bg-white p-3 text-center shadow"
                >
                  <div className="mx-auto mb-2 h-8 w-8 rounded-full bg-gray-200"></div>
                  <span className="text-sm">{category}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="mb-6">
            <h2 className="mb-3 text-lg font-semibold">인기 상품</h2>
            <div className="grid grid-cols-2 gap-4">
              {Array.from({ length: 6 }, (_, i) => (
                <div
                  key={i}
                  className="overflow-hidden rounded-lg bg-white shadow"
                >
                  <div className="h-32 bg-gray-200"></div>
                  <div className="p-3">
                    <h3 className="mb-1 text-sm font-medium">
                      인기 상품 {i + 1}
                    </h3>
                    <p className="mb-2 text-xs text-gray-500">브랜드명</p>
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-red-500">19,900원</span>
                      <Heart className="h-4 w-4 text-gray-400" />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
        <Story />
      </div>
    ),
  ],
};

export const SearchPage: Story = {
  name: '검색 페이지',
  args: {},
  decorators: [
    Story => (
      <div className="relative min-h-screen bg-gray-50">
        <div className="p-4">
          <div className="mb-4 rounded-lg bg-white p-4 shadow">
            <input
              type="text"
              placeholder="상품을 검색해보세요"
              className="w-full rounded-lg border border-gray-200 p-3"
            />
          </div>

          <div className="mb-4">
            <h2 className="mb-3 text-lg font-semibold">인기 검색어</h2>
            <div className="flex flex-wrap gap-2">
              {['맨투맨', '원피스', '스니커즈', '가방', '시계'].map(keyword => (
                <span
                  key={keyword}
                  className="rounded-full border bg-white px-3 py-1 text-sm"
                >
                  {keyword}
                </span>
              ))}
            </div>
          </div>

          <div className="mb-4">
            <h2 className="mb-3 text-lg font-semibold">최근 검색어</h2>
            <div className="space-y-2">
              {['나이키 운동화', '겨울 코트', '무선 이어폰'].map(recent => (
                <div
                  key={recent}
                  className="flex items-center justify-between rounded-lg bg-white p-3"
                >
                  <span className="text-sm">{recent}</span>
                  <button className="text-sm text-gray-400">삭제</button>
                </div>
              ))}
            </div>
          </div>
        </div>
        <Story />
      </div>
    ),
  ],
};

export const WishlistPage: Story = {
  name: '찜 페이지',
  args: {},
  decorators: [
    Story => (
      <div className="relative min-h-screen bg-gray-50">
        <div className="p-4">
          <h1 className="mb-6 text-2xl font-bold">찜한 상품</h1>

          <div className="space-y-4">
            {Array.from({ length: 5 }, (_, i) => (
              <div key={i} className="flex rounded-lg bg-white p-4 shadow">
                <div className="mr-4 h-20 w-20 rounded-lg bg-gray-200"></div>
                <div className="flex-1">
                  <h3 className="mb-1 font-medium">찜한 상품 {i + 1}</h3>
                  <p className="mb-2 text-sm text-gray-500">브랜드명</p>
                  <div className="flex items-center justify-between">
                    <span className="font-bold">25,900원</span>
                    <div className="flex gap-2">
                      <button className="rounded bg-blue-500 px-3 py-1 text-sm text-white">
                        장바구니
                      </button>
                      <Heart className="h-5 w-5 fill-current text-red-500" />
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
        <Story />
      </div>
    ),
  ],
};

export const OrderHistoryPage: Story = {
  name: '주문내역 페이지',
  args: {},
  decorators: [
    Story => (
      <div className="relative min-h-screen bg-gray-50">
        <div className="p-4">
          <h1 className="mb-6 text-2xl font-bold">주문내역</h1>

          <div className="space-y-4">
            {Array.from({ length: 4 }, (_, i) => (
              <div key={i} className="rounded-lg bg-white p-4 shadow">
                <div className="mb-3 flex items-start justify-between">
                  <div>
                    <span className="text-sm text-gray-500">
                      2024.07.{20 + i}
                    </span>
                    <span className="ml-2 rounded bg-green-100 px-2 py-1 text-xs text-green-800">
                      배송완료
                    </span>
                  </div>
                  <span className="font-bold">89,900원</span>
                </div>

                <div className="flex">
                  <div className="mr-3 h-16 w-16 rounded bg-gray-200"></div>
                  <div className="flex-1">
                    <h3 className="mb-1 font-medium">주문 상품 {i + 1}</h3>
                    <p className="text-sm text-gray-500">외 2개</p>
                  </div>
                </div>

                <div className="mt-3 flex gap-2">
                  <button className="flex-1 rounded border border-gray-300 py-2 text-sm">
                    재주문
                  </button>
                  <button className="flex-1 rounded border border-gray-300 py-2 text-sm">
                    리뷰작성
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
        <Story />
      </div>
    ),
  ],
};

export const MyPage: Story = {
  name: '마이 페이지',
  args: {},
  decorators: [
    Story => (
      <div className="relative min-h-screen bg-gray-50">
        <div className="p-4">
          <div className="mb-6 rounded-lg bg-white p-6 shadow">
            <div className="mb-4 flex items-center">
              <div className="mr-4 h-16 w-16 rounded-full bg-gray-300"></div>
              <div>
                <h2 className="text-xl font-bold">홍길동님</h2>
                <p className="text-sm text-gray-500">VIP 회원</p>
              </div>
            </div>

            <div className="grid grid-cols-3 gap-4 text-center">
              <div>
                <div className="text-lg font-bold">12</div>
                <div className="text-sm text-gray-500">주문</div>
              </div>
              <div>
                <div className="text-lg font-bold">5</div>
                <div className="text-sm text-gray-500">리뷰</div>
              </div>
              <div>
                <div className="text-lg font-bold">23</div>
                <div className="text-sm text-gray-500">찜</div>
              </div>
            </div>
          </div>

          <div className="space-y-2">
            {[
              '주문/배송 조회',
              '쿠폰함',
              '적립금',
              '1:1 문의',
              '공지사항',
              '설정',
            ].map(menu => (
              <div
                key={menu}
                className="flex items-center justify-between rounded-lg bg-white p-4 shadow"
              >
                <span>{menu}</span>
                <span className="text-gray-400">›</span>
              </div>
            ))}
          </div>
        </div>
        <Story />
      </div>
    ),
  ],
};

export const MobileView: Story = {
  name: '모바일 뷰',
  args: {},
  parameters: {
    viewport: {
      defaultViewport: 'mobile1',
    },
  },
  decorators: [
    Story => (
      <div className="relative mx-auto min-h-screen max-w-sm bg-gray-50">
        <div className="p-4">
          <h1 className="mb-4 text-xl font-bold">모바일 쇼핑몰</h1>
          <div className="grid grid-cols-1 gap-3">
            {Array.from({ length: 6 }, (_, i) => (
              <div key={i} className="flex rounded-lg bg-white p-3 shadow">
                <div className="mr-3 h-16 w-16 rounded bg-gray-200"></div>
                <div className="flex-1">
                  <h3 className="mb-1 text-sm font-medium">
                    모바일 상품 {i + 1}
                  </h3>
                  <p className="mb-1 text-xs text-gray-500">상품 설명</p>
                  <span className="text-sm font-bold">19,900원</span>
                </div>
              </div>
            ))}
          </div>
        </div>
        <Story />
      </div>
    ),
  ],
};
