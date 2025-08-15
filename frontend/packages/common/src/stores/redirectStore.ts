import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface RedirectState {
  redirectUrl: string | null;
  setRedirectUrl: (url: string) => void;
  getAndClearRedirectUrl: () => string | null;
  clearRedirectUrl: () => void;
}

/**
 * 로그인 후 리다이렉트할 URL을 관리하는 스토어
 *
 * @description
 * 401 에러 등으로 로그인 페이지로 이동할 때 원래 페이지 URL을 저장하고,
 * 로그인 성공 후 해당 페이지로 돌아갈 수 있도록 하는 기능을 제공합니다.
 */
export const useRedirectStore = create<RedirectState>()(
  persist(
    (set, get) => ({
      redirectUrl: null,

      /**
       * 리다이렉트할 URL을 저장합니다.
       * @param url - 저장할 URL
       */
      setRedirectUrl: (url: string) => {
        set({ redirectUrl: url });
      },

      /**
       * 저장된 리다이렉트 URL을 반환하고 스토어에서 제거합니다.
       * @returns 저장된 URL 또는 null
       */
      getAndClearRedirectUrl: () => {
        const { redirectUrl } = get();
        set({ redirectUrl: null });
        return redirectUrl;
      },

      /**
       * 저장된 리다이렉트 URL을 제거합니다.
       */
      clearRedirectUrl: () => {
        set({ redirectUrl: null });
      },
    }),
    {
      name: 'redirect-storage',
      // sessionStorage 사용 (탭 닫으면 자동 삭제)
      storage: {
        getItem: name => {
          const item = sessionStorage.getItem(name);
          return item ? JSON.parse(item) : null;
        },
        setItem: (name, value) => {
          sessionStorage.setItem(name, JSON.stringify(value));
        },
        removeItem: name => {
          sessionStorage.removeItem(name);
        },
      },
    },
  ),
);
