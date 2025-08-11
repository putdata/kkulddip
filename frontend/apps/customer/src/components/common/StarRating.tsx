import { Star } from 'lucide-react';

interface StarRatingProps {
  rating: number;
}

export const StarRating = ({ rating }: StarRatingProps) => {
  return (
    <div className="flex items-center">
      {[1, 2, 3, 4, 5].map(star => (
        <Star
          key={star}
          className={`h-3 w-3 ${
            star <= rating
              ? 'fill-amber-500 text-amber-500'
              : 'fill-gray-200 text-gray-200'
          }`}
        />
      ))}
    </div>
  );
};
