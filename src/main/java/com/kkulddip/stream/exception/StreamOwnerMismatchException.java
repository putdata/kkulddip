package com.kkulddip.stream.exception;

import static com.kkulddip.common.exception.ErrorCode.STREAM_OWNER_MISMATCH;

/**
 * 스트림 소유자가 아닐 때 발생하는 예외
 */
public class StreamOwnerMismatchException extends StreamException {
    
    public StreamOwnerMismatchException() {
        super(STREAM_OWNER_MISMATCH);
    }
    
    public StreamOwnerMismatchException(Long userId, Long streamId) {
        super(STREAM_OWNER_MISMATCH, 
            String.format("사용자 ID %d는 스트림 ID %d의 소유자가 아닙니다.", userId, streamId));
    }
}