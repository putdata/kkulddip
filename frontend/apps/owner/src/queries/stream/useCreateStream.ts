import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { Stream, CreateStreamRequest } from 'common';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';
import { toast } from 'sonner';

/**
 * 스트림 생성 뮤테이션
 */
export const useCreateStream = () => {
  const queryClient = useQueryClient();

  return useMutation<Stream, Error, CreateStreamRequest>({
    mutationFn: streamService.createStream,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: streamQueryKeys.myStreams() });
      toast.success('스트림이 성공적으로 생성되었습니다!');
    },
    onError: error => {
      toast.error(`스트림 생성에 실패했습니다: ${error.message}`);
    },
  });
};