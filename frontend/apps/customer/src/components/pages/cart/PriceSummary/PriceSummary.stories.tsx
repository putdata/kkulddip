import type { Meta, StoryObj } from '@storybook/react-vite';
import PriceSummary from './PriceSummary';

const meta = {
  title: 'components/pages/cart/PriceSummary',
  component: PriceSummary,
  parameters: {
    layout: 'padded',
  },
  tags: ['autodocs'],
} satisfies Meta<typeof PriceSummary>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    productName: '[으뜸] 김치삼겹구이',
    quantity: 1,
    total: 29900,
    totalLabel: '총 결제금액',
  },
};

export const MultipleItems: Story = {
  args: {
    productName: '[으뜸] 김치삼겹구이',
    quantity: 3,
    total: 89700,
    totalLabel: '총 결제금액',
  },
};

export const LongProductName: Story = {
  args: {
    productName: '[프리미엄] 최고급 한우 김치삼겹구이 세트메뉴',
    quantity: 2,
    total: 179800,
    totalLabel: '총 결제금액',
  },
};

export const HighPrice: Story = {
  args: {
    productName: '[특선] 한우 김치삼겹구이',
    quantity: 1,
    total: 89900,
    totalLabel: '최종 결제금액',
  },
};
