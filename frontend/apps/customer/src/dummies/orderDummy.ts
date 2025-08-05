import type { OrderFoodItem } from '@/types/orderFood';

/**
 * 주문 내역 더미 데이터
 */
export const orderDummyData: OrderFoodItem[] = [
  {
    orderId: 1001,
    date: '2024-12-15 18:30',
    storeInfo: {
      storeName: '도미노피자 역삼점',
      description: '피자 전문점',
      ratingAverage: 4.3,
    },
    img: {
      src: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1yvwQMHhsxp4yIDOFWV8oNa_hOzRGUKOklw&s',
      alt: '도미노피자 이미지',
    },
    price: {
      original: 32000,
      discount: 19200,
      discountAmount: 12800,
    },
    items: [
      { name: '페퍼로니 피자', quantity: 1 },
      { name: '콜라 1.25L', quantity: 1 },
    ],
    isFavorited: true,
  },
  {
    orderId: 1002,
    date: '2024-12-14 12:15',
    storeInfo: {
      storeName: '교촌치킨 논현점',
      description: '치킨 전문점',
      ratingAverage: 4.6,
    },
    img: {
      src: 'https://i.namu.wiki/i/vHW7yi9UN0Tulgps-xtq1qB2L7VZvx-34qaERfEMuy82SifcZgAEvJ950AI10sNt2cKgWsxm9EyJggJ1fCJdHQ.webp',
      alt: '교촌치킨 이미지',
    },
    price: {
      original: 24000,
      discount: 20400,
      discountAmount: 3600,
    },
    items: [
      { name: '허니콤보 반마리', quantity: 1 },
      { name: '레드콤보 반마리', quantity: 1 },
    ],
    isFavorited: false,
  },
  {
    orderId: 1003,
    date: '2024-12-13 19:45',
    storeInfo: {
      storeName: '버거킹 강남역점',
      description: '패스트푸드',
      ratingAverage: 4.1,
    },
    img: {
      src: 'https://mob-prd.burgerking.co.kr/images/menu/web/main/2025/01/06/c8bd99f7-0ce8-457c-8824-4360d6b08d82.png',
      alt: '버거킹 이미지',
    },
    price: {
      original: 18000,
      discount: 11900,
      discountAmount: 6100,
    },
    items: [
      { name: '와퍼 세트', quantity: 1 },
      { name: '너겟킹 6조각', quantity: 1 },
    ],
    isFavorited: true,
  },
  {
    orderId: 1004,
    date: '2024-12-12 13:20',
    storeInfo: {
      storeName: '맘스터치 신사점',
      description: '수제버거 전문점',
      ratingAverage: 4.4,
    },
    img: {
      src: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"%3E%3Crect width="48" height="48" fill="%23ff6b35"/%3E%3Cpath d="M12 16h24v16c0 2-2 4-4 4H16c-2 0-4-2-4-4V16z" fill="%23fff"/%3E%3Crect x="16" y="20" width="16" height="2" fill="%23ff6b35"/%3E%3Crect x="16" y="24" width="12" height="2" fill="%23ff6b35"/%3E%3C/svg%3E',
      alt: '맘스터치 이미지',
    },
    price: {
      original: 15000,
      discount: 12000,
      discountAmount: 3000,
    },
    items: [{ name: '싸이버거 세트', quantity: 1 }],
    isFavorited: false,
  },
  {
    orderId: 1005,
    date: '2024-12-11 20:10',
    storeInfo: {
      storeName: '최고집 김치삼겹구이',
      description: '한식 전문점',
      ratingAverage: 4.7,
    },
    img: {
      src: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"%3E%3Crect width="48" height="48" fill="%23d2691e"/%3E%3Cpath d="M8 20h32v8c0 2-2 4-4 4H12c-2 0-4-2-4-4v-8z" fill="%23fff"/%3E%3Crect x="12" y="22" width="24" height="1" fill="%23d2691e"/%3E%3Crect x="12" y="25" width="20" height="1" fill="%23d2691e"/%3E%3C/svg%3E',
      alt: '김치삼겹구이 이미지',
    },
    price: {
      original: 35000,
      discount: 28000,
      discountAmount: 7000,
    },
    items: [
      { name: '[으뜸] 김치삼겹구이', quantity: 1 },
      { name: '공기밥', quantity: 2 },
    ],
    isFavorited: true,
  },
  {
    orderId: 1006,
    date: '2024-12-10 11:30',
    storeInfo: {
      storeName: '스타벅스 역삼점',
      description: '커피 전문점',
      ratingAverage: 4.2,
    },
    img: {
      src: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"%3E%3Ccircle cx="24" cy="24" r="20" fill="%23006241"/%3E%3Cpath d="M24 8c-8.8 0-16 7.2-16 16s7.2 16 16 16 16-7.2 16-16S32.8 8 24 8z" fill="%23fff"/%3E%3Ccircle cx="24" cy="24" r="8" fill="%23006241"/%3E%3C/svg%3E',
      alt: '스타벅스 이미지',
    },
    price: {
      original: 12000,
      discount: 10200,
      discountAmount: 1800,
    },
    items: [
      { name: '아메리카노 Tall', quantity: 2 },
      { name: '크로와상', quantity: 1 },
    ],
    isFavorited: false,
  },
  {
    orderId: 1007,
    date: '2024-12-09 16:40',
    storeInfo: {
      storeName: '청년피자 선릉점',
      description: '가성비 피자',
      ratingAverage: 4.0,
    },
    img: {
      src: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"%3E%3Ccircle cx="24" cy="24" r="20" fill="%23ff4757"/%3E%3Cpath d="M24 8 L36 36 L12 36 Z" fill="%23ffa502"/%3E%3Ccircle cx="20" cy="28" r="2" fill="%23ff4757"/%3E%3Ccircle cx="28" cy="28" r="2" fill="%23ff4757"/%3E%3C/svg%3E',
      alt: '청년피자 이미지',
    },
    price: {
      original: 22000,
      discount: 15400,
      discountAmount: 6600,
    },
    items: [
      { name: '청년 콤비네이션 피자', quantity: 1 },
      { name: '치킨무', quantity: 1 },
    ],
    isFavorited: true,
  },
  {
    orderId: 1008,
    date: '2024-12-08 14:25',
    storeInfo: {
      storeName: '신전떡볶이 테헤란점',
      description: '떡볶이 전문점',
      ratingAverage: 4.5,
    },
    img: {
      src: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"%3E%3Crect width="48" height="48" fill="%23e74c3c"/%3E%3Cpath d="M8 16h32v16c0 2-2 4-4 4H12c-2 0-4-2-4-4V16z" fill="%23fff"/%3E%3Crect x="12" y="20" width="6" height="8" rx="1" fill="%23e74c3c"/%3E%3Crect x="20" y="18" width="6" height="12" rx="1" fill="%23e74c3c"/%3E%3Crect x="28" y="22" width="6" height="6" rx="1" fill="%23e74c3c"/%3E%3C/svg%3E',
      alt: '신전떡볶이 이미지',
    },
    price: {
      original: 16000,
      discount: 13600,
      discountAmount: 2400,
    },
    items: [
      { name: '신전떡볶이 2인분', quantity: 1 },
      { name: '튀김 모듬', quantity: 1 },
      { name: '김밥', quantity: 1 },
    ],
    isFavorited: false,
  },
  {
    orderId: 1009,
    date: '2024-12-07 19:15',
    storeInfo: {
      storeName: '굽네치킨 대치점',
      description: '오븐구이 치킨',
      ratingAverage: 4.3,
    },
    img: {
      src: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"%3E%3Cellipse cx="24" cy="24" rx="18" ry="20" fill="%23ff6b35"/%3E%3Cellipse cx="24" cy="22" rx="12" ry="14" fill="%23fff"/%3E%3Ccircle cx="20" cy="18" r="2" fill="%23000"/%3E%3Ccircle cx="28" cy="18" r="2" fill="%23000"/%3E%3Cpath d="M22 25 L26 25 L24 28 Z" fill="%23ff6b35"/%3E%3C/svg%3E',
      alt: '굽네치킨 이미지',
    },
    price: {
      original: 25000,
      discount: 21250,
      discountAmount: 3750,
    },
    items: [
      { name: '고추바사삭 반마리', quantity: 1 },
      { name: '갈릭소이 반마리', quantity: 1 },
    ],
    isFavorited: true,
  },
  {
    orderId: 1010,
    date: '2024-12-06 12:50',
    storeInfo: {
      storeName: '써브웨이 강남점',
      description: '샌드위치 전문점',
      ratingAverage: 4.1,
    },
    img: {
      src: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"%3E%3Crect width="48" height="48" fill="%23009639"/%3E%3Cpath d="M8 20h32v8c0 2-2 4-4 4H12c-2 0-4-2-4-4v-8z" fill="%23fff"/%3E%3Crect x="12" y="22" width="24" height="2" fill="%23ffd700"/%3E%3Crect x="12" y="25" width="20" height="1" fill="%23ff4500"/%3E%3C/svg%3E',
      alt: '써브웨이 이미지',
    },
    price: {
      original: 14000,
      discount: 11900,
      discountAmount: 2100,
    },
    items: [
      { name: 'B.M.T 샌드위치', quantity: 1 },
      { name: '쿠키', quantity: 2 },
    ],
    isFavorited: false,
  },
];
