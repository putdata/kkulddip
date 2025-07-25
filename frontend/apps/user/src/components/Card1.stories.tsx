import type { Meta, StoryObj } from '@storybook/react-vite';
import { Card1 } from './Card1';

// 메타 정보: 스토리북에서 이 컴포넌트를 어떻게 표시할지 설정
const meta: Meta<typeof Card1> = {
  title: 'Components/Card1', // 스토리북 사이드바에서 보일 이름
  component: Card1, // 사용할 컴포넌트
  parameters: {
    layout: 'centered', // 컴포넌트를 화면 중앙에 배치
  },
  tags: ['autodocs'], // 자동 문서화 활성화
};

export default meta;
type Story = StoryObj<typeof meta>;

// 기본 스토리: 가장 기본적인 상태의 컴포넌트
export const Default: Story = {
  // 현재 Card1 컴포넌트는 props가 없으므로 args는 비어있음
  args: {
    img: {
      src: 'https://picsum.photos/id/237/200/300',
      alt: '가게 이미지',
    },
    storeInfo: {
      name: '아우어 베이커리 강남점',
      menus: ['더티초코', '빨미까레'],
    },
    price: {
      original: 15000,
      discount: 4500,
    },
    pickupTime: {
      from: '18:00',
      to: '20:00',
    },
  },
};
