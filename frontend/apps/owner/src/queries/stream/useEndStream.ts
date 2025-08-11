import { useMutation, useQueryClient } from '@tanstack/react-query';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';
import { toast } from 'sonner';

/**
 * 스트림 종료 뮤테이션
 */
export const useEndStream = () => {
  const queryClient = useQueryClient();

  return useMutation<void, Error, number>({
    mutationFn: streamService.endStream,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: streamQueryKeys.myStreams() });
      toast.success('방송이 종료되었습니다.');
    },
    onError: error => {
      toast.error(`방송 종료에 실패했습니다: ${error.message}`);
    },
  });
};
