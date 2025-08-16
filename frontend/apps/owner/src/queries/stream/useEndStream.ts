import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
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
      toast.success('방송이 성공적으로 종료되었습니다.');
    },
    onError: error => {
      console.error('방송 종료 실패:', error);
      toast.error('방송 종료 중 오류가 발생했습니다.');
    },
  });
};
