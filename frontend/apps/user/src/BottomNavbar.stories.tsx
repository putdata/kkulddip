import type { Meta, StoryObj } from "@storybook/react-vite";
import BottomNavbar from "./BottomNavbar";
import { Heart } from "lucide-react";

const meta = {
  title: "Components/BottomNavbar",
  component: BottomNavbar,
  parameters: {
    layout: "fullscreen",
    docs: {
      description: {
        component:
          "홈, 검색, 찜, 주문내역, 마이 메뉴로 구성된 하단 네비게이션 바 컴포넌트입니다.",
      },
    },
  },
  tags: ["autodocs"],
  argTypes: {},
  decorators: [
    (Story) => (
      <div className="min-h-screen bg-gray-50 relative">
        <div className="p-6">
          <h1 className="text-2xl font-bold mb-6">쇼핑몰 메인 페이지</h1>
          <div className="grid grid-cols-2 gap-4 mb-6">
            <div className="bg-white p-4 rounded-lg shadow">
              <h3 className="font-semibold mb-2">인기 상품</h3>
              <p className="text-sm text-gray-600">
                오늘의 베스트 상품을 확인해보세요
              </p>
            </div>
            <div className="bg-white p-4 rounded-lg shadow">
              <h3 className="font-semibold mb-2">특가 할인</h3>
              <p className="text-sm text-gray-600">한정 시간 특가 상품</p>
            </div>
          </div>
          <div className="space-y-4">
            {Array.from({ length: 8 }, (_, i) => (
              <div key={i} className="bg-white p-4 rounded-lg shadow">
                <h3 className="font-medium mb-2">상품 {i + 1}</h3>
                <p className="text-sm text-gray-500 mb-2">상품 설명입니다.</p>
                <div className="flex justify-between items-center">
                  <span className="font-bold text-lg">29,900원</span>
                  <button className="bg-blue-500 text-white px-3 py-1 rounded text-sm">
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
  name: "기본",
  args: {},
};

export const WithEcommerceContent: Story = {
  name: "쇼핑몰 콘텐츠와 함께",
  args: {},
  decorators: [
    (Story) => (
      <div className="min-h-screen bg-gray-50 relative">
        <div className="p-4">
          <div className="bg-gradient-to-r from-blue-500 to-purple-600 text-white p-6 rounded-lg mb-6">
            <h1 className="text-2xl font-bold mb-2">🛍️ 슈퍼 세일</h1>
            <p>최대 70% 할인! 지금 바로 확인하세요</p>
          </div>

          <div className="mb-6">
            <h2 className="text-lg font-semibold mb-3">카테고리</h2>
            <div className="grid grid-cols-4 gap-3">
              {["패션", "뷰티", "가전", "식품"].map((category) => (
                <div
                  key={category}
                  className="bg-white p-3 rounded-lg shadow text-center"
                >
                  <div className="w-8 h-8 bg-gray-200 rounded-full mx-auto mb-2"></div>
                  <span className="text-sm">{category}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="mb-6">
            <h2 className="text-lg font-semibold mb-3">인기 상품</h2>
            <div className="grid grid-cols-2 gap-4">
              {Array.from({ length: 6 }, (_, i) => (
                <div
                  key={i}
                  className="bg-white rounded-lg shadow overflow-hidden"
                >
                  <div className="h-32 bg-gray-200"></div>
                  <div className="p-3">
                    <h3 className="font-medium text-sm mb-1">
                      인기 상품 {i + 1}
                    </h3>
                    <p className="text-xs text-gray-500 mb-2">브랜드명</p>
                    <div className="flex justify-between items-center">
                      <span className="font-bold text-red-500">19,900원</span>
                      <Heart className="w-4 h-4 text-gray-400" />
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
  name: "검색 페이지",
  args: {},
  decorators: [
    (Story) => (
      <div className="min-h-screen bg-gray-50 relative">
        <div className="p-4">
          <div className="bg-white p-4 rounded-lg shadow mb-4">
            <input
              type="text"
              placeholder="상품을 검색해보세요"
              className="w-full p-3 border border-gray-200 rounded-lg"
            />
          </div>

          <div className="mb-4">
            <h2 className="text-lg font-semibold mb-3">인기 검색어</h2>
            <div className="flex flex-wrap gap-2">
              {["맨투맨", "원피스", "스니커즈", "가방", "시계"].map(
                (keyword) => (
                  <span
                    key={keyword}
                    className="bg-white px-3 py-1 rounded-full text-sm border"
                  >
                    {keyword}
                  </span>
                )
              )}
            </div>
          </div>

          <div className="mb-4">
            <h2 className="text-lg font-semibold mb-3">최근 검색어</h2>
            <div className="space-y-2">
              {["나이키 운동화", "겨울 코트", "무선 이어폰"].map((recent) => (
                <div
                  key={recent}
                  className="bg-white p-3 rounded-lg flex justify-between items-center"
                >
                  <span className="text-sm">{recent}</span>
                  <button className="text-gray-400 text-sm">삭제</button>
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
  name: "찜 페이지",
  args: {},
  decorators: [
    (Story) => (
      <div className="min-h-screen bg-gray-50 relative">
        <div className="p-4">
          <h1 className="text-2xl font-bold mb-6">찜한 상품</h1>

          <div className="space-y-4">
            {Array.from({ length: 5 }, (_, i) => (
              <div key={i} className="bg-white p-4 rounded-lg shadow flex">
                <div className="w-20 h-20 bg-gray-200 rounded-lg mr-4"></div>
                <div className="flex-1">
                  <h3 className="font-medium mb-1">찜한 상품 {i + 1}</h3>
                  <p className="text-sm text-gray-500 mb-2">브랜드명</p>
                  <div className="flex justify-between items-center">
                    <span className="font-bold">25,900원</span>
                    <div className="flex gap-2">
                      <button className="bg-blue-500 text-white px-3 py-1 rounded text-sm">
                        장바구니
                      </button>
                      <Heart className="w-5 h-5 text-red-500 fill-current" />
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
  name: "주문내역 페이지",
  args: {},
  decorators: [
    (Story) => (
      <div className="min-h-screen bg-gray-50 relative">
        <div className="p-4">
          <h1 className="text-2xl font-bold mb-6">주문내역</h1>

          <div className="space-y-4">
            {Array.from({ length: 4 }, (_, i) => (
              <div key={i} className="bg-white p-4 rounded-lg shadow">
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <span className="text-sm text-gray-500">
                      2024.07.{20 + i}
                    </span>
                    <span className="ml-2 bg-green-100 text-green-800 px-2 py-1 rounded text-xs">
                      배송완료
                    </span>
                  </div>
                  <span className="font-bold">89,900원</span>
                </div>

                <div className="flex">
                  <div className="w-16 h-16 bg-gray-200 rounded mr-3"></div>
                  <div className="flex-1">
                    <h3 className="font-medium mb-1">주문 상품 {i + 1}</h3>
                    <p className="text-sm text-gray-500">외 2개</p>
                  </div>
                </div>

                <div className="flex gap-2 mt-3">
                  <button className="flex-1 border border-gray-300 py-2 rounded text-sm">
                    재주문
                  </button>
                  <button className="flex-1 border border-gray-300 py-2 rounded text-sm">
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
  name: "마이 페이지",
  args: {},
  decorators: [
    (Story) => (
      <div className="min-h-screen bg-gray-50 relative">
        <div className="p-4">
          <div className="bg-white p-6 rounded-lg shadow mb-6">
            <div className="flex items-center mb-4">
              <div className="w-16 h-16 bg-gray-300 rounded-full mr-4"></div>
              <div>
                <h2 className="text-xl font-bold">홍길동님</h2>
                <p className="text-sm text-gray-500">VIP 회원</p>
              </div>
            </div>

            <div className="grid grid-cols-3 gap-4 text-center">
              <div>
                <div className="font-bold text-lg">12</div>
                <div className="text-sm text-gray-500">주문</div>
              </div>
              <div>
                <div className="font-bold text-lg">5</div>
                <div className="text-sm text-gray-500">리뷰</div>
              </div>
              <div>
                <div className="font-bold text-lg">23</div>
                <div className="text-sm text-gray-500">찜</div>
              </div>
            </div>
          </div>

          <div className="space-y-2">
            {[
              "주문/배송 조회",
              "쿠폰함",
              "적립금",
              "1:1 문의",
              "공지사항",
              "설정",
            ].map((menu) => (
              <div
                key={menu}
                className="bg-white p-4 rounded-lg shadow flex justify-between items-center"
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
  name: "모바일 뷰",
  args: {},
  parameters: {
    viewport: {
      defaultViewport: "mobile1",
    },
  },
  decorators: [
    (Story) => (
      <div className="min-h-screen bg-gray-50 relative max-w-sm mx-auto">
        <div className="p-4">
          <h1 className="text-xl font-bold mb-4">모바일 쇼핑몰</h1>
          <div className="grid grid-cols-1 gap-3">
            {Array.from({ length: 6 }, (_, i) => (
              <div key={i} className="bg-white p-3 rounded-lg shadow flex">
                <div className="w-16 h-16 bg-gray-200 rounded mr-3"></div>
                <div className="flex-1">
                  <h3 className="font-medium text-sm mb-1">
                    모바일 상품 {i + 1}
                  </h3>
                  <p className="text-xs text-gray-500 mb-1">상품 설명</p>
                  <span className="font-bold text-sm">19,900원</span>
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
