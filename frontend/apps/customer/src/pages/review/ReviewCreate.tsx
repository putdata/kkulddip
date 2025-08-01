import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { Textarea } from '@/components/ui/textarea';
import { Camera, Star } from 'lucide-react';
import React from 'react';

// export interface Props {
//   storeName: string;
//   img: string;
//   img-alt: string;
// }

const props = {
  // 임시 데이터
  storeName: '도미노피자 역삼점',
  img: 'https://images.unsplash.com/photo-1550321989-65d089904d5c?q=80&w=764&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
  // 현 페이지에서 사용할 문구
  question1: '음식은 어떠셨나요?',
  description1: '자세한 띱박스 리뷰를 작성해주세요.',
  placeholder1: '여러분의 따뜻한 리뷰는\n가게 사장님들에게 큰 도움이 됩니다.',
  addPicture: '사진 추가',
  pictureCount: '( 0 / 3 )',
};

// const ReviewCreate = ({ storeName }: Props) => {
const ReviewCreate = () => {
  return (
    // 임시 - div 확인용 스타일
    <>
      <style>{`
      // div {
      //   border: 1px solid aqua;
      // }
    `}</style>
      {/* // 메인 div  */}
      {/* 주의: h-screen을 해뒀지만, footer 크기만큼 빼줘야 함.
      아마.... footer 에서 조정해줘야 할듯?? */}
      <div className="flex h-screen flex-col bg-gray-100">
        {/* 가게 정보, 별점 남기기 */}
        <div className="flex w-full items-center justify-between p-5">
          {/* 상단 왼쪽 */}
          <div className="flex flex-col items-start justify-center gap-2">
            <div className="text-xl font-bold text-black [font-family:'Segoe_UI']">
              {props.storeName}
            </div>
            <div className="inline-flex flex-[0_0_auto] items-end">
              <div className="text-sm font-bold text-gray-900 [font-family:'Segoe_UI']">
                {props.question1}
              </div>
            </div>
            <div className="flex items-center">
              {[1, 2, 3, 4, 5].map(star => (
                <Star key={star} className="text-gray-300" />
                // <div key={star} className="h-6 w-6 text-gray-300">
                //   ⭐
                // </div>
              ))}
            </div>
          </div>

          <div className="flex h-20 w-20 items-center justify-center overflow-hidden bg-amber-100 p-[0.8px]">
            <img alt="Pizza" src={props.img} />
          </div>
        </div>

        <Separator />

        <div className="flex flex-col items-start gap-2 px-5 py-2.5">
          <div className="w-fit whitespace-nowrap text-sm font-bold text-gray-900 [font-family:'Segoe_UI']">
            {props.description1}
          </div>
          <div className="flex w-full items-start gap-1.5 self-stretch">
            <Textarea
              className="h-50 resize-none bg-white p-2.5"
              placeholder={props.placeholder1}
            />
          </div>
        </div>

        <div className="flex w-full items-center justify-center gap-2.5 p-5">
          <Button className="pointer-events-none flex h-auto w-full flex-1 grow items-center justify-center border border-dashed border-amber-600 bg-transparent text-amber-600">
            <Camera className="h-6 w-6" />
            <span className="text-lg font-bold [font-family:'segoe_UI',Helvetica]">
              {props.addPicture}
            </span>
            <span className="text-lg font-bold [font-family:'segoe_UI',Helvetica]">
              {props.pictureCount}
            </span>
          </Button>
        </div>

        <div className="flex justify-center">
          <Button className="w-50 pointer-events-none flex bg-amber-500 text-center font-bold text-white [font-family:Helvetica]">
            등록하기
          </Button>
        </div>
      </div>
    </>
  );
};

export default ReviewCreate;
