import type { Meta, StoryObj } from '@storybook/react-vite';
import PickUpStatusCard from './PickUpStatusCard';

const meta: Meta<typeof PickUpStatusCard> = {
  title: 'Components/pages/Order/PickUpStatusCard',
  component: PickUpStatusCard,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const InProgress: Story = {
  args: {
    item: {
      orderId: 12345,
      status: 'IN_PROGRESS',
      createdAt: '2024-01-15 14:30',
    },
  },
};

export const Completed: Story = {
  args: {
    item: {
      orderId: 67890,
      status: 'COMPLETED',
      createdAt: '2024-01-15 14:30',
      pickupCompletedAt: '2024-01-15 15:45',
    },
  },
};
