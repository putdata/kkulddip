import type { Meta, StoryObj } from '@storybook/react-vite';
import PaymentButton from './PaymentButton';

const meta = {
  title: 'components/pages/cart/PaymentButton',
  component: PaymentButton,
  parameters: {
    layout: 'fullscreen',
  },
  tags: ['autodocs'],
  argTypes: {
    onNext: { action: 'payment clicked' },
  },
} satisfies Meta<typeof PaymentButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    total: 29900,
    buttonSuffix: '결제하기',
    onNext: () => console.log('payment clicked'),
  },
};

export const HighAmount: Story = {
  args: {
    total: 89700,
    buttonSuffix: '결제하기',
    onNext: () => console.log('payment clicked'),
  },
};

export const CustomButtonText: Story = {
  args: {
    total: 29900,
    buttonSuffix: '주문하기',
    onNext: () => console.log('payment clicked'),
  },
};

export const VeryHighAmount: Story = {
  args: {
    total: 1299000,
    buttonSuffix: '결제하기',
    onNext: () => console.log('payment clicked'),
  },
};
