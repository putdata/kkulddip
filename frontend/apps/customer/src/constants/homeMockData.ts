// TypeScript 인터페이스 정의
export interface Store {
  storeId: number;
  ownerId: number;
  storeName: string;
  storeAddress: string;
  description: string;
  operatingHours: string;
  phoneNumber: string;
  ratingAverage: number;
  reviewNum: number;
  distanceFromUser: number;
  representativeDdipboxName: string;
  representativeOriginalPrice: number;
  representativeSalePrice: number;
  storeProfileImage: string;
  active: boolean;
}

export interface StoreApiResponseMetadata {
  sortBy: string;
  sortDirection: string;
  totalEstimate: number;
  searchKeyword: string;
  category: string;
}

export interface StoreApiResponseBody {
  content: Store[];
  hasNext: boolean;
  size: number;
  actualSize: number;
  isFirst: boolean;
  isLast: boolean;
  cursor: string;
  metadata: StoreApiResponseMetadata;
}

export interface StoreApiResponse {
  success: boolean;
  status: number;
  body: StoreApiResponseBody;
}

// 더미 데이터
export const dummyStoreData: StoreApiResponse = {
  success: true,
  status: 0,
  body: {
    content: [
      {
        storeId: 1,
        ownerId: 101,
        storeName: '맛있는 베이커리',
        storeAddress: '서울시 강남구 테헤란로 123',
        description: '신선한 빵과 케이크를 매일 구워내는 베이커리입니다.',
        operatingHours: '07:00 - 22:00',
        phoneNumber: '02-1234-5678',
        ratingAverage: 4.5,
        reviewNum: 127,
        distanceFromUser: 0.3,
        representativeDdipboxName: '크로와상 세트',
        representativeOriginalPrice: 15000,
        representativeSalePrice: 12000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400',
        active: true,
      },
      {
        storeId: 2,
        ownerId: 102,
        storeName: '홈메이드 도시락',
        storeAddress: '서울시 서초구 강남대로 456',
        description: '엄마 손맛 그대로 만든 건강한 도시락',
        operatingHours: '11:00 - 20:00',
        phoneNumber: '02-2345-6789',
        ratingAverage: 4.2,
        reviewNum: 89,
        distanceFromUser: 0.7,
        representativeDdipboxName: '한식 도시락',
        representativeOriginalPrice: 8000,
        representativeSalePrice: 6500,
        storeProfileImage:
          'https://images.unsplash.com/photo-1579952363873-27d3bfad9c0d?w=400',
        active: true,
      },
      {
        storeId: 3,
        ownerId: 103,
        storeName: '피자 마스터',
        storeAddress: '서울시 마포구 홍대로 789',
        description: '수제 도우로 만든 정통 이탈리안 피자',
        operatingHours: '16:00 - 02:00',
        phoneNumber: '02-3456-7890',
        ratingAverage: 4.7,
        reviewNum: 234,
        distanceFromUser: 1.2,
        representativeDdipboxName: '마르게리타 피자',
        representativeOriginalPrice: 25000,
        representativeSalePrice: 20000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400',
        active: true,
      },
      {
        storeId: 4,
        ownerId: 104,
        storeName: '카페 블루',
        storeAddress: '서울시 용산구 이태원로 321',
        description: '직접 로스팅한 원두로 내린 스페셜티 커피',
        operatingHours: '08:00 - 23:00',
        phoneNumber: '02-4567-8901',
        ratingAverage: 4.3,
        reviewNum: 156,
        distanceFromUser: 0.9,
        representativeDdipboxName: '아메리카노 & 디저트 세트',
        representativeOriginalPrice: 12000,
        representativeSalePrice: 9000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400',
        active: true,
      },
      {
        storeId: 5,
        ownerId: 105,
        storeName: '신선한 샐러드',
        storeAddress: '서울시 송파구 잠실로 654',
        description: '매일 아침 공수하는 신선한 채소로 만든 건강 샐러드',
        operatingHours: '09:00 - 21:00',
        phoneNumber: '02-5678-9012',
        ratingAverage: 4.1,
        reviewNum: 73,
        distanceFromUser: 1.8,
        representativeDdipboxName: '그린 샐러드 볼',
        representativeOriginalPrice: 14000,
        representativeSalePrice: 11000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400',
        active: true,
      },
      {
        storeId: 6,
        ownerId: 106,
        storeName: '치킨 하우스',
        storeAddress: '서울시 영등포구 여의도로 987',
        description: '바삭하고 맛있는 프리미엄 치킨 전문점',
        operatingHours: '17:00 - 01:00',
        phoneNumber: '02-6789-0123',
        ratingAverage: 4.6,
        reviewNum: 198,
        distanceFromUser: 2.1,
        representativeDdipboxName: '양념 치킨 세트',
        representativeOriginalPrice: 22000,
        representativeSalePrice: 18000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1562967914-608f82629710?w=400',
        active: true,
      },
      {
        storeId: 7,
        ownerId: 107,
        storeName: '일식당 사쿠라',
        storeAddress: '서울시 중구 명동길 147',
        description: '정통 일본 요리를 맛볼 수 있는 일식당',
        operatingHours: '12:00 - 22:00',
        phoneNumber: '02-7890-1234',
        ratingAverage: 4.4,
        reviewNum: 112,
        distanceFromUser: 1.5,
        representativeDdipboxName: '치라시 정식',
        representativeOriginalPrice: 18000,
        representativeSalePrice: 15000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=400',
        active: true,
      },
      {
        storeId: 8,
        ownerId: 108,
        storeName: '델리 샌드위치',
        storeAddress: '서울시 관악구 관악로 258',
        description: '신선한 재료로 만든 프리미엄 샌드위치',
        operatingHours: '08:00 - 20:00',
        phoneNumber: '02-8901-2345',
        ratingAverage: 4.0,
        reviewNum: 67,
        distanceFromUser: 2.3,
        representativeDdipboxName: '클럽 샌드위치',
        representativeOriginalPrice: 11000,
        representativeSalePrice: 8500,
        storeProfileImage:
          'https://images.unsplash.com/photo-1528735602780-2552fd46c7af?w=400',
        active: true,
      },
      {
        storeId: 9,
        ownerId: 109,
        storeName: '한식 전문점',
        storeAddress: '서울시 동대문구 장한로 369',
        description: '할머니 손맛을 재현한 전통 한식 요리',
        operatingHours: '11:00 - 21:30',
        phoneNumber: '02-9012-3456',
        ratingAverage: 4.8,
        reviewNum: 289,
        distanceFromUser: 1.7,
        representativeDdipboxName: '한정식 세트',
        representativeOriginalPrice: 16000,
        representativeSalePrice: 13000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1498654896293-37aacf113fd9?w=400',
        active: true,
      },
      {
        storeId: 10,
        ownerId: 110,
        storeName: '파스타 키친',
        storeAddress: '서울시 성북구 성북로 741',
        description: '이탈리아 정통 파스타와 리조또 전문점',
        operatingHours: '11:30 - 22:30',
        phoneNumber: '02-0123-4567',
        ratingAverage: 4.2,
        reviewNum: 134,
        distanceFromUser: 2.0,
        representativeDdipboxName: '크림 파스타',
        representativeOriginalPrice: 17000,
        representativeSalePrice: 14000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=400',
        active: true,
      },
      {
        storeId: 11,
        ownerId: 111,
        storeName: '버거 킹덤',
        storeAddress: '서울시 노원구 상계로 852',
        description: '수제 패티로 만든 프리미엄 버거 전문점',
        operatingHours: '11:00 - 23:00',
        phoneNumber: '02-1357-2468',
        ratingAverage: 4.3,
        reviewNum: 176,
        distanceFromUser: 3.2,
        representativeDdipboxName: '치즈 버거 세트',
        representativeOriginalPrice: 13000,
        representativeSalePrice: 10500,
        storeProfileImage:
          'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400',
        active: true,
      },
      {
        storeId: 12,
        ownerId: 112,
        storeName: '타코 플레이스',
        storeAddress: '서울시 은평구 은평로 963',
        description: '정통 멕시칸 타코와 부리또 전문점',
        operatingHours: '12:00 - 23:30',
        phoneNumber: '02-2468-1357',
        ratingAverage: 4.1,
        reviewNum: 92,
        distanceFromUser: 2.8,
        representativeDdipboxName: '치킨 타코 세트',
        representativeOriginalPrice: 15000,
        representativeSalePrice: 12000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400',
        active: true,
      },
      {
        storeId: 13,
        ownerId: 113,
        storeName: '디저트 스튜디오',
        storeAddress: '서울시 구로구 구로로 174',
        description: '수제 디저트와 케이크 전문점',
        operatingHours: '10:00 - 22:00',
        phoneNumber: '02-3691-2580',
        ratingAverage: 4.6,
        reviewNum: 203,
        distanceFromUser: 1.4,
        representativeDdipboxName: '티라미수 케이크',
        representativeOriginalPrice: 8000,
        representativeSalePrice: 6500,
        storeProfileImage:
          'https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?w=400',
        active: true,
      },
      {
        storeId: 14,
        ownerId: 114,
        storeName: '쌀국수 하노이',
        storeAddress: '서울시 금천구 시흥대로 285',
        description: '베트남 현지 맛을 그대로 재현한 쌀국수 전문점',
        operatingHours: '10:30 - 21:00',
        phoneNumber: '02-4702-5813',
        ratingAverage: 4.4,
        reviewNum: 167,
        distanceFromUser: 2.6,
        representativeDdipboxName: '소고기 쌀국수',
        representativeOriginalPrice: 9000,
        representativeSalePrice: 7500,
        storeProfileImage:
          'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400',
        active: true,
      },
      {
        storeId: 15,
        ownerId: 115,
        storeName: '팬케이크 하우스',
        storeAddress: '서울시 동작구 사당로 396',
        description: '푹신한 수제 팬케이크와 와플 전문점',
        operatingHours: '09:00 - 21:00',
        phoneNumber: '02-5814-6925',
        ratingAverage: 4.2,
        reviewNum: 138,
        distanceFromUser: 1.9,
        representativeDdipboxName: '블루베리 팬케이크',
        representativeOriginalPrice: 12000,
        representativeSalePrice: 10000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1506084868230-bb9d95c24759?w=400',
        active: true,
      },
      {
        storeId: 16,
        ownerId: 116,
        storeName: '중식당 만리장성',
        storeAddress: '서울시 광진구 아차산로 507',
        description: '정통 중화요리와 딤섬 전문점',
        operatingHours: '11:00 - 22:30',
        phoneNumber: '02-6925-7036',
        ratingAverage: 4.5,
        reviewNum: 221,
        distanceFromUser: 2.4,
        representativeDdipboxName: '짜장면 세트',
        representativeOriginalPrice: 10000,
        representativeSalePrice: 8000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1617093727343-374698b1b08d?w=400',
        active: true,
      },
      {
        storeId: 17,
        ownerId: 117,
        storeName: '인도 커리 하우스',
        storeAddress: '서울시 성동구 왕십리로 618',
        description: '정통 인도 스파이스로 만든 커리 전문점',
        operatingHours: '12:00 - 23:00',
        phoneNumber: '02-7036-8147',
        ratingAverage: 4.3,
        reviewNum: 95,
        distanceFromUser: 3.1,
        representativeDdipboxName: '치킨 커리 세트',
        representativeOriginalPrice: 16000,
        representativeSalePrice: 13500,
        storeProfileImage:
          'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=400',
        active: true,
      },
      {
        storeId: 18,
        ownerId: 118,
        storeName: '스시 오마카세',
        storeAddress: '서울시 서대문구 연희로 729',
        description: '신선한 횟감으로 만든 프리미엄 스시',
        operatingHours: '17:00 - 24:00',
        phoneNumber: '02-8147-9258',
        ratingAverage: 4.7,
        reviewNum: 312,
        distanceFromUser: 1.6,
        representativeDdipboxName: '스시 모듬',
        representativeOriginalPrice: 35000,
        representativeSalePrice: 28000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=400',
        active: true,
      },
      {
        storeId: 19,
        ownerId: 119,
        storeName: '고기 구이 전문점',
        storeAddress: '서울시 양천구 목동서로 840',
        description: '최고급 한우와 돼지고기 구이 전문점',
        operatingHours: '17:30 - 02:00',
        phoneNumber: '02-9258-0369',
        ratingAverage: 4.8,
        reviewNum: 267,
        distanceFromUser: 3.5,
        representativeDdipboxName: '한우 갈비살 세트',
        representativeOriginalPrice: 45000,
        representativeSalePrice: 38000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1544025162-d76694265947?w=400',
        active: true,
      },
      {
        storeId: 20,
        ownerId: 120,
        storeName: '해산물 전문점',
        storeAddress: '서울시 강서구 화곡로 951',
        description: '싱싱한 해산물로 만한 다양한 요리',
        operatingHours: '16:00 - 01:00',
        phoneNumber: '02-0369-1470',
        ratingAverage: 4.4,
        reviewNum: 184,
        distanceFromUser: 4.2,
        representativeDdipboxName: '해물찜 세트',
        representativeOriginalPrice: 32000,
        representativeSalePrice: 26000,
        storeProfileImage:
          'https://images.unsplash.com/photo-1542838132-92c53300491e?w=400',
        active: true,
      },
    ],
    hasNext: false,
    size: 20,
    actualSize: 20,
    isFirst: true,
    isLast: true,
    cursor: 'eyJzdG9yZUlkIjoyMH0=',
    metadata: {
      sortBy: 'distance',
      sortDirection: 'ASC',
      totalEstimate: 20,
      searchKeyword: '',
      category: 'all',
    },
  },
};
