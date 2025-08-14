import type { Meta, StoryObj } from '@storybook/react-vite';
import ProfileSection from './ProfileSection';

const meta: Meta<typeof ProfileSection> = {
  title: 'Components/ProfileSection',
  component: ProfileSection,
  parameters: {
    layout: 'centered',
    backgrounds: {
      default: 'gray',
      values: [
        { name: 'gray', value: '#f9fafb' },
        { name: 'white', value: '#ffffff' },
      ],
    },
  },
  tags: ['autodocs'],
  argTypes: {
    profile: {
      control: 'object',
      description: '사용자 프로필 데이터',
    },
  },
  decorators: [
    Story => (
      <div className="max-w-md">
        <Story />
      </div>
    ),
  ],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    profile: {
      name: '홍길동',
      level: 'WORKER_BEE',
      orderCount: 127,
      points: 2450,
      couponCount: 5,
      co2: 15.5,
    },
  },
};
