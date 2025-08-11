import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { StreamTokenResponse } from 'common';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';
import { toast } from 'sonner';

/**
 * 스트림 시작 뮤테이션
 */
export const useStartStream = () => {
  const queryClient = useQueryClient();

  return useMutation<StreamTokenResponse, Error, number>({
    mutationFn: streamService.startStream,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: streamQueryKeys.myStreams() });
    },
    onError: error => {
      toast.error(`스트림 시작에 실패했습니다: ${error.message}`);
    },
  });
};
