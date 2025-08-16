import type { Meta, StoryObj } from '@storybook/react-vite';
import OrderSummary from './OrderSummary';

const meta: Meta<typeof OrderSummary> = {
  title: 'components/pages/Payment/OrderSummary',
  component: OrderSummary,
  tags: ['autodocs'],
  argTypes: {
    orderItems: {
      control: 'object',
      description: '주문 아이템 배열',
    },
  },
};

export default meta;

type Story = StoryObj<typeof OrderSummary>;

export const Default: Story = {
  args: {
    orderItems: [
      {
        productId: 1,
        quantity: 1,
        unitPrice: 8900,
      },
    ],
  },
};

export const MultipleItems: Story = {
  args: {
    orderItems: [
      {
        productId: 1,
        quantity: 2,
        unitPrice: 8900,
      },
      {
        productId: 2,
        quantity: 1,
        unitPrice: 12500,
      },
      {
        productId: 3,
        quantity: 3,
        unitPrice: 6800,
      },
    ],
  },
};

export const LargeQuantity: Story = {
  args: {
    orderItems: [
      {
        productId: 1,
        quantity: 10,
        unitPrice: 8900,
      },
    ],
  },
};

export const ManyItems: Story = {
  args: {
    orderItems: [
      {
        productId: 1,
        quantity: 1,
        unitPrice: 8900,
      },
      {
        productId: 2,
        quantity: 2,
        unitPrice: 12500,
      },
      {
        productId: 3,
        quantity: 1,
        unitPrice: 6800,
      },
      {
        productId: 4,
        quantity: 1,
        unitPrice: 15000,
      },
      {
        productId: 5,
        quantity: 2,
        unitPrice: 9800,
      },
    ],
  },
};
