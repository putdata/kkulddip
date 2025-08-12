package com.kkulddip.stream.exception;

import com.kkulddip.common.exception.ErrorCode;
import lombok.Getter;

/**
 * OpenVidu 연결 관련 예외
 */
@Getter
public class OpenViduConnectionException extends StreamException {

    private final String sessionId;
    private final String role;

    public OpenViduConnectionException(ErrorCode errorCode, String sessionId, String role) {
        super(errorCode);
        this.sessionId = sessionId;
        this.role = role;
    }

    public OpenViduConnectionException(ErrorCode errorCode, String sessionId, String role, Throwable cause) {
        super(errorCode, cause);
        this.sessionId = sessionId;
        this.role = role;
    }

    public static OpenViduConnectionException connectionCreationFailed(String sessionId, String role, Exception cause) {
        return new OpenViduConnectionException(
            ErrorCode.COMMON_INTERNAL_SERVER_ERROR,
            sessionId,
            role,
            cause
        );
    }
}