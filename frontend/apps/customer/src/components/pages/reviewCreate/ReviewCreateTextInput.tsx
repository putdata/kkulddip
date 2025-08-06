import { Textarea } from '@/components/ui/textarea';
import { reviewCreateMessages } from '@/constants/messages';
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
  const messages = reviewCreateMessages;

  const [localText, setLocalText] = useState(reviewText);

  return (
    <div className="flex flex-col items-start gap-2 px-5 py-2.5">
      <div className="w-fit whitespace-nowrap text-sm font-bold text-gray-900 [font-family:'Segoe_UI']">
        {messages.description1}
      </div>
      <div className="flex w-full items-start gap-1.5 self-stretch">
        <Textarea
          className="h-50 resize-none bg-white p-2.5"
          placeholder={messages.placeholder1}
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
