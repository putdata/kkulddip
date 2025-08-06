import { Textarea } from '@/components/ui/textarea';
import { useState } from 'react';

// Props 타입 정의
interface ReviewCreateTextProps {
  reviewText: string;
  setReviewText: (text: string) => void;
}

const ReviewCreateText = ({
  reviewText,
  setReviewText,
}: ReviewCreateTextProps) => {
  // UI 문구 (프론트엔드에서 관리)

  const [localText, setLocalText] = useState(reviewText);

  return (
    <div className="flex w-full flex-col items-start gap-2">
      <div className="w-full whitespace-nowrap text-sm font-bold text-gray-900 [font-family:'Segoe_UI']">
        자세한 띱박스 리뷰를 작성해주세요.
      </div>
      <div className="flex w-full items-start gap-1.5 self-stretch">
        <Textarea
          className="h-50 resize-none bg-white p-2.5"
          placeholder={
            '여러분의 따뜻한 리뷰는\n가게 사장님들에게 큰 도움이 됩니다.'
          }
          value={localText}
          onChange={e => {
            setLocalText(e.target.value);
          }}
          onBlur={e => {
            setReviewText(e.target.value);
            console.log('작성된 리뷰:', e.target.value);
          }}
        />
      </div>
    </div>
  );
};

export default ReviewCreateText;
