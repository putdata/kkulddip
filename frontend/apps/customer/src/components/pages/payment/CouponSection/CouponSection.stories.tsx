import type { Meta, StoryObj } from '@storybook/react-vite';
import CouponSection from './CouponSection';

const meta: Meta<typeof CouponSection> = {
  title: 'components/pages/Payment/CouponSection',
  component: CouponSection,
  tags: ['autodocs'],
  argTypes: {
    discountAmount: {
      control: { type: 'number' },
      description: '할인 금액 (숫자, 원 단위)',
      defaultValue: 3000,
    },
  },
};

export default meta;

type Story = StoryObj<typeof CouponSection>;

export const Default: Story = {
  args: {
    discountAmount: 3000,
  },
};
