import { useState, useEffect, useCallback, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Input } from '@/components/ui/input';
import { SearchIcon, Loader2 } from 'lucide-react';
import { useInfiniteQuery } from '@tanstack/react-query';
import { StoreService } from '@/services/storeService';
import useGeolocation from '@/hooks/useGeolocation';
import type { Store } from '@/types/store';
import { Card } from '@/components/ui/card';
import { priceUtils } from '@/utils/priceFormat';
import { useNavigate } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';

interface StoreSearchParams {
  keyword: string;
  userLatitude?: number;
  userLongitude?: number;
  sortBy?: 'id' | 'created_at' | 'rating' | 'distance';
  size?: number;
  cursor?: string;
}

const Search = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [searchInput, setSearchInput] = useState(searchParams.get('keyword') || '');
  const [searchKeyword, setSearchKeyword] = useState(searchParams.get('keyword') || '');
  const location = useGeolocation();
  const latitude = location.coordinate?.latitude;
  const longitude = location.coordinate?.longitude;
  const navigate = useNavigate();
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadingRef = useRef<HTMLDivElement>(null);

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetching,
    isFetchingNextPage,
    isLoading,
    isError,
  } = useInfiniteQuery({
    queryKey: ['storeSearch', searchKeyword, latitude, longitude],
    queryFn: ({ pageParam }) => {
      const searchParams: StoreSearchParams = {
        keyword: searchKeyword,
        userLatitude: latitude ?? undefined,
        userLongitude: longitude ?? undefined,
        sortBy: 'rating',
        size: 10,
        cursor: pageParam as string | undefined,
      };
      return StoreService.searchStores(searchParams);
    },
    getNextPageParam: (lastPage) => {
      return lastPage.hasNext ? lastPage.nextCursor : undefined;
    },
    enabled: !!searchKeyword && searchKeyword.length > 0,
    staleTime: 5 * 60 * 1000, // 5분
  });

  // 무한 스크롤 구현
  const lastElementRef = useCallback(
    (node: HTMLDivElement | null) => {
      if (isLoading || isFetchingNextPage) return;
      if (observerRef.current) observerRef.current.disconnect();

      observerRef.current = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && hasNextPage && !isFetching) {
          fetchNextPage();
        }
      });

      if (node) observerRef.current.observe(node);
    },
    [isLoading, isFetchingNextPage, fetchNextPage, hasNextPage, isFetching]
  );

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchInput.trim()) {
      setSearchKeyword(searchInput.trim());
      setSearchParams({ keyword: searchInput.trim() });
    }
  };

  const handleStoreClick = (storeId: number) => {
    navigate(ROUTE_PATH.STORE_DETAIL(storeId.toString()));
  };

  // 검색 결과 플래튼
  const stores: Store[] = data?.pages.flatMap((page) => page.content) ?? [];

  return (
    <div className="flex min-h-screen flex-col bg-gray-50">
      {/* 검색바 */}
      <div className="sticky top-0 z-10 bg-white p-4 shadow-sm">
        <form onSubmit={handleSearch} className="relative">
          <div className="absolute left-3 top-1/2 -translate-y-1/2">
            <SearchIcon className="h-4 w-4 text-gray-400" />
          </div>
          <Input
            type="search"
            placeholder="가게명을 검색하세요"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            className="pl-10 pr-4"
          />
        </form>
      </div>

      {/* 검색 결과 */}
      <div className="flex-1 p-4">
        {!searchKeyword ? (
          <div className="flex flex-col items-center justify-center py-20">
            <div className="text-4xl mb-4">🔍</div>
            <h3 className="text-lg font-semibold text-gray-800 mb-2">
              가게를 검색해보세요
            </h3>
            <p className="text-sm text-gray-500">가게명을 입력하여 검색할 수 있어요</p>
          </div>
        ) : isLoading ? (
          <div className="flex flex-col items-center justify-center py-20">
            <Loader2 className="h-8 w-8 animate-spin text-amber-600 mb-4" />
            <p className="text-gray-500">검색 중...</p>
          </div>
        ) : isError ? (
          <div className="flex flex-col items-center justify-center py-20">
            <div className="text-4xl mb-4">❌</div>
            <h3 className="text-lg font-semibold text-gray-800 mb-2">
              검색 중 오류가 발생했어요
            </h3>
            <p className="text-sm text-gray-500">다시 시도해주세요</p>
          </div>
        ) : stores.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-20">
            <div className="text-4xl mb-4">🏪</div>
            <h3 className="text-lg font-semibold text-gray-800 mb-2">
              검색 결과가 없어요
            </h3>
            <p className="text-sm text-gray-500">다른 키워드로 검색해보세요</p>
          </div>
        ) : (
          <>
            <div className="mb-4">
              <p className="text-sm text-gray-600">
                '{searchKeyword}' 검색 결과 {stores.length}개
              </p>
            </div>
            <div className="space-y-4">
              {stores.map((store, index) => (
                <Card
                  key={`${store.storeId}-${index}`}
                  className="p-4 cursor-pointer hover:shadow-md transition-shadow"
                  onClick={() => handleStoreClick(store.storeId)}
                  ref={index === stores.length - 1 ? lastElementRef : null}
                >
                  <div className="flex items-start space-x-4">
                    <img
                      src={store.storeProfileImage}
                      alt={store.storeName}
                      className="h-16 w-16 rounded-lg object-cover"
                    />
                    <div className="flex-1 min-w-0">
                      <h3 className="font-semibold text-gray-900 truncate">
                        {store.storeName}
                      </h3>
                      <p className="text-sm text-gray-600 line-clamp-2">
                        {store.description}
                      </p>
                      <div className="flex items-center space-x-2 mt-2">
                        <span className="text-sm text-yellow-600">
                          ⭐ {store.ratingAverage.toFixed(1)}
                        </span>
                        <span className="text-sm text-gray-500">
                          리뷰 {store.reviewCount}개
                        </span>
                        <span className="text-sm text-gray-500">
                          {store.distanceFromUser.toFixed(1)}km
                        </span>
                      </div>
                      <div className="flex items-center space-x-2 mt-1">
                        <span className="text-sm text-gray-400 line-through">
                          {priceUtils.formatPrice(store.representativeOriginalPrice)}
                        </span>
                        <span className="text-sm font-semibold text-amber-600">
                          {priceUtils.formatPrice(store.representativeSalePrice)}
                        </span>
                      </div>
                    </div>
                  </div>
                </Card>
              ))}
            </div>

            {/* 로딩 인디케이터 */}
            {isFetchingNextPage && (
              <div className="flex justify-center py-4">
                <Loader2 className="h-6 w-6 animate-spin text-amber-600" />
              </div>
            )}

            {/* 더 이상 로드할 데이터가 없을 때 */}
            {!hasNextPage && stores.length > 0 && (
              <div className="text-center py-8">
                <p className="text-sm text-gray-500">모든 검색 결과를 불러왔어요</p>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default Search;
