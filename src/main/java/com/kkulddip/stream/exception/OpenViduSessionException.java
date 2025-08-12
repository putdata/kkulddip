package com.kkulddip.stream.exception;

import com.kkulddip.common.exception.ErrorCode;
import io.openvidu.java.client.OpenViduHttpException;
import lombok.Getter;

/**
 * OpenVidu 세션 관련 예외
 */
@Getter
public class OpenViduSessionException extends StreamException {

    private final String sessionId;

    public OpenViduSessionException(ErrorCode errorCode, String sessionId) {
        super(errorCode);
        this.sessionId = sessionId;
    }

    public OpenViduSessionException(ErrorCode errorCode, String sessionId, Throwable cause) {
        super(errorCode, cause);
        this.sessionId = sessionId;
    }

    public static OpenViduSessionException sessionCreationFailed(String sessionId, Exception cause) {
        return new OpenViduSessionException(
            ErrorCode.COMMON_INTERNAL_SERVER_ERROR,
            sessionId,
            cause
        );
    }

    public static OpenViduSessionException sessionNotFound(String sessionId) {
        return new OpenViduSessionException(
            ErrorCode.COMMON_ENTITY_NOT_FOUND,
            sessionId
        );
    }

    public static OpenViduSessionException fromOpenViduException(String sessionId, Exception cause) {
        if (cause instanceof OpenViduHttpException httpEx && httpEx.getStatus() == 404) {
            return sessionNotFound(sessionId);
        }
        return sessionCreationFailed(sessionId, cause);
    }
}