import type { Meta, StoryObj } from '@storybook/react-vite';
import LoginCarousel from './LoginCarousel';

const meta: Meta<typeof LoginCarousel> = {
  title: 'Components/pages/LoginCarousel',
  component: LoginCarousel,
  parameters: {
    layout: 'centered',
  },
};

export default meta;

type Story = StoryObj<typeof LoginCarousel>;

export const Default: Story = {
  args: {},
};
