import type { Meta, StoryObj } from '@storybook/react';
import SignupCard from './SignupCard';
import { MemoryRouter } from 'react-router-dom';

const meta: Meta<typeof SignupCard> = {
  title: 'Components/pages/SignupCard',
  component: SignupCard,
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

type Story = StoryObj<typeof SignupCard>;

export const Default: Story = {
  args: {},
};
