import { useEffect, useRef, useState } from 'react';

/**
 * 스크롤 위치에 따른 헤더 상태 관리 Hook
 *
 * @description
 * 스크롤 위치를 감지하여 헤더의 배경색 변경 등에 사용됩니다.
 */
export const useScrollHeader = () => {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      const scrollElement = document.querySelector(
        '.simplebar-content-wrapper',
      );
      setIsScrolled((scrollElement?.scrollTop || 0) > 10);
    };

    const scrollElement = document.querySelector('.simplebar-content-wrapper');
    scrollElement?.addEventListener('scroll', handleScroll);

    return () => scrollElement?.removeEventListener('scroll', handleScroll);
  }, []);

  return isScrolled;
};

/**
 * 요소의 화면 노출 여부를 감지하는 Hook
 *
 * @description
 * Intersection Observer를 사용하여 요소가 뷰포트에 진입하는지 감지합니다.
 * 스크롤 애니메이션이나 지연 로딩에 활용됩니다.
 */
export const useIntersectionObserver = (options = {}) => {
  const [isVisible, setIsVisible] = useState(false);
  const ref = useRef<HTMLElement>(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry?.isIntersecting) {
          setIsVisible(true);
          observer.unobserve(entry.target);
        }
      },
      {
        threshold: 0.1,
        ...options,
      },
    );

    const currentRef = ref.current;
    if (currentRef) {
      observer.observe(currentRef);
    }

    return () => {
      if (currentRef) {
        observer.unobserve(currentRef);
      }
    };
  }, [options]);

  return { ref, isVisible };
};
