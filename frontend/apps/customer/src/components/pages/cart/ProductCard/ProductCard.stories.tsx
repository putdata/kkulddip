import type { Meta, StoryObj } from '@storybook/react-vite';
import ProductCard from './ProductCard';
import type { CartItem } from '@/types/cart';

const meta = {
  title: 'components/pages/cart/ProductCard',
  component: ProductCard,
  parameters: {
    layout: 'padded',
  },
  tags: ['autodocs'],
} satisfies Meta<typeof ProductCard>;

export default meta;
type Story = StoryObj<typeof meta>;

const defaultProduct: CartItem = {
  ddipboxId: 1,
  name: '[으뜸] 김치삼겹구이',
  price: 29900,
  description: '김치와 삼겹살이 어우러진 최고의 맛',
  quantity: 0,
  discountRate: 0,
  storeId: 0,
};

export const Default: Story = {
  args: {
    product: defaultProduct,
  },
};

export const LongProductName: Story = {
  args: {
    product: {
      ...defaultProduct,
      name: '[프리미엄] 최고급 한우 김치삼겹구이 세트메뉴',
    },
  },
};

export const LongDescription: Story = {
  args: {
    product: {
      ...defaultProduct,
      description:
        '신선한 김치와 두툼한 삼겹살이 어우러진 최고의 맛으로, 오랜 전통의 비법 양념으로 숙성시켜 더욱 깊은 풍미를 자랑합니다',
    },
  },
};

export const HighPrice: Story = {
  args: {
    product: {
      ...defaultProduct,
      price: 89900,
      name: '[특선] 한우 김치삼겹구이',
    },
  },
};
