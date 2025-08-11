import { useState } from 'react';

interface UseStarRatingReturn {
  rating: number;
  handleStarClick: (star: number) => void;
  isStarFilled: (star: number) => boolean;
  setRating: (rating: number) => void;
}

export const useStarRating = (
  initialRating: number = 0,
): UseStarRatingReturn => {
  const [rating, setRating] = useState<number>(initialRating);

  const handleStarClick = (star: number): void => {
    setRating(star);
    console.log('선택된 별점: ', star);
  };
  const isStarFilled = (star: number): boolean => {
    return star <= rating;
  };

  return {
    rating,
    handleStarClick,
    isStarFilled,
    setRating,
  };
};
