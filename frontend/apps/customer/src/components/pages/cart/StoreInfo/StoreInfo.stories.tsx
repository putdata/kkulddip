// import type { Meta, StoryObj } from '@storybook/react-vite';
// import StoreInfo from './StoreInfo';
// import type { StoreInfo as StoreInfoType } from '@/types/cart';

// const meta = {
//   title: 'components/pages/cart/StoreInfo',
//   component: StoreInfo,
//   parameters: {
//     layout: 'padded',
//   },
//   tags: ['autodocs'],
// } satisfies Meta<typeof StoreInfo>;

// export default meta;
// type Story = StoryObj<typeof meta>;

// const defaultStore: StoreInfoType = {
//   name: '최고집 김치삼겹구이&김치찜',
//   pickupTime: '15-20분',
//   pickupType: '매장 픽업',
// };

// export const Default: Story = {
//   args: {
//     Store: defaultStore,
//     pickupTimePrefix: '픽업가능시간:',
//   },
// };

// export const LongStoreName: Story = {
//   args: {
//     Store: {
//       ...defaultStore,
//       name: '매우 긴 이름을 가진 최고급 프리미엄 김치삼겹구이&김치찜 전문점',
//     },
//     pickupTimePrefix: '픽업가능시간:',
//   },
// };

// export const DeliveryType: Story = {
//   args: {
//     Store: {
//       ...defaultStore,
//       pickupType: '배달 가능',
//       pickupTime: '30-40분',
//     },
//     pickupTimePrefix: '배달예상시간:',
//   },
// };
