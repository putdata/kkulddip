import { useState, type ChangeEvent, type FormEvent } from 'react';
import { toast } from 'sonner';
import type { CreateStreamRequest } from 'common';

/**
 * useStreamForm Hook Props
 */
interface UseStreamFormProps {
  /** 스토어 ID */
  storeId: number;
  /** 폼 제출 핸들러 */
  onSubmit: (data: CreateStreamRequest) => void | Promise<void>;
}

/**
 * 스트림 생성 폼 관리 Hook
 *
 * @description
 * 스트림 생성 폼의 상태와 유효성 검사를 관리합니다.
 * 제목과 설명 입력을 처리하고, 폼 제출 시 유효성을 검사합니다.
 *
 * @param {UseStreamFormProps} props - Hook 설정
 * @returns 폼 상태와 핸들러 함수들
 */
export const useStreamForm = ({ storeId, onSubmit }: UseStreamFormProps) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [isValid, setIsValid] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!title.trim()) {
      toast.error('스트림 제목을 입력해주세요.');
      return;
    }

    if (title.length > 200) {
      toast.error('제목은 200자 이내로 입력해주세요.');
      return;
    }

    if (description.length > 1000) {
      toast.error('설명은 1000자 이내로 입력해주세요.');
      return;
    }

    const streamData: CreateStreamRequest = {
      storeId,
      title: title.trim(),
      description: description.trim() || undefined,
    };

    await onSubmit(streamData);
  };

  const handleTitleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setTitle(value);
    setIsValid(value.trim().length > 0 && value.length <= 200);
  };

  const handleDescriptionChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    const value = e.target.value;
    setDescription(value);
  };

  const resetForm = () => {
    setTitle('');
    setDescription('');
    setIsValid(false);
  };

  return {
    /** 스트림 제목 */
    title,
    /** 스트림 설명 */
    description,
    /** 폼 유효성 상태 */
    isValid,

    /** 폼 제출 핸들러 */
    handleSubmit,
    /** 제목 변경 핸들러 */
    handleTitleChange,
    /** 설명 변경 핸들러 */
    handleDescriptionChange,
    /** 폼 초기화 */
    resetForm,
  };
};
