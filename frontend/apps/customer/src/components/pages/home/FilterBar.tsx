import {
  MapPin,
  Clock,
  Percent,
  Star,
  Filter,
  ChevronDown,
} from 'lucide-react';
import { useState, useRef } from 'react';

const FilterBar = () => {
  const [selectedFilters, setSelectedFilters] = useState<string[]>([]);
  const [showMore, setShowMore] = useState(false);
  const mainScrollRef = useRef<HTMLDivElement>(null);
  const additionalScrollRef = useRef<HTMLDivElement>(null);

  // 마우스 드래그 스크롤 기능
  const useDragScroll = (ref: React.RefObject<HTMLDivElement | null>) => {
    const [isDown, setIsDown] = useState(false);
    const [startX, setStartX] = useState(0);
    const [scrollLeft, setScrollLeft] = useState(0);

    const handleMouseDown = (e: React.MouseEvent) => {
      if (!ref.current) {
        return;
      }
      setIsDown(true);
      setStartX(e.pageX - ref.current.offsetLeft);
      setScrollLeft(ref.current.scrollLeft);
      ref.current.style.cursor = 'grabbing';
    };

    const handleMouseLeave = () => {
      setIsDown(false);
      if (ref.current) {
        ref.current.style.cursor = 'grab';
      }
    };

    const handleMouseUp = () => {
      setIsDown(false);
      if (ref.current) {
        ref.current.style.cursor = 'grab';
      }
    };

    const handleMouseMove = (e: React.MouseEvent) => {
      if (!isDown || !ref.current) {
        return;
      }
      e.preventDefault();
      const x = e.pageX - ref.current.offsetLeft;
      const walk = (x - startX) * 2;
      ref.current.scrollLeft = scrollLeft - walk;
    };

    return {
      onMouseDown: handleMouseDown,
      onMouseLeave: handleMouseLeave,
      onMouseUp: handleMouseUp,
      onMouseMove: handleMouseMove,
    };
  };

  const mainDragProps = useDragScroll(mainScrollRef);
  const additionalDragProps = useDragScroll(additionalScrollRef);

  const mainFilters = [
    { id: 'pickup', label: '픽업가능', icon: Clock, color: 'emerald' },
    { id: 'nearby', label: '1km 이내', icon: MapPin, color: 'blue' },
    { id: 'discount', label: '할인중', icon: Percent, color: 'red' },
    { id: 'rating', label: '별점 4.0+', icon: Star, color: 'yellow' },
  ];

  const additionalFilters = [
    { id: 'new', label: '신규매장', icon: '🆕', color: 'purple' },
    { id: 'popular', label: '인기매장', icon: '🔥', color: 'orange' },
    { id: 'review', label: '리뷰 많은순', icon: '💬', color: 'indigo' },
    { id: 'lowprice', label: '가격 낮은순', icon: '💰', color: 'green' },
  ];

  const toggleFilter = (filterId: string) => {
    setSelectedFilters(prev =>
      prev.includes(filterId)
        ? prev.filter(id => id !== filterId)
        : [...prev, filterId],
    );
  };

  const getFilterColor = (color: string, isSelected: boolean) => {
    if (!isSelected) {
      return 'border-gray-200 bg-white text-gray-600 hover:border-gray-300';
    }

    const colors = {
      emerald:
        'border-emerald-500 bg-emerald-500 text-white shadow-emerald-200',
      blue: 'border-blue-500 bg-blue-500 text-white shadow-blue-200',
      red: 'border-red-500 bg-red-500 text-white shadow-red-200',
      yellow: 'border-amber-500 bg-amber-500 text-white shadow-amber-200',
      purple: 'border-purple-500 bg-purple-500 text-white shadow-purple-200',
      orange: 'border-orange-500 bg-orange-500 text-white shadow-orange-200',
      indigo: 'border-indigo-500 bg-indigo-500 text-white shadow-indigo-200',
      green: 'border-green-500 bg-green-500 text-white shadow-green-200',
    };

    return colors[color as keyof typeof colors] || colors.emerald;
  };

  return (
    <div className="border-b border-gray-100 bg-white py-4">
      <div className="px-4">
        {/* 메인 필터 */}
        <div
          ref={mainScrollRef}
          className="scrollbar-hide mb-3 flex cursor-grab select-none gap-2 overflow-x-auto pb-1"
          style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
          {...mainDragProps}
        >
          <div className="flex min-w-max gap-2">
            {mainFilters.map(filter => {
              const isSelected = selectedFilters.includes(filter.id);
              const IconComponent = filter.icon;

              return (
                <button
                  key={filter.id}
                  onClick={e => {
                    e.stopPropagation();
                    toggleFilter(filter.id);
                  }}
                  onMouseDown={e => e.stopPropagation()}
                  className={`flex flex-shrink-0 transform cursor-pointer items-center gap-2 whitespace-nowrap rounded-full border-2 px-4 py-2.5 text-sm font-medium transition-all duration-200 active:scale-95 ${getFilterColor(filter.color, isSelected)} ${isSelected ? 'shadow-lg' : 'hover:shadow-md'} `}
                >
                  <IconComponent className="h-4 w-4" />
                  {filter.label}
                </button>
              );
            })}

            {/* 더보기 버튼 */}
            <button
              onClick={e => {
                e.stopPropagation();
                setShowMore(!showMore);
              }}
              onMouseDown={e => e.stopPropagation()}
              className={`flex flex-shrink-0 transform cursor-pointer items-center gap-1 whitespace-nowrap rounded-full border-2 px-3 py-2.5 text-sm font-medium transition-all duration-200 active:scale-95 ${
                showMore
                  ? 'border-gray-400 bg-gray-100 text-gray-700'
                  : 'border-gray-200 bg-white text-gray-600 hover:border-gray-300'
              } `}
            >
              <Filter className="h-4 w-4" />
              <ChevronDown
                className={`h-3 w-3 transition-transform duration-200 ${showMore ? 'rotate-180' : ''}`}
              />
            </button>
          </div>
        </div>

        {/* 추가 필터 (접혀있음) */}
        {showMore && (
          <div
            ref={additionalScrollRef}
            className="scrollbar-hide animate-in slide-in-from-top-2 cursor-grab select-none overflow-x-auto pb-1 duration-200"
            style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
            {...additionalDragProps}
          >
            <div className="flex min-w-max gap-2">
              {additionalFilters.map(filter => {
                const isSelected = selectedFilters.includes(filter.id);

                return (
                  <button
                    key={filter.id}
                    onClick={e => {
                      e.stopPropagation();
                      toggleFilter(filter.id);
                    }}
                    onMouseDown={e => e.stopPropagation()}
                    className={`flex flex-shrink-0 transform cursor-pointer items-center gap-2 whitespace-nowrap rounded-full border-2 px-4 py-2 text-sm font-medium transition-all duration-200 active:scale-95 ${getFilterColor(filter.color, isSelected)} ${isSelected ? 'shadow-lg' : 'hover:shadow-md'} `}
                  >
                    <span className="text-sm">{filter.icon}</span>
                    {filter.label}
                  </button>
                );
              })}
            </div>
          </div>
        )}

        {/* 선택된 필터 개수 표시 */}
        {selectedFilters.length > 0 && (
          <div className="mt-3 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="text-sm text-gray-600">
                {selectedFilters.length}개 필터 적용됨
              </span>
              <div className="flex gap-1">
                {selectedFilters.slice(0, 3).map(filterId => (
                  <span
                    key={filterId}
                    className="inline-block h-2 w-2 rounded-full bg-amber-400"
                  ></span>
                ))}
                {selectedFilters.length > 3 && (
                  <span className="text-xs text-gray-500">
                    +{selectedFilters.length - 3}
                  </span>
                )}
              </div>
            </div>
            <button
              onClick={() => setSelectedFilters([])}
              className="text-sm font-medium text-gray-500 hover:text-gray-700"
            >
              초기화
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default FilterBar;
