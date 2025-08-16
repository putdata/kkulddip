import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import type { StreamTokenResponse } from 'common';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';

/**
 * 스트림 연결 뮤테이션 (OpenVidu 토큰 발급)
 */
export const useConnectStream = () => {
  const queryClient = useQueryClient();

  return useMutation<StreamTokenResponse, Error, number>({
    mutationFn: streamService.connectStream,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: streamQueryKeys.myStreams() });
      toast.success('스트림에 성공적으로 연결되었습니다.');
    },
    onError: error => {
      console.error('스트림 연결 실패:', error);
      toast.error('스트림 연결 중 오류가 발생했습니다.');
    },
  });
};
