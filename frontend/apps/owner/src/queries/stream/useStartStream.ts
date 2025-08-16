import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type { Stream } from 'common';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';

/**
 * 스트림 시작 뮤테이션 (방송 상태를 LIVE로 변경)
 */
export const useStartStream = () => {
  const queryClient = useQueryClient();

  return useMutation<Stream, Error, number>({
    mutationFn: streamService.startStream,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: streamQueryKeys.myStreams() });
      toast.success('방송이 성공적으로 시작되었습니다.');
    },
    onError: error => {
      console.error('방송 시작 실패:', error);
      toast.error('방송 시작 중 오류가 발생했습니다.');
    },
  });
};
