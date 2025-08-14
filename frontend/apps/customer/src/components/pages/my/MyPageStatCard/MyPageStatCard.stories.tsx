import type { Meta, StoryObj } from '@storybook/react-vite';
import MyPageStatCard from './MyPageStatCard';

const meta: Meta<typeof MyPageStatCard> = {
  title: 'Components/pages/My/MyPageStatCard',
  component: MyPageStatCard,
};

export default meta;

type Story = StoryObj<typeof MyPageStatCard>;

export const Default: Story = {
  args: {
    data: {
      totalOrder: 15,
      level: 'Gold',
    },
    isLoading: false,
    isError: false,
  },
};

export const Loading: Story = {
  args: {
    isLoading: true,
    isError: false,
    data: undefined,
  },
};

export const Error: Story = {
  args: {
    isLoading: false,
    isError: true,
    data: undefined,
  },
};
