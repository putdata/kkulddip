/* eslint-disable @typescript-eslint/no-explicit-any */

import type { Meta, StoryObj } from '@storybook/react-vite';
import MyPageStatCard from './MyPageStatCard';

const meta: Meta<typeof MyPageStatCard> = {
  title: 'Components/pages/My/MyPageStatCard',
  component: MyPageStatCard,
};

export default meta;

type Story = StoryObj<typeof MyPageStatCard>;

export const MockedStatCard: Story = {
  args: {
    customerId: 1,
    useMyPageStatsHook: ((customerId: number) => {
      void customerId;
      return {
        data: { totalOrder: 99, level: 'Platinum' },
        isLoading: false,
        isError: false,
        isSuccess: true,
        isFetched: true,
        refetch: async () => ({}),
        status: 'success',
        fetchStatus: 'idle',
      };
    }) as any,
  },
};

export const LoadingStatCard: Story = {
  args: {
    customerId: 2,
    useMyPageStatsHook: (() => ({
      data: null,
      isLoading: true,
      isError: false,
    })) as any,
  },
};

export const ErrorStatCard: Story = {
  args: {
    customerId: 3,
    useMyPageStatsHook: (() => ({
      data: null,
      isLoading: false,
      isError: true,
    })) as any,
  },
};
