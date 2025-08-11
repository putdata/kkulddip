package com.kkulddip.stream.exception;

import static com.kkulddip.common.exception.ErrorCode.STREAM_CANNOT_BE_ENDED;

/**
 * 스트림을 종료할 수 없을 때 발생하는 예외
 */
public class StreamCannotBeEndedException extends StreamException {
    
    public StreamCannotBeEndedException() {
        super(STREAM_CANNOT_BE_ENDED);
    }
    
    public StreamCannotBeEndedException(String message) {
        super(STREAM_CANNOT_BE_ENDED, message);
    }
}