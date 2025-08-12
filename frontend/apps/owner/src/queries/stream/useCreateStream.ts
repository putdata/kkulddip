import { useMutation, useQueryClient } from '@tanstack/react-query';
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
    },
    onError: error => {
      console.error('스트림 생성 실패:', error);
    },
  });
};
