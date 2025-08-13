interface Notification {
  id: number;
  title: string;
  message: string;
  type: 'order' | 'event' | 'marketing' | 'system' | 'review' | 'pickup';
  createdAt: string;
}

export type { Notification };
