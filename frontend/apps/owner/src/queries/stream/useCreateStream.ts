import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type { Stream, CreateStreamRequest } from 'common';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';

/**
 * 스트림 생성 뮤테이션
 */
export const useCreateStream = () => {
  const queryClient = useQueryClient();

  return useMutation<Stream, Error, CreateStreamRequest>({
    mutationFn: streamService.createStream,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: streamQueryKeys.myStreams() });
      toast.success('스트림이 성공적으로 생성되었습니다.');
    },
    onError: error => {
      console.error('스트림 생성 실패:', error);
      toast.error('스트림 생성 중 오류가 발생했습니다.');
    },
  });
};
