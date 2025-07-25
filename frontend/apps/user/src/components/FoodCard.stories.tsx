import FoodCard from './FoodCard';
import type { Meta, StoryObj } from '@storybook/react-vite';

// 필수: Meta 설정 (default export)
const meta = {
  title: 'UI/FoodCard',
  component: FoodCard,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    title: {
      control: { type: 'text' },
      description: '음식점 이름',
    },
    description: {
      control: { type: 'text' },
      description: '메뉴 설명',
    },
    image: {
      control: { type: 'object' },
      description: '이미지 정보 (url, alt)',
    },
    price: {
      control: { type: 'object' },
      description: '가격 정보 (original, discounted, discountPercent)',
    },
    location: {
      control: { type: 'object' },
      description: '위치 정보 (rating, distance)',
    },
    stock: {
      control: { type: 'object' },
      description: '재고 정보 (timeLeft, remainingQuantity) - 선택사항',
    },
  },
} satisfies Meta<typeof FoodCard>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    title: '교촌치킨 선릉점',
    description: '허니콤보, 레드콤보',
    image: {
      url: 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=400&h=300&fit=crop',
      alt: '치킨',
    },
    price: {
      original: 28000,
      discounted: 16800,
      discountPercent: 40,
    },
    location: {
      rating: 4.7,
      distance: 0.5,
    },
    stock: {
      timeLeft: 1,
      remainingQuantity: 2,
    },
  },
};

export const Chicken: Story = {
  args: {
    title: '교촌치킨 선릉점',
    description: '허니콤보, 레드콤보',
    image: {
      url: 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=400&h=300&fit=crop',
      alt: '치킨',
    },
    price: {
      original: 28000,
      discounted: 16800,
      discountPercent: 40,
    },
    location: {
      rating: 4.7,
      distance: 0.5,
    },
    stock: {
      timeLeft: 1,
      remainingQuantity: 2,
    },
  },
};

export const Burger: Story = {
  args: {
    title: '맘스터치 강남점',
    description: '싸이버거, 치킨버거 세트',
    image: {
      url: 'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop',
      alt: '햄버거 세트',
    },
    price: {
      original: 15000,
      discounted: 9000,
      discountPercent: 40,
    },
    location: {
      rating: 4.5,
      distance: 1,
    },
    stock: {
      timeLeft: 2,
      remainingQuantity: 3,
    },
  },
};

export const HighDiscount: Story = {
  args: {
    title: '피자헛 테헤란점',
    description: '슈퍼슈프림, 치즈크러스트',
    image: {
      url: 'https://images.unsplash.com/photo-1534308983923-353928d96d0d?w=400&h=300&fit=crop',
      alt: '피자',
    },
    price: {
      original: 35000,
      discounted: 17500,
      discountPercent: 50,
    },
    location: {
      rating: 4.2,
      distance: 1.5,
    },
    stock: {
      timeLeft: 1,
      remainingQuantity: 1,
    },
  },
};

export const Korean: Story = {
  args: {
    title: '한솥도시락 역삼점',
    description: '제육볶음, 불고기 도시락',
    image: {
      url: 'https://images.unsplash.com/photo-1579952363873-27d3bfad9c0d?w=400&h=300&fit=crop',
      alt: '한식 도시락',
    },
    price: {
      original: 6000,
      discounted: 4500,
      discountPercent: 25,
    },
    location: {
      rating: 4.4,
      distance: 0.8,
    },
    stock: {
      timeLeft: 4,
      remainingQuantity: 10,
    },
  },
};

// 재고 정보가 없는 경우의 예시
export const WithoutStock: Story = {
  args: {
    title: '서브웨이 강남점',
    description: '터키베이컨클럽, 치킨데리야끼 세트',
    image: {
      url: 'https://images.unsplash.com/photo-1520072959219-c595dc870360?w=400&h=300&fit=crop',
      alt: '서브웨이 샌드위치',
    },
    price: {
      original: 12000,
      discounted: 8400,
      discountPercent: 30,
    },
    location: {
      rating: 4.3,
      distance: 0.7,
    },
    // stock 정보 없음 (선택사항이므로 제외 가능)
  },
};
