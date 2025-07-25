import type { Meta, StoryObj } from '@storybook/react-vite';
import FoodCard from './FoodCard';

const meta: Meta<typeof FoodCard> = {
  title: 'Components/FoodCard',
  component: FoodCard,
  parameters: {
    layout: 'padded',
  },
  argTypes: {
    onClick: {
      action: 'clicked',
    },
  },
};

export default meta;
type Story = StoryObj<typeof FoodCard>;

// 기본 스토리 - 모든 정보가 포함된 완전한 상태
export const Default: Story = {
  args: {
    food: {
      storeInfo: {
        storeName: '맛있는 김밥천국',
        description: '신선한 재료로 만든 김밥과 분식',
        ratingAverage: 4.5,
      },

      img: {
        src: 'https://images.unsplash.com/photo-1553909489-cd47e0ef937f?w=400&h=300&fit=crop&crop=center',
        alt: '김밥 이미지',
      },
      price: {
        original: 5000,
        discount: 3500,
      },
      distance: 0.8,
      timeLeftHour: 2,
      remainingQuantity: 5,
    },
  },
};

// 시간 제한 없는 스토리 - 선택적 필드들이 없는 상태
export const NoTimeLimit: Story = {
  args: {
    food: {
      storeInfo: {
        storeName: '24시 편의점',
        description: '언제나 신선한 도시락과 간편식',
        ratingAverage: 4.2,
      },

      img: {
        src: 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=300&fit=crop&crop=center',
        alt: '편의점 도시락',
      },
      price: {
        original: 8000,
        discount: 6000,
      },
      distance: 1.5,
      remainingQuantity: 10,
    },
  },
};
