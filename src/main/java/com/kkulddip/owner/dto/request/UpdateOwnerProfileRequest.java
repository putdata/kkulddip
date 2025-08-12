package com.kkulddip.owner.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOwnerProfileRequest(
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    String name,

    @Size(max = 255, message = "프로필 이미지 URL은 255자 이하여야 합니다")
    String profileImageUrl,

    @Size(max = 20, message = "사업자 등록번호는 20자 이하여야 합니다")
    String businessNumber,

    @Size(max = 50, message = "대표자명은 50자 이하여야 합니다")
    String representativeName
) {}