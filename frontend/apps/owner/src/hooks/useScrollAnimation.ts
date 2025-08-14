import { useEffect, useRef, useState } from 'react';

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
