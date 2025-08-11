import type { Meta, StoryObj } from '@storybook/react-vite';
import NotificationCard from './NotificationCard';

const meta = {
  title: 'Components/Notification/NotificationCard',
  component: NotificationCard,
  parameters: {
    layout: 'fullscreen',
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: 'select',
      options: ['order', 'promotion', 'system', 'review', 'pickup'],
    },
    isRead: {
      control: 'boolean',
    },
    onClick: { action: 'clicked' },
  },
} satisfies Meta<typeof NotificationCard>;

export default meta;
type Story = StoryObj<typeof meta>;

export const OrderUnread: Story = {
  args: {
    id: '1',
    title: '주문이 접수되었습니다',
    message:
      '맛있는 김치찌개 주문이 성공적으로 접수되었습니다. 예상 픽업 시간은 20분입니다.',
    type: 'order',
    isRead: false,
    createdAt: '2024-01-15 14:30',
    onClick: () => {},
  },
};

export const OrderRead: Story = {
  args: {
    id: '1',
    title: '주문이 접수되었습니다',
    message:
      '맛있는 김치찌개 주문이 성공적으로 접수되었습니다. 예상 픽업 시간은 20분입니다.',
    type: 'order',
    isRead: true,
    createdAt: '2024-01-15 14:30',
    onClick: () => {},
  },
};

export const PromotionUnread: Story = {
  args: {
    id: '2',
    title: '새로운 할인 이벤트',
    message: '이번 주말 전체 메뉴 20% 할인! 놓치지 마세요.',
    type: 'promotion',
    isRead: false,
    createdAt: '2024-01-14 10:00',
    onClick: () => {},
  },
};

export const SystemRead: Story = {
  args: {
    id: '3',
    title: '시스템 점검 안내',
    message: '오늘 밤 12시부터 새벽 2시까지 시스템 점검이 있습니다.',
    type: 'system',
    isRead: true,
    createdAt: '2024-01-13 22:00',
    onClick: () => {},
  },
};

export const ReviewUnread: Story = {
  args: {
    id: '4',
    title: '리뷰 작성 요청',
    message: '최근 주문하신 음식은 어떠셨나요? 리뷰를 작성해주세요.',
    type: 'review',
    isRead: false,
    createdAt: '2024-01-12 15:20',
    onClick: () => {},
  },
};

export const LongMessage: Story = {
  args: {
    id: '5',
    title: '매우 긴 메시지가 있는 알림',
    message:
      '이것은 매우 긴 알림 메시지입니다. 알림 메시지가 길어질 때 UI가 어떻게 표시되는지 확인하기 위한 테스트용 메시지입니다. 여러 줄에 걸쳐 표시되어야 하며, 적절한 line-height와 spacing이 적용되어야 합니다.',
    type: 'order',
    isRead: false,
    createdAt: '2024-01-11 09:15',
    onClick: () => {},
  },
};
