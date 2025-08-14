import type { Meta, StoryObj } from '@storybook/react-vite';
import FoodCard from './FoodCard';
import { dummyStoreData } from '@/constants/homeMockData';

const meta: Meta<typeof FoodCard> = {
  title: 'Components/common/FoodCard',
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
    store: dummyStoreData.body.content[0], // 첫 번째 매장 데이터 사용
  },
};

export const SecondStore: Story = {
  args: {
    store: dummyStoreData.body.content[1], // 두 번째 매장 데이터 사용
  },
};
