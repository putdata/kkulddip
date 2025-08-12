import { useMutation, useQueryClient } from '@tanstack/react-query';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';

/**
 * 스트림 종료 뮤테이션
 */
export const useEndStream = () => {
  const queryClient = useQueryClient();

  return useMutation<void, Error, number>({
    mutationFn: streamService.endStream,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: streamQueryKeys.myStreams() });
    },
    onError: error => {
      console.error('방송 종료 실패:', error);
    },
  });
};
