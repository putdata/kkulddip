import type { Meta, StoryObj } from '@storybook/react-vite';
import NavigationBar from '@/components/common/NavigationBar/NavigationBar';
import { MemoryRouter } from 'react-router-dom';

const meta: Meta<typeof NavigationBar> = {
  title: 'Components/common/NavigationBar',
  component: NavigationBar,
  decorators: [
    Story => (
      <MemoryRouter>
        <Story />
      </MemoryRouter>
    ),
  ],
  parameters: {
    layout: 'fullscreen',
  },
};

export default meta;

type Story = StoryObj<typeof NavigationBar>;

export const Default: Story = {
  args: {},
};
