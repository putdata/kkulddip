interface Notification {
  id: string;
  title: string;
  message: string;
  type: 'order' | 'promotion' | 'system' | 'review' | 'pickup';
  isRead: boolean;
  createdAt: string;
}

export type { Notification };
