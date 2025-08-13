import type { Meta, StoryObj } from '@storybook/react-vite';
import PaymentMethod from './PaymentMethod';

const meta: Meta<typeof PaymentMethod> = {
  title: 'components/pages/Payment/PaymentMethod',
  component: PaymentMethod,
  tags: ['autodocs'],
};

export default meta;

type Story = StoryObj<typeof PaymentMethod>;

export const Default: Story = {};
