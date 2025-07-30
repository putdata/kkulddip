// GoogleLoginButton.stories.tsx
import type { Meta, StoryObj } from '@storybook/react-vite';
import GoogleLoginButton from './GoogleLoginButton';
import { MemoryRouter } from 'react-router-dom';

const meta: Meta<typeof GoogleLoginButton> = {
  title: 'Components/pages/GoogleLoginButton',
  component: GoogleLoginButton,
  decorators: [
    Story => (
      <MemoryRouter>
        <Story />
      </MemoryRouter>
    ),
  ],
  parameters: {
    layout: 'centered',
  },
};

export default meta;

type Story = StoryObj<typeof GoogleLoginButton>;

export const Default: Story = {};
