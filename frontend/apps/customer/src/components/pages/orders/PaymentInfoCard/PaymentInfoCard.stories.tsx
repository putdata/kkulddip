import type { Meta, StoryObj } from '@storybook/react-vite';
import PaymentInfoCard from './PaymentInfoCard';

const meta: Meta<typeof PaymentInfoCard> = {
  title: 'Components/PaymentInfoCard',
  component: PaymentInfoCard,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    paymentInfo: {
      totalOriginalPrice: 18800,
      totalDiscount: 6500,
      discounts: [
        {
          discountHistoryId: 1,
          discountAmount: 4100,
          discountType: 'MEMBERSHIP',
        },
        {
          discountHistoryId: 2,
          discountAmount: 1500,
          discountType: 'IMMEDIATE',
        },
        {
          discountHistoryId: 3,
          discountAmount: 900,
          discountType: 'COUPON',
        },
      ],
      totalItems: 3,
      paymentMethod: '토스페이',
    },
  },
};

export const SingleDiscount: Story = {
  args: {
    paymentInfo: {
      totalOriginalPrice: 15000,
      totalDiscount: 2000,
      discounts: [
        {
          discountHistoryId: 1,
          discountAmount: 2000,
          discountType: 'MEMBERSHIP',
        },
      ],
      totalItems: 2,
      paymentMethod: '카카오페이',
    },
  },
};

export const NoDiscount: Story = {
  args: {
    paymentInfo: {
      totalOriginalPrice: 12000,
      totalDiscount: 0,
      discounts: [],
      totalItems: 1,
      paymentMethod: '신용카드',
    },
  },
};
