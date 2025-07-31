import { useEffect, useRef, useState } from 'react';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  type CarouselApi,
} from '@/components/ui/carousel';

export interface FoodItem {
  id: number;
  name: string;
  imageUrl: string;
  description: string;
}

interface LoginFoodCardProps {
  item: FoodItem;
}

const LoginFoodCard = ({ item }: LoginFoodCardProps) => {
  return (
    <div className="w-full overflow-hidden rounded-xl bg-white shadow-md">
      <div className="flex aspect-[4/3] w-full items-center justify-center bg-gray-200">
        <img
          src={item.imageUrl}
          alt={item.name}
          className="h-full w-full object-cover"
        />
      </div>
      <div className="p-4">
        <h2 className="mb-1 text-base font-semibold text-gray-800">
          {item.description}
        </h2>
        <p className="text-sm text-gray-500">{item.name} 어때요?</p>
      </div>
    </div>
  );
};

const foodItems: FoodItem[] = [
  {
    id: 1,
    name: '반찬',
    imageUrl: '/assets/banchan.jpg',
    description: '집밥이 그리울 때',
  },
  {
    id: 2,
    name: '샐러드',
    imageUrl: '/assets/salad.jpg',
    description: '건강한 식단에는',
  },
  {
    id: 3,
    name: '샌드위치',
    imageUrl: '/assets/sandwich.jpg',
    description: '출출한 저녁',
  },
  {
    id: 4,
    name: '디저트',
    imageUrl: '/assets/dessert.jpg',
    description: '당이 떨어질 때',
  },
];

/**
 * 로그인 페이지에 표시되는 자동 슬라이딩 캐러셀 컴포넌트입니다.
 *
 * CarouselApi를 사용하여 2초마다 자동으로 오른쪽으로 슬라이드하며,
 * 마지막 슬라이드에 도달하면 첫 슬라이드로 자연스럽게 되돌아갑니다.
 *
 * @returns 음식 추천 카드들을 순환 표시하는 Carousel UI
 */
const LoginCarousel = () => {
  const [api, setApi] = useState<CarouselApi | null>(null);
  const currentRef = useRef(0);

  useEffect(() => {
    if (!api) {
      return;
    }

    const interval = setInterval(() => {
      const count = foodItems.length;
      const next = currentRef.current + 1;

      if (next >= count) {
        api.scrollTo(0);
        currentRef.current = 0;
      } else {
        api.scrollTo(next);
        currentRef.current = next;
      }
    }, 2000);

    return () => clearInterval(interval);
  }, [api]);

  return (
    <div className="mx-auto w-full max-w-sm">
      <Carousel
        setApi={setApi}
        className="w-full"
        opts={{
          align: 'start',
          loop: true,
        }}
      >
        <CarouselContent className="-ml-1">
          {foodItems.map(item => (
            <CarouselItem key={item.id} className="basis-full pl-1">
              <div className="p-1">
                <LoginFoodCard item={item} />
              </div>
            </CarouselItem>
          ))}
        </CarouselContent>
      </Carousel>
    </div>
  );
};

export default LoginCarousel;
