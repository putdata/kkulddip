// 가게 상세 페이지 Mock Data

export interface DdipBoxSummaryDto {
  ddipboxId: number;
  ddipboxName: string;
  category: string;
  originalPrice: number;
  salePrice: number;
  remainingQuantity: number;
  active: boolean;
}

export interface StoreDetailDto {
  storeId: number;
  ownerId: number;
  storeName: string;
  storeAddress: string;
  description: string;
  operatingHours: string;
  phoneNumber: string;
  ratingAverage: number;
  reviewCount: number;
  businessNumber: string;
  storeProfileImage: string;
  latitude: number;
  longitude: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  ddipBoxes: DdipBoxSummaryDto[];
}

// Mock Data
export const mockStoreDetail: StoreDetailDto = {
  storeId: 1,
  ownerId: 101,
  storeName: '맛있는 베이커리',
  storeAddress: '서울특별시 강남구 테헤란로 123, 1층',
  description:
    '신선한 재료로 매일 아침 만드는 수제 베이커리입니다. 건강하고 맛있는 빵을 합리적인 가격에 제공합니다.',
  operatingHours: '18:00-20:00',
  phoneNumber: '02-1234-5678',
  ratingAverage: 4.7,
  reviewCount: 156,
  businessNumber: '123-45-67890',
  storeProfileImage:
    'https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?q=80&w=764&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
  latitude: 37.5665,
  longitude: 126.978,
  active: true,
  createdAt: '2024-01-15T09:00:00',
  updatedAt: '2024-08-01T14:30:00',
  ddipBoxes: [
    {
      ddipboxId: 1001,
      ddipboxName: '아침 빵 세트',
      category: '빵류',
      originalPrice: 12000,
      salePrice: 8000,
      remainingQuantity: 5,
      active: true,
    },
    {
      ddipboxId: 1002,
      ddipboxName: '샐러드 & 샌드위치 박스',
      category: '샐러드',
      originalPrice: 15000,
      salePrice: 10000,
      remainingQuantity: 3,
      active: true,
    },
    {
      ddipboxId: 1003,
      ddipboxName: '디저트 모음',
      category: '디저트',
      originalPrice: 18000,
      salePrice: 12000,
      remainingQuantity: 0,
      active: false,
    },
  ],
};

// 추가 Mock Data (다양한 케이스)
export const mockStoreDetailList: StoreDetailDto[] = [
  mockStoreDetail,
  {
    storeId: 2,
    ownerId: 102,
    storeName: '신선마켓',
    storeAddress: '서울특별시 마포구 홍대입구로 456, B1층',
    description:
      '매일 새벽에 들어오는 신선한 채소와 과일을 저렴하게 판매합니다.',
    operatingHours: '06:00-22:00 (연중무휴)',
    phoneNumber: '02-2345-6789',
    ratingAverage: 4.3,
    reviewCount: 89,
    businessNumber: '234-56-78901',
    storeProfileImage: 'https://example.com/images/store2.jpg',
    latitude: 37.5563,
    longitude: 126.9233,
    active: true,
    createdAt: '2024-02-20T10:30:00',
    updatedAt: '2024-07-28T16:45:00',
    ddipBoxes: [
      {
        ddipboxId: 2001,
        ddipboxName: '채소 믹스',
        category: '채소',
        originalPrice: 8000,
        salePrice: 5000,
        remainingQuantity: 12,
        active: true,
      },
      {
        ddipboxId: 2002,
        ddipboxName: '과일 세트',
        category: '과일',
        originalPrice: 20000,
        salePrice: 14000,
        remainingQuantity: 7,
        active: true,
      },
    ],
  },
  {
    storeId: 3,
    ownerId: 103,
    storeName: '코리아 치킨',
    storeAddress: '부산광역시 해운대구 해운대해변로 789, 2층',
    description: '바삭하고 맛있는 치킨 전문점. 다양한 소스와 함께 즐기세요!',
    operatingHours: '16:00-02:00',
    phoneNumber: '051-3456-7890',
    ratingAverage: 4.9,
    reviewCount: 234,
    businessNumber: '345-67-89012',
    storeProfileImage: 'https://example.com/images/store3.jpg',
    latitude: 35.1595,
    longitude: 129.1606,
    active: true,
    createdAt: '2024-03-10T14:20:00',
    updatedAt: '2024-08-05T19:15:00',
    ddipBoxes: [
      {
        ddipboxId: 3001,
        ddipboxName: '치킨 반마리 + 사이드',
        category: '치킨',
        originalPrice: 25000,
        salePrice: 18000,
        remainingQuantity: 2,
        active: true,
      },
    ],
  },
];

// API 시뮬레이션 함수
export const fetchStoreDetail = async (
  storeId: number,
): Promise<StoreDetailDto> => {
  // 실제 API 호출 대신 mock data 반환
  return new Promise(resolve => {
    setTimeout(() => {
      const store =
        mockStoreDetailList.find(s => s.storeId === storeId) || mockStoreDetail;
      resolve(store);
    }, 1000); // 1초 지연으로 실제 API 호출 시뮬레이션
  });
};

// 사용 예시
// const storeDetail = await fetchStoreDetail(1);
// console.log(storeDetail);
