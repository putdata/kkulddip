import type { Meta, StoryObj } from '@storybook/react-vite';
import ProductCard from './ProductCard';
import type { Product } from '@/types/cart';

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

const defaultProduct: Product = {
  id: 1,
  name: '[으뜸] 김치삼겹구이',
  price: 29900,
  description: '김치와 삼겹살이 어우러진 최고의 맛',
  image:
    'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"%3E%3Crect width="48" height="48" fill="%23ff6b35"/%3E%3Cpath d="M12 16h24v16c0 2-2 4-4 4H16c-2 0-4-2-4-4V16z" fill="%23fff"/%3E%3Crect x="16" y="20" width="16" height="2" fill="%23ff6b35"/%3E%3Crect x="16" y="24" width="12" height="2" fill="%23ff6b35"/%3E%3C/svg%3E',
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
