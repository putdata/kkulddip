import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export type OnboardingStep = 'store' | 'ddipbox' | 'notification';

export type MobileStep =
  | 'store-basic' // 가게명, 사업자번호
  | 'store-location' // 주소, 지도 선택
  | 'store-contact' // 전화번호, 운영시간
  | 'store-description' // 가게 설명
  | 'ddipbox-basic' // 띱박스명, 카테고리
  | 'ddipbox-pricing' // 가격 정보
  | 'ddipbox-quantity' // 수량 정보
  | 'notification'; // 알림 설정

export interface OnboardingFormData {
  // Store 정보
  storeName: string;
  businessNumber: string;
  storeAddress: string;
  description: string;
  phone: string;
  operatingHours: string;
  latitude: number;
  longitude: number;

  // Ddipbox 정보
  ddipboxName: string;
  category: string;
  originalPrice: number;
  discountRate: number;
  salePrice: number;
  quantity: number;
  saleTime: string;
  dailyQuantity: number;
  maxPerCustomer: number;
  ddipboxDescription: string;

  // Notification 정보
  notifications: {
    orders: boolean;
    inventory: boolean;
    system: boolean;
  };
}

interface OnboardingState {
  completedSteps: Set<string>;
  currentStep: OnboardingStep;
  currentMobileStep: MobileStep;
  storeCreated: boolean;
  ddipboxCreated: boolean;

  // Form data
  formData: OnboardingFormData;

  // Actions
  markStepCompleted: (step: OnboardingStep) => void;
  setCurrentStep: (step: OnboardingStep) => void;
  setCurrentMobileStep: (step: MobileStep) => void;
  setStoreCreated: (created: boolean) => void;
  setDdipboxCreated: (created: boolean) => void;
  canAccessStep: (step: OnboardingStep) => boolean;
  canAccessMobileStep: (step: MobileStep) => boolean;
  updateFormData: (data: Partial<OnboardingFormData>) => void;
  getStoreFormData: () => Partial<OnboardingFormData>;
  getDdipboxFormData: () => Partial<OnboardingFormData>;
  resetForm: () => void;
  reset: () => void;
}

const initialFormData: OnboardingFormData = {
  // Store 정보
  storeName: '',
  businessNumber: '',
  storeAddress: '',
  description: '',
  phone: '',
  operatingHours: '',
  latitude: 0,
  longitude: 0,

  // Ddipbox 정보
  ddipboxName: '',
  category: '',
  originalPrice: 0,
  discountRate: 0,
  salePrice: 0,
  quantity: 0,
  saleTime: '',
  dailyQuantity: 1,
  maxPerCustomer: 1,
  ddipboxDescription: '',

  // Notification 정보
  notifications: {
    orders: false,
    inventory: false,
    system: false,
  },
};

export const useOnboardingStore = create<OnboardingState>()(
  persist(
    (set, get) => ({
      completedSteps: new Set<string>(),
      currentStep: 'store',
      currentMobileStep: 'store-basic',
      storeCreated: false,
      ddipboxCreated: false,
      formData: initialFormData,

      markStepCompleted: (step: OnboardingStep) => {
        set(state => ({
          completedSteps: new Set([...state.completedSteps, step]),
        }));
      },

      setCurrentStep: (step: OnboardingStep) => {
        set({ currentStep: step });
      },

      setCurrentMobileStep: (step: MobileStep) => {
        set({ currentMobileStep: step });
      },

      setStoreCreated: (created: boolean) => {
        set({ storeCreated: created });
        if (created) {
          get().markStepCompleted('store');
        }
      },

      setDdipboxCreated: (created: boolean) => {
        set({ ddipboxCreated: created });
        if (created) {
          get().markStepCompleted('ddipbox');
        }
      },

      canAccessStep: (step: OnboardingStep) => {
        const state = get();

        switch (step) {
          case 'store':
            return true; // 항상 접근 가능
          case 'ddipbox':
            return state.storeCreated; // 스토어 생성 후에만 접근 가능
          case 'notification':
            return state.storeCreated; // 스토어 생성 후에만 접근 가능 (ddipbox는 선택사항)
          default:
            return false;
        }
      },

      canAccessMobileStep: (step: MobileStep) => {
        const state = get();

        // 스토어 관련 스텝들
        const storeSteps: MobileStep[] = [
          'store-basic',
          'store-location',
          'store-contact',
          'store-description',
        ];
        // 띱박스 관련 스텝들
        const ddipboxSteps: MobileStep[] = [
          'ddipbox-basic',
          'ddipbox-pricing',
          'ddipbox-quantity',
        ];

        if (storeSteps.includes(step)) {
          return true; // 스토어 스텝은 항상 접근 가능
        }

        if (ddipboxSteps.includes(step)) {
          return state.storeCreated; // 스토어 생성 후에만 접근 가능
        }

        if (step === 'notification') {
          return state.storeCreated; // 스토어 생성 후에만 접근 가능
        }

        return false;
      },

      updateFormData: (data: Partial<OnboardingFormData>) => {
        set(state => ({
          formData: { ...state.formData, ...data },
        }));
      },

      getStoreFormData: () => {
        const { formData } = get();
        return {
          storeName: formData.storeName,
          businessNumber: formData.businessNumber,
          storeAddress: formData.storeAddress,
          description: formData.description,
          phone: formData.phone,
          operatingHours: formData.operatingHours,
          latitude: formData.latitude,
          longitude: formData.longitude,
        };
      },

      getDdipboxFormData: () => {
        const { formData } = get();
        return {
          ddipboxName: formData.ddipboxName,
          category: formData.category,
          originalPrice: formData.originalPrice,
          discountRate: formData.discountRate,
          salePrice: formData.salePrice,
          quantity: formData.quantity,
          saleTime: formData.saleTime,
          dailyQuantity: formData.dailyQuantity,
          maxPerCustomer: formData.maxPerCustomer,
          ddipboxDescription: formData.ddipboxDescription,
        };
      },

      resetForm: () => {
        set(() => ({
          formData: initialFormData,
        }));
      },

      reset: () => {
        set({
          completedSteps: new Set<string>(),
          currentStep: 'store',
          currentMobileStep: 'store-basic',
          storeCreated: false,
          ddipboxCreated: false,
          formData: initialFormData,
        });
      },
    }),
    {
      name: 'onboarding-storage',
      partialize: state => ({
        ...state,
        completedSteps: Array.from(state.completedSteps),
      }),
      onRehydrateStorage: () => state => {
        if (
          state &&
          state.completedSteps &&
          Array.isArray(state.completedSteps)
        ) {
          state.completedSteps = new Set(state.completedSteps as string[]);
        }
      },
    },
  ),
);
