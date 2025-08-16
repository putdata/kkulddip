import { useCallback, useRef } from 'react';

interface UseInfiniteScrollProps {
  isLoading: boolean;
  isFetchingNextPage: boolean;
  hasNextPage: boolean | undefined;
  isFetching: boolean;
  fetchNextPage: () => void;
}

export const useInfiniteScroll = ({
  isLoading,
  isFetchingNextPage,
  hasNextPage,
  isFetching,
  fetchNextPage,
}: UseInfiniteScrollProps) => {
  const observerRef = useRef<IntersectionObserver | null>(null);

  const lastElementRef = useCallback(
    (node: HTMLDivElement | null) => {
      if (isLoading || isFetchingNextPage) {
        return;
      }
      if (observerRef.current) {
        observerRef.current.disconnect();
      }

      observerRef.current = new IntersectionObserver(entries => {
        if (entries[0]?.isIntersecting && hasNextPage && !isFetching) {
          fetchNextPage();
        }
      });

      if (node) {
        observerRef.current.observe(node);
      }
    },
    [isLoading, isFetchingNextPage, fetchNextPage, hasNextPage, isFetching],
  );

  return { lastElementRef };
};
