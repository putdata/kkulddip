import type { Meta, StoryObj } from '@storybook/react-vite';
import OrderSummary from './OrderSummary';

const meta: Meta<typeof OrderSummary> = {
  title: 'components/pages/Payment/OrderSummary',
  component: OrderSummary,
  tags: ['autodocs'],
  argTypes: {
    productName: {
      control: 'text',
      defaultValue: '[으뜸] 김치삼겹구이',
      description: '상품 이름',
    },
    quantity: {
      control: { type: 'number', min: 1 },
      defaultValue: 1,
      description: '상품 수량',
    },
  },
};

export default meta;

type Story = StoryObj<typeof OrderSummary>;

export const Default: Story = {
  args: {
    productName: '[으뜸] 김치삼겹구이',
    quantity: 1,
  },
};

export const MultipleItems: Story = {
  args: {
    productName: '최고집 불고기 정식',
    quantity: 3,
  },
};

export const LongName: Story = {
  args: {
    productName:
      '불타는 불맛 매운 김치 삼겹불고기와 백김치 우동 콤보 정식 세트 (매운맛 주의)',
    quantity: 2,
  },
};
