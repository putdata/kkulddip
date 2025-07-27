package com.kkulddip.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kkulddip.common.exception.ErrorCode;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse<T>(
        boolean success,
        String status,
        String code,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        T body
) {
    
    public static <T> ErrorResponse<T> of(String status, String code, String message, T body) {
        return new ErrorResponse<>(false, status, code, message, LocalDateTime.now(), body);
    }
    
    public static ErrorResponse<Void> of(String status, String code, String message) {
        return new ErrorResponse<>(false, status, code, message, LocalDateTime.now(), null);
    }

    public static ErrorResponse<Void> of(ErrorCode errorCode) {
        return new ErrorResponse<>(
                false,
                errorCode.getStatus().name(),
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now(),
                null
        );
    }
}
