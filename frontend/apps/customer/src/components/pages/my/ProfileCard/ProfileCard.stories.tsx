import type { Meta, StoryObj } from '@storybook/react-vite';
import ProfileCard from './ProfileCard';

const meta: Meta<typeof ProfileCard> = {
  title: 'Components/pages/My/ProfileCard',
  component: ProfileCard,
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof ProfileCard>;

export const Default: Story = {
  args: {
    name: '혜린',
    savedAmount: 12345,
    savedCO2: 6.7,
  },
};
