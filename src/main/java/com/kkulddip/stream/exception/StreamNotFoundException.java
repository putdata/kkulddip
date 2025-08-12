package com.kkulddip.stream.exception;

import static com.kkulddip.common.exception.ErrorCode.STREAM_NOT_FOUND;

/**
 * 스트림을 찾을 수 없을 때 발생하는 예외
 */
public class StreamNotFoundException extends StreamException {
    
    public StreamNotFoundException() {
        super(STREAM_NOT_FOUND);
    }
    
    public StreamNotFoundException(Long streamId) {
        super(STREAM_NOT_FOUND, "스트림을 찾을 수 없습니다. ID: " + streamId);
    }
}