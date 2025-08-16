import { useCallback, useRef } from 'react';
import FilterBar from '@/components/pages/home/FilterBar';
import HomeMainContainer from '@/components/pages/home/HomeMainContainer';
import { useStores } from '@/hooks/useStores';
import { Loader2 } from 'lucide-react';
import type { Store, StoreListResponse } from '@/types/store';

const Home = () => {
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetching,
    isFetchingNextPage,
    isLoading,
    isError,
  } = useStores();

  const observerRef = useRef<IntersectionObserver | null>(null);

  // 무한 스크롤 구현
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

  if (isLoading) {
    return (
      <div className="flex min-h-dvh items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin text-amber-600" />
          <div className="text-gray-500">주변 가게들을 불러오고 있어요...</div>
        </div>
      </div>
    );
  }

  // 에러 처리
  if (isError) {
    return <div>에러가 발생했습니다.</div>;
  }

  // 가게 목록 플래튼
  const stores: Store[] =
    data?.pages.flatMap((page: StoreListResponse) => page.content) ?? [];

  return (
    <div>
      <div className="pt-5">
        <FilterBar />
      </div>
      <HomeMainContainer 
        stores={stores} 
        lastElementRef={lastElementRef}
        isFetchingNextPage={isFetchingNextPage}
        hasNextPage={hasNextPage}
      />
    </div>
  );
};

export default Home;
