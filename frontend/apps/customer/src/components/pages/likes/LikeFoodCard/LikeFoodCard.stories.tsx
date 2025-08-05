import type { Meta, StoryObj } from '@storybook/react-vite';
import LikeFoodCard from './LikeFoodCard';
import type { CardItemProps } from '@/types/likeFoodCard';

const meta: Meta<typeof LikeFoodCard> = {
  title: 'Components/pages/Likes/LikeFoodCard',
  component: LikeFoodCard,
  tags: ['autodocs'],
};

export default meta;

type Story = StoryObj<typeof LikeFoodCard>;

const baseItem: CardItemProps['item'] = {
  storeInfo: {
    storeName: '도미노피자 역삼점',
    description: '페퍼로니, 불고기 피자',
    ratingAverage: 4.3,
  },
  img: {
    src: 'https://media.istockphoto.com/id/1442417585/ko/%EC%82%AC%EC%A7%84/%EC%B9%98%EC%A6%88-%ED%8E%98%ED%8D%BC%EB%A1%9C%EB%8B%88-%ED%94%BC%EC%9E%90-%ED%95%9C-%EC%A1%B0%EA%B0%81%EC%9D%84-%EB%A8%B9%EB%8A%94-%EC%82%AC%EB%9E%8C.jpg?s=612x612&w=0&k=20&c=SuuCw0tUAhTwszDbwyKl7XEKE7Y_uDjwGHNrxIKSSUw=',
    alt: '음식 이미지',
  },
  price: {
    original: 22000,
    discount: 13200,
  },
  timeLeftHour: 3,
  discountRate: 40,
  distance: 0.7,
  remainingQuantity: 5,
};

export const Default: Story = {
  args: {
    item: baseItem,
  },
};

export const NoDiscount: Story = {
  args: {
    item: {
      ...baseItem,
      price: { original: undefined, discount: 15000 },
      discountRate: undefined,
    },
  },
};

export const NoTimeLeft: Story = {
  args: {
    item: {
      ...baseItem,
      timeLeftHour: undefined,
    },
  },
};

export const LowStock: Story = {
  args: {
    item: {
      ...baseItem,
      remainingQuantity: 1,
    },
  },
};

export const NoBadges: Story = {
  args: {
    item: {
      ...baseItem,
      timeLeftHour: undefined,
      discountRate: undefined,
      remainingQuantity: undefined,
    },
  },
};
