import type { ReviewResponse } from '@/types/review';

/**
 * 리뷰 목업 데이터
 */
export const mockReviews: Record<number, ReviewResponse[]> = {
  // 1번 가게: 리뷰 5개
  1: [
    {
      reviewId: 1,
      customerId: 101,
      storeId: 2,
      orderId: 1001,
      userName: '김민수',
      profileImage:
        'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face',
      content:
        '음식이 정말 맛있어요! 배달도 빠르고 포장도 깔끔하게 잘 되어있네요. 다음에도 또 주문할 예정입니다.',
      rating: 5,
      createdAt: '2025-08-10T18:30:15.000Z',
      updatedAt: '2025-08-10T18:30:15.000Z',
      helpfulCount: 12,
      images: [
        {
          reviewImgId: 1,
          imageUrl:
            'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=300&fit=crop',
          originalName: '음식사진1.jpg',
          fileSize: 234567,
          uploadOrder: 1,
          createdAt: '2025-08-10T18:30:15.000Z',
        },
        {
          reviewImgId: 2,
          imageUrl:
            'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&h=300&fit=crop',
          originalName: '음식사진2.jpg',
          fileSize: 189234,
          uploadOrder: 2,
          createdAt: '2025-08-10T18:30:15.000Z',
        },
      ],
      reply: {
        replyId: 1,
        ownerId: 201,
        content:
          '좋은 후기 감사합니다! 앞으로도 맛있는 음식으로 보답하겠습니다 😊',
        createdAt: '2025-08-10T20:15:30.000Z',
        updatedAt: '2025-08-10T20:15:30.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 2,
      customerId: 102,
      storeId: 2,
      orderId: 1002,
      userName: '박지영',
      profileImage:
        'https://images.unsplash.com/photo-1494790108755-2616b056b6f9?w=100&h=100&fit=crop&crop=face',
      content:
        '가격 대비 양이 좀 아쉬워요. 맛은 괜찮은데 좀 더 푸짐했으면 좋겠네요.',
      rating: 3,
      createdAt: '2025-08-09T12:45:22.000Z',
      updatedAt: '2025-08-09T12:45:22.000Z',
      helpfulCount: 5,
      images: [],
      reply: null,
      isHelpful: false,
    },
    {
      reviewId: 3,
      customerId: 103,
      storeId: 2,
      orderId: 1003,
      userName: '이준호',
      profileImage:
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face',
      content:
        '진짜 맛없어요... 돈 아까웠습니다. 주문 실수였나 싶을 정도로 별로네요.',
      rating: 1,
      createdAt: '2025-08-08T19:20:45.000Z',
      updatedAt: '2025-08-08T19:20:45.000Z',
      helpfulCount: 2,
      images: [
        {
          reviewImgId: 3,
          imageUrl:
            'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop',
          originalName: '실망스러운음식.jpg',
          fileSize: 156789,
          uploadOrder: 1,
          createdAt: '2025-08-08T19:20:45.000Z',
        },
      ],
      reply: {
        replyId: 2,
        ownerId: 201,
        content:
          '죄송합니다. 개선하도록 노력하겠습니다. 연락 주시면 보상해드리겠습니다.',
        createdAt: '2025-08-09T09:30:12.000Z',
        updatedAt: '2025-08-09T09:30:12.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 4,
      customerId: 104,
      storeId: 2,
      orderId: 1004,
      userName: '최수연',
      profileImage:
        'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop&crop=face',
      content:
        '평범해요. 나쁘지도 좋지도 않은 그런 맛이네요. 한 번 정도는 괜찮을 것 같아요.',
      rating: 3,
      createdAt: '2025-08-07T16:10:33.000Z',
      updatedAt: '2025-08-07T16:10:33.000Z',
      helpfulCount: 8,
      images: [
        {
          reviewImgId: 4,
          imageUrl:
            'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&h=300&fit=crop',
          originalName: '평범한음식.jpg',
          fileSize: 198765,
          uploadOrder: 1,
          createdAt: '2025-08-07T16:10:33.000Z',
        },
        {
          reviewImgId: 5,
          imageUrl:
            'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400&h=300&fit=crop',
          originalName: '반찬.jpg',
          fileSize: 167432,
          uploadOrder: 2,
          createdAt: '2025-08-07T16:10:33.000Z',
        },
        {
          reviewImgId: 6,
          imageUrl:
            'https://images.unsplash.com/photo-1551782450-a2132b4ba21d?w=400&h=300&fit=crop',
          originalName: '국물.jpg',
          fileSize: 145678,
          uploadOrder: 3,
          createdAt: '2025-08-07T16:10:33.000Z',
        },
      ],
      reply: null,
      isHelpful: false,
    },
    {
      reviewId: 5,
      customerId: 105,
      storeId: 2,
      orderId: 1005,
      userName: '장우진',
      profileImage:
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop&crop=face',
      content:
        '대박! 진짜 맛있어요. 친구들한테도 추천했습니다. 사장님 번창하세요!',
      rating: 5,
      createdAt: '2025-08-06T21:35:17.000Z',
      updatedAt: '2025-08-06T21:35:17.000Z',
      helpfulCount: 23,
      images: [
        {
          reviewImgId: 7,
          imageUrl:
            'https://images.unsplash.com/photo-1606502400342-6f7daa4ed2c8?w=400&h=300&fit=crop',
          originalName: '맛있는음식.jpg',
          fileSize: 287654,
          uploadOrder: 1,
          createdAt: '2025-08-06T21:35:17.000Z',
        },
      ],
      reply: {
        replyId: 3,
        ownerId: 201,
        content: '감사합니다! 항상 최선을 다하겠습니다 🙏',
        createdAt: '2025-08-07T08:20:45.000Z',
        updatedAt: '2025-08-07T08:20:45.000Z',
      },
      isHelpful: true,
    },
  ],

  // 2번 가게: 리뷰 0개
  2: [],

  // 3번 가게: 리뷰 23개
  3: [
    {
      reviewId: 6,
      customerId: 106,
      storeId: 3,
      orderId: 2001,
      userName: '김태희',
      profileImage:
        'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=100&h=100&fit=crop&crop=face',
      content:
        '여기 진짜 맛집이에요! 특히 시그니처 메뉴가 정말 독특하고 맛있어요. 재료도 신선하고 정성이 느껴져요.',
      rating: 5,
      createdAt: '2025-08-11T14:20:30.000Z',
      updatedAt: '2025-08-11T14:20:30.000Z',
      helpfulCount: 45,
      images: [
        {
          reviewImgId: 8,
          imageUrl:
            'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=300&fit=crop',
          originalName: '시그니처메뉴.jpg',
          fileSize: 324567,
          uploadOrder: 1,
          createdAt: '2025-08-11T14:20:30.000Z',
        },
        {
          reviewImgId: 9,
          imageUrl:
            'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400&h=300&fit=crop',
          originalName: '플레이팅.jpg',
          fileSize: 298765,
          uploadOrder: 2,
          createdAt: '2025-08-11T14:20:30.000Z',
        },
      ],
      reply: {
        replyId: 4,
        ownerId: 301,
        content:
          '감동적인 후기 감사합니다! 신선한 재료로 정성껏 만든 보람이 있네요 😊',
        createdAt: '2025-08-11T16:45:12.000Z',
        updatedAt: '2025-08-11T16:45:12.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 7,
      customerId: 107,
      storeId: 3,
      orderId: 2002,
      userName: '정민재',
      profileImage:
        'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=100&h=100&fit=crop&crop=face',
      content: '좋아요! 배달 시간도 빠르고 음식도 따뜻하게 왔어요.',
      rating: 4,
      createdAt: '2025-08-10T19:15:45.000Z',
      updatedAt: '2025-08-10T19:15:45.000Z',
      helpfulCount: 8,
      images: [],
      reply: null,
      isHelpful: true,
    },
    {
      reviewId: 8,
      customerId: 108,
      storeId: 3,
      orderId: 2003,
      userName: '한소영',
      profileImage:
        'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&h=100&fit=crop&crop=face',
      content: '음... 기대했는데 조금 아쉬워요. 간이 좀 싱거운 것 같아요.',
      rating: 2,
      createdAt: '2025-08-10T12:30:22.000Z',
      updatedAt: '2025-08-10T12:30:22.000Z',
      helpfulCount: 3,
      images: [
        {
          reviewImgId: 10,
          imageUrl:
            'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400&h=300&fit=crop',
          originalName: '싱거운음식.jpg',
          fileSize: 176543,
          uploadOrder: 1,
          createdAt: '2025-08-10T12:30:22.000Z',
        },
      ],
      reply: {
        replyId: 5,
        ownerId: 301,
        content: '소중한 의견 감사합니다. 간 조절에 더 신경 쓰겠습니다.',
        createdAt: '2025-08-10T15:20:18.000Z',
        updatedAt: '2025-08-10T15:20:18.000Z',
      },
      isHelpful: false,
    },
    {
      reviewId: 9,
      customerId: 109,
      storeId: 3,
      orderId: 2004,
      userName: '윤성호',
      profileImage:
        'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=100&h=100&fit=crop&crop=face',
      content: '완전 대만족! 양도 많고 맛도 좋아요. 가격도 합리적이고요.',
      rating: 5,
      createdAt: '2025-08-09T20:45:33.000Z',
      updatedAt: '2025-08-09T20:45:33.000Z',
      helpfulCount: 27,
      images: [
        {
          reviewImgId: 11,
          imageUrl:
            'https://images.unsplash.com/photo-1563379091339-03246963d28a?w=400&h=300&fit=crop',
          originalName: '푸짐한양.jpg',
          fileSize: 245678,
          uploadOrder: 1,
          createdAt: '2025-08-09T20:45:33.000Z',
        },
        {
          reviewImgId: 12,
          imageUrl:
            'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=300&fit=crop',
          originalName: '메인요리.jpg',
          fileSize: 267890,
          uploadOrder: 2,
          createdAt: '2025-08-09T20:45:33.000Z',
        },
        {
          reviewImgId: 13,
          imageUrl:
            'https://images.unsplash.com/photo-1551782450-a2132b4ba21d?w=400&h=300&fit=crop',
          originalName: '사이드메뉴.jpg',
          fileSize: 198432,
          uploadOrder: 3,
          createdAt: '2025-08-09T20:45:33.000Z',
        },
      ],
      reply: null,
      isHelpful: true,
    },
    {
      reviewId: 10,
      customerId: 110,
      storeId: 3,
      orderId: 2005,
      userName: '임다은',
      profileImage:
        'https://images.unsplash.com/photo-1502767089025-6572583495b7?w=100&h=100&fit=crop&crop=face',
      content: '보통이에요. 특별할 건 없지만 나쁘지도 않아요.',
      rating: 3,
      createdAt: '2025-08-09T18:12:15.000Z',
      updatedAt: '2025-08-09T18:12:15.000Z',
      helpfulCount: 4,
      images: [],
      reply: null,
      isHelpful: false,
    },
    {
      reviewId: 11,
      customerId: 111,
      storeId: 3,
      orderId: 2006,
      userName: '오준혁',
      profileImage:
        'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face',
      content:
        '맛있어요! 다만 배달이 조금 늦었네요. 그래도 음식이 맛있어서 용서됩니다 ㅎㅎ',
      rating: 4,
      createdAt: '2025-08-09T16:30:41.000Z',
      updatedAt: '2025-08-09T16:30:41.000Z',
      helpfulCount: 11,
      images: [
        {
          reviewImgId: 14,
          imageUrl:
            'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&h=300&fit=crop',
          originalName: '늦어도맛있는음식.jpg',
          fileSize: 234567,
          uploadOrder: 1,
          createdAt: '2025-08-09T16:30:41.000Z',
        },
      ],
      reply: {
        replyId: 6,
        ownerId: 301,
        content: '배달 지연 죄송합니다. 더 빠른 서비스 위해 노력하겠습니다!',
        createdAt: '2025-08-09T18:45:22.000Z',
        updatedAt: '2025-08-09T18:45:22.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 12,
      customerId: 112,
      storeId: 3,
      orderId: 2007,
      userName: '강지우',
      profileImage:
        'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=100&h=100&fit=crop&crop=face',
      content: '정말 실망이에요. 음식이 차갑게 왔고 맛도 별로였어요.',
      rating: 1,
      createdAt: '2025-08-08T21:20:18.000Z',
      updatedAt: '2025-08-08T21:20:18.000Z',
      helpfulCount: 6,
      images: [
        {
          reviewImgId: 15,
          imageUrl:
            'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop',
          originalName: '차가운음식.jpg',
          fileSize: 187654,
          uploadOrder: 1,
          createdAt: '2025-08-08T21:20:18.000Z',
        },
        {
          reviewImgId: 16,
          imageUrl:
            'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=300&fit=crop',
          originalName: '실망스러운플레이팅.jpg',
          fileSize: 156789,
          uploadOrder: 2,
          createdAt: '2025-08-08T21:20:18.000Z',
        },
      ],
      reply: {
        replyId: 7,
        ownerId: 301,
        content:
          '정말 죄송합니다. 즉시 개선 조치하겠습니다. 연락 주시면 재주문 도와드리겠습니다.',
        createdAt: '2025-08-09T09:15:30.000Z',
        updatedAt: '2025-08-09T09:15:30.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 13,
      customerId: 113,
      storeId: 3,
      orderId: 2008,
      userName: '송하린',
      profileImage:
        'https://images.unsplash.com/photo-1494790108755-2616b056b6f9?w=100&h=100&fit=crop&crop=face',
      content: '깔끔하고 맛있어요. 건강한 맛이라 좋네요!',
      rating: 4,
      createdAt: '2025-08-08T14:45:27.000Z',
      updatedAt: '2025-08-08T14:45:27.000Z',
      helpfulCount: 15,
      images: [],
      reply: null,
      isHelpful: true,
    },
    {
      reviewId: 14,
      customerId: 114,
      storeId: 3,
      orderId: 2009,
      userName: '박현우',
      profileImage:
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face',
      content: '최고예요! 진짜 맛집 발견했네요. 자주 주문할게요.',
      rating: 5,
      createdAt: '2025-08-07T19:35:52.000Z',
      updatedAt: '2025-08-07T19:35:52.000Z',
      helpfulCount: 32,
      images: [
        {
          reviewImgId: 17,
          imageUrl:
            'https://images.unsplash.com/photo-1606502400342-6f7daa4ed2c8?w=400&h=300&fit=crop',
          originalName: '최고의맛.jpg',
          fileSize: 312456,
          uploadOrder: 1,
          createdAt: '2025-08-07T19:35:52.000Z',
        },
      ],
      reply: {
        replyId: 8,
        ownerId: 301,
        content: '감사합니다! 언제나 최고의 맛으로 보답하겠습니다 ❤️',
        createdAt: '2025-08-08T10:20:15.000Z',
        updatedAt: '2025-08-08T10:20:15.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 15,
      customerId: 115,
      storeId: 3,
      orderId: 2010,
      userName: '최예진',
      profileImage:
        'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop&crop=face',
      content: '그냥 그래요. 특별한 건 없네요.',
      rating: 2,
      createdAt: '2025-08-07T11:20:33.000Z',
      updatedAt: '2025-08-07T11:20:33.000Z',
      helpfulCount: 2,
      images: [],
      reply: null,
      isHelpful: false,
    },
    {
      reviewId: 16,
      customerId: 116,
      storeId: 3,
      orderId: 2011,
      userName: '홍석진',
      profileImage:
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop&crop=face',
      content: '양념이 너무 진해요. 개인적으로는 좀 부담스러웠어요.',
      rating: 2,
      createdAt: '2025-08-06T22:15:48.000Z',
      updatedAt: '2025-08-06T22:15:48.000Z',
      helpfulCount: 7,
      images: [
        {
          reviewImgId: 18,
          imageUrl:
            'https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&h=300&fit=crop',
          originalName: '진한양념.jpg',
          fileSize: 198765,
          uploadOrder: 1,
          createdAt: '2025-08-06T22:15:48.000Z',
        },
      ],
      reply: {
        replyId: 9,
        ownerId: 301,
        content: '피드백 감사합니다. 양념 농도 조절에 참고하겠습니다.',
        createdAt: '2025-08-07T08:30:25.000Z',
        updatedAt: '2025-08-07T08:30:25.000Z',
      },
      isHelpful: false,
    },
    {
      reviewId: 17,
      customerId: 117,
      storeId: 3,
      orderId: 2012,
      userName: '서민지',
      profileImage:
        'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=100&h=100&fit=crop&crop=face',
      content: '맛있어요! 특히 소스가 정말 좋네요. 집에서도 만들어보고 싶어요.',
      rating: 4,
      createdAt: '2025-08-06T17:40:12.000Z',
      updatedAt: '2025-08-06T17:40:12.000Z',
      helpfulCount: 19,
      images: [
        {
          reviewImgId: 19,
          imageUrl:
            'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400&h=300&fit=crop',
          originalName: '맛있는소스.jpg',
          fileSize: 223456,
          uploadOrder: 1,
          createdAt: '2025-08-06T17:40:12.000Z',
        },
        {
          reviewImgId: 20,
          imageUrl:
            'https://images.unsplash.com/photo-1551782450-a2132b4ba21d?w=400&h=300&fit=crop',
          originalName: '전체요리.jpg',
          fileSize: 267890,
          uploadOrder: 2,
          createdAt: '2025-08-06T17:40:12.000Z',
        },
      ],
      reply: null,
      isHelpful: true,
    },
    {
      reviewId: 18,
      customerId: 118,
      storeId: 3,
      orderId: 2013,
      userName: '김동현',
      profileImage:
        'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=100&h=100&fit=crop&crop=face',
      content: '완전 강추! 친구들과 같이 시켜먹었는데 모두 만족했어요.',
      rating: 5,
      createdAt: '2025-08-05T20:25:39.000Z',
      updatedAt: '2025-08-05T20:25:39.000Z',
      helpfulCount: 28,
      images: [
        {
          reviewImgId: 21,
          imageUrl:
            'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=300&fit=crop',
          originalName: '친구들과함께.jpg',
          fileSize: 345678,
          uploadOrder: 1,
          createdAt: '2025-08-05T20:25:39.000Z',
        },
      ],
      reply: {
        replyId: 10,
        ownerId: 301,
        content:
          '친구들과 함께 즐겨주셔서 감사합니다! 다음에도 맛있게 해드릴게요 🎉',
        createdAt: '2025-08-06T09:15:22.000Z',
        updatedAt: '2025-08-06T09:15:22.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 19,
      customerId: 119,
      storeId: 3,
      orderId: 2014,
      userName: '이수민',
      profileImage:
        'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100&h=100&fit=crop&crop=face',
      content: '괜찮아요. 평범한 맛이네요.',
      rating: 3,
      createdAt: '2025-08-05T15:10:26.000Z',
      updatedAt: '2025-08-05T15:10:26.000Z',
      helpfulCount: 5,
      images: [],
      reply: null,
      isHelpful: false,
    },
    {
      reviewId: 20,
      customerId: 120,
      storeId: 3,
      orderId: 2015,
      userName: '조영훈',
      profileImage:
        'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=100&h=100&fit=crop&crop=face',
      content: '너무 짜요. 건강을 생각한다면 염분 조절이 필요할 것 같아요.',
      rating: 2,
      createdAt: '2025-08-04T19:45:17.000Z',
      updatedAt: '2025-08-04T19:45:17.000Z',
      helpfulCount: 9,
      images: [
        {
          reviewImgId: 22,
          imageUrl:
            'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400&h=300&fit=crop',
          originalName: '짠음식.jpg',
          fileSize: 176543,
          uploadOrder: 1,
          createdAt: '2025-08-04T19:45:17.000Z',
        },
      ],
      reply: {
        replyId: 11,
        ownerId: 301,
        content: '소중한 의견 감사합니다. 염분 조절에 더욱 신경 쓰겠습니다.',
        createdAt: '2025-08-05T10:20:45.000Z',
        updatedAt: '2025-08-05T10:20:45.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 21,
      customerId: 121,
      storeId: 3,
      orderId: 2016,
      userName: '안채영',
      profileImage:
        'https://images.unsplash.com/photo-1502767089025-6572583495b7?w=100&h=100&fit=crop&crop=face',
      content: '대박 맛있어요! 진짜 숨은 맛집이네요. 사장님 대박나세요!',
      rating: 5,
      createdAt: '2025-08-04T14:30:55.000Z',
      updatedAt: '2025-08-04T14:30:55.000Z',
      helpfulCount: 41,
      images: [
        {
          reviewImgId: 23,
          imageUrl:
            'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400&h=300&fit=crop',
          originalName: '숨은맛집.jpg',
          fileSize: 298765,
          uploadOrder: 1,
          createdAt: '2025-08-04T14:30:55.000Z',
        },
        {
          reviewImgId: 24,
          imageUrl:
            'https://images.unsplash.com/photo-1563379091339-03246963d28a?w=400&h=300&fit=crop',
          originalName: '대박맛.jpg',
          fileSize: 325671,
          uploadOrder: 2,
          createdAt: '2025-08-04T14:30:55.000Z',
        },
      ],
      reply: {
        replyId: 12,
        ownerId: 301,
        content: '정말 감사합니다! 숨은 맛집이라고 해주셔서 너무 기뻐요 😍',
        createdAt: '2025-08-04T17:15:32.000Z',
        updatedAt: '2025-08-04T17:15:32.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 22,
      customerId: 122,
      storeId: 3,
      orderId: 2017,
      userName: '백준서',
      profileImage:
        'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face',
      content: '그냥 보통이에요. 특별히 나쁘지도 좋지도 않네요.',
      rating: 3,
      createdAt: '2025-08-03T16:20:42.000Z',
      updatedAt: '2025-08-03T16:20:42.000Z',
      helpfulCount: 3,
      images: [],
      reply: null,
      isHelpful: false,
    },
    {
      reviewId: 23,
      customerId: 123,
      storeId: 3,
      orderId: 2018,
      userName: '정하은',
      profileImage:
        'https://images.unsplash.com/photo-1494790108755-2616b056b6f9?w=100&h=100&fit=crop&crop=face',
      content:
        '맛있어요! 포장도 깔끔하고 위생적으로 잘 포장되어 있어서 믿고 먹을 수 있네요.',
      rating: 4,
      createdAt: '2025-08-03T12:45:18.000Z',
      updatedAt: '2025-08-03T12:45:18.000Z',
      helpfulCount: 16,
      images: [
        {
          reviewImgId: 25,
          imageUrl:
            'https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=300&fit=crop',
          originalName: '깔끔한포장.jpg',
          fileSize: 201234,
          uploadOrder: 1,
          createdAt: '2025-08-03T12:45:18.000Z',
        },
        {
          reviewImgId: 26,
          imageUrl:
            'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&h=300&fit=crop',
          originalName: '위생적인음식.jpg',
          fileSize: 187659,
          uploadOrder: 2,
          createdAt: '2025-08-03T12:45:18.000Z',
        },
        {
          reviewImgId: 27,
          imageUrl:
            'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400&h=300&fit=crop',
          originalName: '믿을수있는음식.jpg',
          fileSize: 234876,
          uploadOrder: 3,
          createdAt: '2025-08-03T12:45:18.000Z',
        },
      ],
      reply: {
        replyId: 13,
        ownerId: 301,
        content: '위생과 포장에 신경써주신 걸 알아봐 주셔서 감사합니다! 🙏',
        createdAt: '2025-08-03T15:30:25.000Z',
        updatedAt: '2025-08-03T15:30:25.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 24,
      customerId: 124,
      storeId: 3,
      orderId: 2019,
      userName: '남태윤',
      profileImage:
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face',
      content: '와... 진짜 맛없어요. 돈 아깝네요. 다시는 안 시킬 것 같아요.',
      rating: 1,
      createdAt: '2025-08-02T21:10:35.000Z',
      updatedAt: '2025-08-02T21:10:35.000Z',
      helpfulCount: 4,
      images: [
        {
          reviewImgId: 28,
          imageUrl:
            'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop',
          originalName: '맛없는음식.jpg',
          fileSize: 165432,
          uploadOrder: 1,
          createdAt: '2025-08-02T21:10:35.000Z',
        },
      ],
      reply: {
        replyId: 14,
        ownerId: 301,
        content:
          '정말 죄송합니다. 즉시 확인하고 개선하겠습니다. 개인적으로 연락드리겠습니다.',
        createdAt: '2025-08-03T08:45:12.000Z',
        updatedAt: '2025-08-03T08:45:12.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 25,
      customerId: 125,
      storeId: 3,
      orderId: 2020,
      userName: '유서연',
      profileImage:
        'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop&crop=face',
      content: '좋아요! 가성비도 좋고 맛도 괜찮아요. 자주 이용할 것 같아요.',
      rating: 4,
      createdAt: '2025-08-02T18:25:47.000Z',
      updatedAt: '2025-08-02T18:25:47.000Z',
      helpfulCount: 13,
      images: [],
      reply: null,
      isHelpful: true,
    },
    {
      reviewId: 26,
      customerId: 126,
      storeId: 3,
      orderId: 2021,
      userName: '강민성',
      profileImage:
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop&crop=face',
      content:
        '최고의 맛집 발견! 진짜 인생 맛집이에요. 모든 메뉴가 다 맛있어요!',
      rating: 5,
      createdAt: '2025-08-01T20:15:29.000Z',
      updatedAt: '2025-08-01T20:15:29.000Z',
      helpfulCount: 38,
      images: [
        {
          reviewImgId: 29,
          imageUrl:
            'https://images.unsplash.com/photo-1606502400342-6f7daa4ed2c8?w=400&h=300&fit=crop',
          originalName: '인생맛집.jpg',
          fileSize: 356789,
          uploadOrder: 1,
          createdAt: '2025-08-01T20:15:29.000Z',
        },
        {
          reviewImgId: 30,
          imageUrl:
            'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=300&fit=crop',
          originalName: '모든메뉴맛있음.jpg',
          fileSize: 298432,
          uploadOrder: 2,
          createdAt: '2025-08-01T20:15:29.000Z',
        },
      ],
      reply: {
        replyId: 15,
        ownerId: 301,
        content:
          '인생 맛집이라고 불러주셔서 정말 감동입니다! 항상 최선을 다하겠습니다 ❤️',
        createdAt: '2025-08-02T09:30:41.000Z',
        updatedAt: '2025-08-02T09:30:41.000Z',
      },
      isHelpful: true,
    },
    {
      reviewId: 27,
      customerId: 127,
      storeId: 3,
      orderId: 2022,
      userName: '윤지원',
      profileImage:
        'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=100&h=100&fit=crop&crop=face',
      content: '음... 기대보다는 아쉬워요. 그냥 평범한 맛이네요.',
      rating: 2,
      createdAt: '2025-08-01T14:40:16.000Z',
      updatedAt: '2025-08-01T14:40:16.000Z',
      helpfulCount: 6,
      images: [],
      reply: null,
      isHelpful: false,
    },
    {
      reviewId: 28,
      customerId: 128,
      storeId: 3,
      orderId: 2023,
      userName: '신동욱',
      profileImage:
        'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=100&h=100&fit=crop&crop=face',
      content: '완전 추천해요! 맛도 좋고 서비스도 친절해요. 10점 만점에 10점!',
      rating: 5,
      createdAt: '2025-07-31T19:55:33.000Z',
      updatedAt: '2025-07-31T19:55:33.000Z',
      helpfulCount: 34,
      images: [
        {
          reviewImgId: 31,
          imageUrl:
            'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400&h=300&fit=crop',
          originalName: '10점만점.jpg',
          fileSize: 278654,
          uploadOrder: 1,
          createdAt: '2025-07-31T19:55:33.000Z',
        },
      ],
      reply: {
        replyId: 16,
        ownerId: 301,
        content: '10점 만점이라니! 정말 감사합니다. 더욱 열심히 하겠습니다! 🌟',
        createdAt: '2025-08-01T10:25:18.000Z',
        updatedAt: '2025-08-01T10:25:18.000Z',
      },
      isHelpful: true,
    },
  ],
};

/**
 * 가게별 리뷰 통계
 */
export const mockReviewStats = {
  1: { totalReviews: 0, averageRating: 0 },
  2: { totalReviews: 5, averageRating: 3.4 },
  3: { totalReviews: 23, averageRating: 3.7 },
};
