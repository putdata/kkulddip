package com.kkulddip.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(max = 11, message = "전화번호는 11자 이하여야 합니다")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식이 아닙니다")
    String phoneNumber,
    
    Boolean isVerified,
    
    Boolean notificationEnabled
) {} 