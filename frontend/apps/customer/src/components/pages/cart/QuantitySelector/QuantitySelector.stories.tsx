import type { Meta, StoryObj } from '@storybook/react-vite';
import QuantitySelector from './QuantitySelector';

const meta = {
  title: 'components/pages/cart/QuantitySelector',
  component: QuantitySelector,
  parameters: {
    layout: 'padded',
  },
  tags: ['autodocs'],
  argTypes: {
    onQuantityChange: { action: 'quantity changed' },
    quantity: {
      control: { type: 'number', min: 1, max: 10 },
    },
    initialQuantity: {
      control: { type: 'number', min: 1, max: 5 },
    },
  },
} satisfies Meta<typeof QuantitySelector>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    quantity: 1,
    onQuantityChange: (change: number) =>
      console.log('quantity changed:', change),
    initialQuantity: 1,
    label: '수량',
  },
};

export const MultipleItems: Story = {
  args: {
    quantity: 3,
    onQuantityChange: (change: number) =>
      console.log('quantity changed:', change),
    initialQuantity: 1,
    label: '수량',
  },
};

export const MinimumQuantity: Story = {
  args: {
    quantity: 1,
    onQuantityChange: (change: number) =>
      console.log('quantity changed:', change),
    initialQuantity: 1,
    label: '수량',
  },
};

export const CustomMinimum: Story = {
  args: {
    quantity: 2,
    onQuantityChange: (change: number) =>
      console.log('quantity changed:', change),
    initialQuantity: 2,
    label: '최소 주문 수량',
  },
};
