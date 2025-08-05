import type { Meta, StoryObj } from '@storybook/react-vite';
import PickupInfo from './PickupInfo';

const meta: Meta<typeof PickupInfo> = {
  title: 'components/pages/Payment/PickupInfo',
  component: PickupInfo,
  tags: ['autodocs'],
  argTypes: {
    storeName: {
      control: 'text',
      defaultValue: '최고집 김치삼겹구이&김치찜',
      description: '가게 이름',
    },
    address: {
      control: 'text',
      defaultValue: '서울시 강남구 테헤란로 123',
      description: '가게 주소',
    },
    pickupTime: {
      control: 'text',
      defaultValue: '15-20분',
      description: '예상 소요 시간',
    },
  },
};

export default meta;

type Story = StoryObj<typeof PickupInfo>;

export const Default: Story = {
  args: {
    storeName: '최고집 김치삼겹구이&김치찜',
    address: '서울시 강남구 테헤란로 123',
    pickupTime: '15-20분',
  },
};

export const DifferentLocation: Story = {
  args: {
    storeName: '화로명가 직화쭈꾸미',
    address: '부산광역시 해운대구 해변로 456',
    pickupTime: '10-15분',
  },
};
