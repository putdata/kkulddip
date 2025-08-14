import { Store, Settings } from 'lucide-react';

export const settingsItems = [
  {
    id: 'store',
    label: '가게',
    icon: Store,
  },
  {
    id: 'general',
    label: '일반',
    icon: Settings,
  },
] as const;

export type SettingItemId = (typeof settingsItems)[number]['id'];

export const SETTING_IDS = {
  STORE: 'store',
  GENERAL: 'general',
} as const;
