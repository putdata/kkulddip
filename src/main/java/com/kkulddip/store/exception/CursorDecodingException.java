package com.kkulddip.store.exception;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;

/**
 * 커서 디코딩 실패시 발생하는 예외
 */
public class CursorDecodingException extends BusinessException {

    public CursorDecodingException() {
        super(ErrorCode.STORE_CURSOR_DECODE_FAILED);
    }

    public CursorDecodingException(String cursor) {
        super(ErrorCode.STORE_CURSOR_DECODE_FAILED, "커서: " + cursor);
    }

    public CursorDecodingException(String cursor, Throwable cause) {
        super(ErrorCode.STORE_CURSOR_DECODE_FAILED, "커서: " + cursor + ", 원인: " + cause.getMessage());
    }
}