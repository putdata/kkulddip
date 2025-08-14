import type { Notification } from '@/types/notification';

export const dummyNotifications: Notification[] = [
  {
    id: 1,
    title: '주문이 접수되었습니다',
    message:
      '맛있는 김치찌개 주문이 성공적으로 접수되었습니다. 예상 픽업 시간은 20분입니다.',
    type: 'order',
    createdAt: '2024-01-15T14:30:00',
  },
  {
    id: 2,
    title: '새로운 할인 이벤트',
    message: '이번 주말 전체 메뉴 20% 할인! 놓치지 마세요.',
    type: 'marketing',
    createdAt: '2024-01-14T10:00:00',
  },
  {
    id: 3,
    title: '픽업이 완료되었습니다',
    message: '주문하신 음식이 픽업 완료되었습니다. 맛있게 드세요!',
    type: 'pickup',
    createdAt: '2024-01-13T19:45:00',
  },
  {
    id: 4,
    title: '리뷰 작성 요청',
    message: '최근 주문하신 음식은 어떠셨나요? 리뷰를 작성해주세요.',
    type: 'review',
    createdAt: '2024-01-12T15:20:00',
  },
];
