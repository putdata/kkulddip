import type { Meta, StoryObj } from '@storybook/react-vite';
import LoginDrawer from './LoginDrawer';
import { MemoryRouter } from 'react-router-dom';

const meta: Meta<typeof LoginDrawer> = {
  title: 'Components/pages/Login/LoginDrawer',
  component: LoginDrawer,
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

type Story = StoryObj<typeof LoginDrawer>;

export const Default: Story = {
  args: {},
};
