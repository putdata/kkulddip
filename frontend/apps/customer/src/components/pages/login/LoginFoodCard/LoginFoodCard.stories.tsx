import type { Meta, StoryObj } from '@storybook/react';
import LoginFoodCard from './LoginFoodCard';

const meta: Meta<typeof LoginFoodCard> = {
  title: 'Components/pages/LoginFoodCard',
  component: LoginFoodCard,
  parameters: {
    layout: 'centered',
  },
  args: {
    item: {
      id: 1,
      name: '반찬',
      imageUrl: 'assets/banchan.jpg',
      description: '집밥이 그리울 때',
    },
  },
};

export default meta;

type Story = StoryObj<typeof LoginFoodCard>;

export const Default: Story = {};
