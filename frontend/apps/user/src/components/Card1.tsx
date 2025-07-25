import React from 'react';
import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
} from '@/components/ui/card';

import { DiscountBadge } from './Badge';

export interface Card1Props {
  img: {
    src: string;
    alt: string;
  };

  storeInfo: {
    name: string;
    menus: string[];
  };

  price: {
    original: number;
    discount: number;
  };

  pickupTime: {
    from: string;
    to: string;
  };
}

export const Card1 = ({ img, storeInfo, price, pickupTime }: Card1Props) => {
  return (
    <Card className="w-72 overflow-hidden p-0">
      <CardContent className="flex flex-col p-0">
        {/* 위쪽: 이미지 */}
        <div className="h-32 w-full bg-gray-200">
          <img src={img.src} alt={img.alt} className="w-full" />
          <p className="p-4 text-sm text-gray-600">이미지 영역</p>
        </div>

        {/* 아래쪽: 정보 영역 */}
        <div className="bg-white p-4">
          {/* 가게명 */}
          <CardTitle className="mb-1 text-lg font-bold">
            {storeInfo.name}
          </CardTitle>

          {/* 품목 */}
          <CardDescription className="mb-3 text-sm text-gray-500">
            {storeInfo.menus.join(', ')}
          </CardDescription>

          {/* 가격 | 픽업 시간 */}
          <CardContent className="flex justify-between p-0">
            <div>
              {/* 가격 정보 */}
              <div className="mb-2">
                <div className="mb-1 text-sm text-gray-400 line-through">
                  {price.original.toLocaleString()}원
                </div>
                <div className="text-2xl font-bold text-green-600">
                  {price.discount.toLocaleString()}원
                </div>
              </div>

              {/* 할인율 */}
              <DiscountBadge
                originalPrice={price.original}
                discountPrice={price.discount}
              />
            </div>
            <div className="flex flex-col items-end justify-end text-gray-400">
              <p>픽업 가능 시간</p>
              <p>
                {pickupTime.from} ~ {pickupTime.to}
              </p>
            </div>
          </CardContent>
        </div>
      </CardContent>
    </Card>
  );
};
