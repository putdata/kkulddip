package com.kkulddip.owner.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MonthlySettlementRangeRequest(
    @NotNull(message = "시작 년도는 필수입니다")
    @Min(value = 2020, message = "시작 년도는 2020년 이상이어야 합니다")
    @Max(value = 2100, message = "시작 년도는 2100년 이하여야 합니다")
    Integer startYear,

    @NotNull(message = "시작 월은 필수입니다")
    @Min(value = 1, message = "시작 월은 1 이상이어야 합니다")
    @Max(value = 12, message = "시작 월은 12 이하여야 합니다")
    Integer startMonth,

    @NotNull(message = "종료 년도는 필수입니다")
    @Min(value = 2020, message = "종료 년도는 2020년 이상이어야 합니다")
    @Max(value = 2100, message = "종료 년도는 2100년 이하여야 합니다")
    Integer endYear,

    @NotNull(message = "종료 월은 필수입니다")
    @Min(value = 1, message = "종료 월은 1 이상이어야 합니다")
    @Max(value = 12, message = "종료 월은 12 이하여야 합니다")
    Integer endMonth
) {}