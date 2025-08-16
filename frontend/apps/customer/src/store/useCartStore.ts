import { create } from 'zustand';

import type { AddToCartResult, CartItem, StoreInfo } from '@/types/cart';
import { persist } from 'zustand/middleware';
import type { DdipBox } from '@/types/store';

interface CartStore {
  storeInfo: StoreInfo | null;
  items: CartItem[];
  storeChangeInfo: {
    show: boolean;
    pendingDdipbox: DdipBox | null;
    pendingStoreInfo: StoreInfo | null;
  };

  addToCart: (ddipbox: DdipBox, storeInfo: StoreInfo) => AddToCartResult;
  updateQuantity: (ddipboxId: number, quantity: number) => void;
  clearCart: () => void;
  clearAndAddNewStore: () => void; // 매개변수 제거
  setStoreChangeModal: (
    show: boolean,
    ddipbox?: DdipBox,
    storeInfo?: StoreInfo,
  ) => void;
}

// DdipBox -> CartItem 변환 헬퍼 함수
export const convertDdipBoxToCartItem = (ddipbox: DdipBox): CartItem => ({
  name: ddipbox.ddipboxName,
  price: ddipbox.salePrice,
  description: ddipbox.description,
  quantity: 1,
  // DdipBox에서 추가로 필요한 정보들
  ddipboxId: ddipbox.ddipboxId,
  discountRate: ddipbox.discountRate,
  storeId: ddipbox.storeId,
});

// Zustand 스토어 생성
export const useCartStore = create<CartStore>()(
  persist(
    (set, get) => ({
      items: [],
      storeInfo: null,

      storeChangeInfo: {
        show: false,

        pendingDdipbox: null,

        pendingStoreInfo: null,
      },
      // 장바구니에 아이템 추가
      addToCart: (ddipbox: DdipBox, storeInfo: StoreInfo) => {
        const state = get();

        // 다른 가게 상품인지 체크
        if (state.storeInfo && state.storeInfo.storeId !== storeInfo.storeId) {
          set({
            storeChangeInfo: {
              show: true,
              pendingDdipbox: ddipbox,
              pendingStoreInfo: storeInfo,
            },
          });

          return {
            success: false,
            message: '동일한 가게의 띱박스만 장바구니에 담을 수 있어요!', // 추가
            requiresConfirmation: true,
            conflictStoreInfo: state.storeInfo,
          };
        }

        // 동일 상품 존재 체크
        const existingItemIndex = state.items.findIndex(
          item => item.ddipboxId === ddipbox.ddipboxId,
        );

        if (existingItemIndex !== -1) {
          // 기존 상품 수량 증가
          set(state => ({
            items: state.items.map((item, index) =>
              index === existingItemIndex
                ? { ...item, quantity: item.quantity + 1 }
                : item,
            ),
          }));

          return {
            success: true,
            action: 'updated' as const,
            message: `${ddipbox.ddipboxName} 수량이 증가했습니다.`,
          };
        } else {
          // 새로운 상품 추가
          const newCartItem = convertDdipBoxToCartItem(ddipbox);

          set(state => ({
            storeInfo: storeInfo,
            items: [...state.items, newCartItem],
          }));

          return {
            success: true,
            action: 'added' as const,
            message: `${ddipbox.ddipboxName}이(가) 장바구니에 추가되었습니다.`,
          };
        }
      },

      // 수량 변경 함수
      updateQuantity: (ddipboxId: number, quantity: number) => {
        if (quantity <= 0) {
          // 수량이 0 이하면 아이템 제거
          set(state => {
            const newItems = state.items.filter(
              item => item.ddipboxId !== ddipboxId,
            );
            return {
              items: newItems,
              storeInfo: newItems.length === 0 ? null : state.storeInfo,
              storeChangeInfo:
                newItems.length === 0
                  ? {
                      show: false,
                      pendingDdipbox: null,
                      pendingStoreInfo: null,
                    }
                  : state.storeChangeInfo,
            };
          });
        } else {
          // 수량 업데이트
          set(state => ({
            items: state.items.map(item =>
              item.ddipboxId === ddipboxId ? { ...item, quantity } : item,
            ),
          }));
        }
      },

      // 장바구니 초기화
      clearCart: () =>
        set({
          items: [],
          storeInfo: null,
          storeChangeInfo: {
            show: false,
            pendingDdipbox: null,
            pendingStoreInfo: null,
          },
        }),

      // 장바구니 초기화 후 새 가게 상품 추가
      clearAndAddNewStore: () => {
        const state = get();
        const { pendingDdipbox, pendingStoreInfo } = state.storeChangeInfo;

        if (pendingDdipbox && pendingStoreInfo) {
          const newCartItem = convertDdipBoxToCartItem(pendingDdipbox);
          set({
            storeInfo: pendingStoreInfo,
            items: [newCartItem],
            storeChangeInfo: {
              show: false,
              pendingDdipbox: null,
              pendingStoreInfo: null,
            },
          });
        }
      },

      // 모달 제어 함수
      setStoreChangeModal: (
        show: boolean,
        ddipbox?: DdipBox,
        storeInfo?: StoreInfo,
      ) =>
        set({
          storeChangeInfo: {
            show,
            pendingDdipbox: ddipbox || null,
            pendingStoreInfo: storeInfo || null,
          },
        }),
    }),
    {
      name: 'cart-storage',
    },
  ),
);
