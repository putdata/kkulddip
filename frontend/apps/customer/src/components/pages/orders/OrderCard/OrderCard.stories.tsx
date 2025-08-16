import type { Meta, StoryObj } from '@storybook/react-vite';
import { BrowserRouter } from 'react-router-dom';
import OrderCard from './OrderCard';
import { type OrderCardProps } from '@/types/orderFood';

const meta: Meta<typeof OrderCard> = {
  title: 'Components/OrderCard',
  component: OrderCard,
  decorators: [
    Story => (
      <BrowserRouter>
        <div className="min-h-screen bg-gray-50 p-4">
          <Story />
        </div>
      </BrowserRouter>
    ),
  ],
  parameters: {
    layout: 'fullscreen',
    docs: {
      description: {
        component:
          '주문 내역을 표시하는 카드 컴포넌트입니다. 주문 정보, 할인 내역, 리뷰 작성 버튼 등을 제공합니다.',
      },
    },
  },
  argTypes: {
    item: {
      description: '주문 정보 객체',
      control: { type: 'object' },
    },
  },
};

export default meta;
type Story = StoryObj<typeof OrderCard>;

// Mock 데이터 생성 함수
const createMockOrder = (
  overrides: Partial<OrderCardProps['item']> = {},
): OrderCardProps['item'] => ({
  orderDate: '2024-08-12T14:30:00Z',
  storeName: '맥도날드 강남점',
  storeId: 1,
  orderItems: [
    {
      productId: 1,
      productName: '빅맥 세트',
      quantity: 1,
      unitPrice: 0,
      totalPrice: 0,
    },
    {
      productId: 2,
      productName: '치킨맥너겟 4조각',
      quantity: 2,
      unitPrice: 0,
      totalPrice: 0,
    },
  ],
  originalPrice: 15900,
  finalPrice: 12900,
  orderId: 'ORDER123',
  orderStatus: 'CONFIRMED',
  pickupTime: '2024-08-12T15:00:00Z',
  hasReview: false,
  ...overrides,
});

// 기본 스토리
export const Default: Story = {
  args: {
    item: createMockOrder(),
  },
};

// 긴 상점명과 메뉴명
export const LongNames: Story = {
  args: {
    item: createMockOrder({
      storeName: '피자헛 강남역점 매우 긴 이름의 매장입니다',
      orderItems: [
        {
          productId: 1,
          productName: '슈퍼 디럭스 페퍼로니 피자 라지 사이즈 엄청 긴 메뉴명',
          quantity: 1,
          unitPrice: 0,
          totalPrice: 0,
        },
        {
          productId: 2,
          productName: '치킨윙',
          quantity: 5,
          unitPrice: 0,
          totalPrice: 0,
        },
      ],
    }),
  },
};

// 할인 없는 경우
export const NoDiscount: Story = {
  args: {
    item: createMockOrder({
      originalPrice: 12900,
      finalPrice: 12900,
    }),
  },
};

// 리뷰 작성 완료 상태
export const WithReview: Story = {
  args: {
    item: createMockOrder({
      hasReview: true,
      orderStatus: 'PICKED_UP',
    }),
  },
};
