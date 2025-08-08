import type { Meta, StoryObj } from '@storybook/react-vite';
import OrderProcessingLoader from './OrderProcessingLoader';

const meta: Meta<typeof OrderProcessingLoader> = {
  title: 'Components/pages/payment/OrderProcessingLoader',
  component: OrderProcessingLoader,
  parameters: {
    layout: 'fullscreen',
  },
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  name: '기본',
};

export const DarkMode: Story = {
  name: '다크 모드',
  parameters: {
    backgrounds: {
      default: 'dark',
    },
  },
  decorators: [
    Story => (
      <div className="dark bg-gray-900 text-white">
        <Story />
      </div>
    ),
  ],
};
