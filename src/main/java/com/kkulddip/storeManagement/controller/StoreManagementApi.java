package com.kkulddip.storeManagement.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.storeManagement.dto.request.CreateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreStatusRequest;
import com.kkulddip.storeManagement.dto.response.StoreManagementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 가게 관리 API 인터페이스
 * Swagger 문서화를 위한 인터페이스
 */
@Tag(name = "Store Management", description = "가게 관리 API (사장님 전용)")
@SecurityRequirement(name = "bearerAuth")
public interface StoreManagementApi {

    @Operation(
        summary = "가게 등록",
        description = "새로운 가게를 등록합니다. 사장님만 사용할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "가게 등록 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (사장님이 아님)"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "중복된 가게명"
        )
    })
    ApiResponse<StoreManagementResponse> createStore(
        @Parameter(description = "가게 등록 정보", required = true)
        @Valid @RequestBody CreateStoreRequest request
    );

    @Operation(
        summary = "가게 정보 수정",
        description = "기존 가게의 정보를 수정합니다. 해당 가게의 사장님만 수정할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (해당 가게 사장님이 아님)"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게"
        )
    })
    ApiResponse<StoreManagementResponse> updateStore(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId,
        
        @Parameter(description = "수정할 가게 정보")
        @Valid @RequestBody UpdateStoreRequest request
    );

    @Operation(
        summary = "가게 활성화/비활성화",
        description = "가게의 영업 상태를 변경합니다. 비활성화 시 활성 주문이 있으면 실패합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 상태 변경 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (해당 가게 사장님이 아님)"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "활성 주문이 있어 비활성화할 수 없음"
        )
    })
    ApiResponse<StoreManagementResponse> updateStoreStatus(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId,
        
        @Parameter(description = "변경할 상태 정보", required = true)
        @Valid @RequestBody UpdateStoreStatusRequest request
    );

    @Operation(
        summary = "가게 삭제",
        description = "가게를 논리적으로 삭제(비활성화)합니다. 활성 주문이 있으면 삭제할 수 없습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 삭제 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (해당 가게 사장님이 아님)"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "활성 주문이 있어 삭제할 수 없음"
        )
    })
    ApiResponse<Void> deleteStore(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId
    );
}