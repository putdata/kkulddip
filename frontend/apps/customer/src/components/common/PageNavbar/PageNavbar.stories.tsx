import type { Meta, StoryObj } from '@storybook/react-vite';
import PageNavbar from './PageNavbar';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';

const meta: Meta<typeof PageNavbar> = {
  title: 'Components/common/PageNavbar',
  component: PageNavbar,
  decorators: [
    Story => (
      <MemoryRouter initialEntries={[ROUTE_PATH.LIKE]}>
        <Routes>
          <Route path={ROUTE_PATH.LIKE} element={<Story />} />
        </Routes>
      </MemoryRouter>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof PageNavbar>;

export const LikePage: Story = {};
