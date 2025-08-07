import type { Meta, StoryObj } from '@storybook/react-vite';
import OrderCard from './OrderCard';

const meta: Meta<typeof OrderCard> = {
  title: 'Components/OrderCard',
  component: OrderCard,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    item: {
      storeInfo: {
        storeName: '맛있는 치킨집',
        description: '신선한 재료로 만든 맛있는 치킨',
        ratingAverage: 4.5,
      },
      img: {
        src: 'https://via.placeholder.com/48x48?text=🍗',
        alt: '치킨 이미지',
      },
      price: {
        original: 25000,
        discount: 20000,
        discountAmount: 5000,
      },
      date: '2024-01-15',
      items: [{ name: '후라이드 치킨 띱박스', quantity: 1 }],
      isFavorited: true,
      orderId: 1,
    },
  },
};
