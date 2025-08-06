import { Button } from '@/components/ui/button';
import { Camera, X } from 'lucide-react';
import { toast } from 'sonner';

interface ReviewCreatePhotoInputProps {
  selectedImages: File[];
  imagePreviewUrls: string[];
  setSelectedImages: (images: File[]) => void;
  setImagePreviewUrls: (urls: string[]) => void;
}

const ReviewCreatePhotoInput = ({
  selectedImages,
  imagePreviewUrls,
  setSelectedImages,
  setImagePreviewUrls,
}: ReviewCreatePhotoInputProps) => {
  // 파일 선택창 열기
  const openFileDialog = () => {
    document.getElementById('imageInput')?.click();
  };

  // 파일 선택 핸들러
  const handleImageSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;

    // 이미지 업로드 취소
    if (!files) {
      return;
    }

    console.log('선택된 파일들:', files);

    // 현재 선택된 이미지 + 새로 선택된 이미지
    const newImages = Array.from(files);
    const totalImages = [...selectedImages, ...newImages];

    if (totalImages.length > 3) {
      toast.error('사진 업로드 제한', {
        description: '최대 3개까지만 선택할 수 있습니다.',
      });
      return;
    }

    setSelectedImages(totalImages);

    // 미리보기 URL 생성
    const newPreviewUrls = newImages.map(file => URL.createObjectURL(file));
    setImagePreviewUrls([...imagePreviewUrls, ...newPreviewUrls]);
  };

  // 이미지 삭제 핸들러
  const handleImageRemove = (index: number) => {
    // 해당 인덱스의 이미지와 미리보기 URL 제거
    const newImages = selectedImages.filter((_, i) => i !== index);
    const newPreviewUrls = imagePreviewUrls.filter((_, i) => i !== index);

    // 메모리 해제 (중요!)
    const urlToRevoke = imagePreviewUrls[index];
    if (urlToRevoke) {
      // undefined가 아닐 때만 실행
      URL.revokeObjectURL(urlToRevoke);
    }

    setSelectedImages(newImages);
    setImagePreviewUrls(newPreviewUrls);
  };

  return (
    <div className="flex w-full flex-col">
      {selectedImages.length < 3 && (
        <Button
          className="flex h-auto grow cursor-pointer items-center justify-center border border-dashed border-amber-600 bg-transparent text-amber-600 hover:bg-amber-500 hover:text-white"
          onClick={openFileDialog}
        >
          <input
            id="imageInput"
            type="file"
            multiple
            accept="image/*"
            onChange={handleImageSelect}
            className="hidden"
          />
          <Camera className="h-6 w-6" />
          <span className="text-lg font-bold [font-family:'segoe_UI',Helvetica]">
            사진 추가
          </span>
          <span className="text-lg font-bold [font-family:'segoe_UI',Helvetica]">
            ( {selectedImages.length} / 3 )
          </span>
        </Button>
      )}

      {/* 선택된 이미지 미리보기 */}
      {selectedImages.length > 0 && (
        <div className="flex flex-col items-center gap-2 pt-3">
          <div className="flex items-center justify-center gap-2 overflow-x-auto">
            {imagePreviewUrls.map((url, index) => (
              <div key={index} className="relative flex-shrink-0">
                <img
                  src={url}
                  alt={`선택된 이미지 ${index + 1}`}
                  className="h-20 w-20 rounded-lg border border-gray-200 object-cover"
                />
                {/* 이미지 삭제 버튼 */}
                <button
                  onClick={() => handleImageRemove(index)}
                  className="absolute right-1 top-1 flex h-6 w-6 items-center justify-center rounded-full bg-red-500 text-white hover:bg-red-600"
                >
                  <X className="h-3 w-3" strokeWidth={5} />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default ReviewCreatePhotoInput;
