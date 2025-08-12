package com.kkulddip.stream.exception;

import static com.kkulddip.common.exception.ErrorCode.STREAM_CANNOT_BE_STARTED;

/**
 * 스트림을 시작할 수 없을 때 발생하는 예외
 */
public class StreamCannotBeStartedException extends StreamException {
    
    public StreamCannotBeStartedException() {
        super(STREAM_CANNOT_BE_STARTED);
    }
    
    public StreamCannotBeStartedException(String message) {
        super(STREAM_CANNOT_BE_STARTED, message);
    }
}