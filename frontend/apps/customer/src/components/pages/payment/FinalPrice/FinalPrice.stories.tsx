import type { Meta, StoryObj } from '@storybook/react-vite';
import FinalPrice from './FinalPrice';

const meta: Meta<typeof FinalPrice> = {
  title: 'components/pages/Payment/FinalPrice',
  component: FinalPrice,
  tags: ['autodocs'],
  argTypes: {
    orderAmount: {
      control: { type: 'number' },
      defaultValue: 29900,
      description: '상품 총 금액 (원)',
    },
    discount: {
      control: { type: 'number' },
      defaultValue: 3000,
      description: '할인 금액 (원)',
    },
  },
};

export default meta;

type Story = StoryObj<typeof FinalPrice>;

export const Default: Story = {
  args: {
    orderAmount: 29900,
    discount: 3000,
  },
};

export const NoDiscount: Story = {
  args: {
    orderAmount: 29900,
    discount: 0,
  },
};

export const FullDiscount: Story = {
  args: {
    orderAmount: 29900,
    discount: 29900,
  },
};
