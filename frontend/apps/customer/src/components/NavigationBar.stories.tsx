import type { Meta, StoryObj } from '@storybook/react';
import NavigationBar from './NavigationBar';
import { MemoryRouter } from 'react-router-dom';

const meta: Meta<typeof NavigationBar> = {
  title: 'Components/NavigationBar',
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
