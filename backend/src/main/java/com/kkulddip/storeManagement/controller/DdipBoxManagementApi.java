package com.kkulddip.storeManagement.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.storeManagement.dto.request.CreateDdipBoxRequest;
import com.kkulddip.storeManagement.dto.request.UpdateDdipBoxRequest;
import com.kkulddip.storeManagement.dto.request.UpdateDdipBoxQuantityRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreStatusRequest;
import com.kkulddip.storeManagement.dto.response.DdipBoxManagementResponse;
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

import java.util.List;

/**
 * 띱박스 관리 API 인터페이스
 * Swagger 문서화를 위한 인터페이스
 */
@Tag(name = "DdipBox Management", description = "띱박스 관리 API (사장님 전용)")
@SecurityRequirement(name = "bearerAuth")
public interface DdipBoxManagementApi {

    @Operation(
        summary = "띱박스 생성",
        description = "가게에 새로운 띱박스 상품을 등록합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "띱박스 생성 성공",
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
    ApiResponse<DdipBoxManagementResponse> createDdipBox(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId,
        
        @Parameter(description = "띱박스 생성 정보", required = true)
        @Valid @RequestBody CreateDdipBoxRequest request
    );

    @Operation(
        summary = "띱박스 정보 수정",
        description = "기존 띱박스의 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "띱박스 수정 성공",
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
            description = "권한 없음"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게 또는 띱박스"
        )
    })
    ApiResponse<DdipBoxManagementResponse> updateDdipBox(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId,
        
        @Parameter(description = "띱박스 ID", required = true, example = "1")
        @PathVariable Long ddipboxId,
        
        @Parameter(description = "수정할 띱박스 정보")
        @Valid @RequestBody UpdateDdipBoxRequest request
    );

    @Operation(
        summary = "띱박스 재고 수량 업데이트",
        description = "띱박스의 잔여 수량을 직접 설정하거나 일일 수량을 재설정합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "재고 수량 업데이트 성공",
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
            description = "권한 없음"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게 또는 띱박스"
        )
    })
    ApiResponse<DdipBoxManagementResponse> updateDdipBoxQuantity(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId,
        
        @Parameter(description = "띱박스 ID", required = true, example = "1")
        @PathVariable Long ddipboxId,
        
        @Parameter(description = "수량 업데이트 정보", required = true)
        @Valid @RequestBody UpdateDdipBoxQuantityRequest request
    );

    @Operation(
        summary = "띱박스 활성화/비활성화",
        description = "띱박스의 판매 상태를 변경합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "띱박스 상태 변경 성공",
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
            description = "권한 없음"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게 또는 띱박스"
        )
    })
    ApiResponse<DdipBoxManagementResponse> updateDdipBoxStatus(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId,
        
        @Parameter(description = "띱박스 ID", required = true, example = "1")
        @PathVariable Long ddipboxId,
        
        @Parameter(description = "변경할 상태 정보", required = true)
        @Valid @RequestBody UpdateStoreStatusRequest request
    );

    @Operation(
        summary = "띱박스 삭제",
        description = "띱박스를 논리적으로 삭제(비활성화)합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "띱박스 삭제 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게 또는 띱박스"
        )
    })
    ApiResponse<Void> deleteDdipBox(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId,
        
        @Parameter(description = "띱박스 ID", required = true, example = "1")
        @PathVariable Long ddipboxId
    );

    @Operation(
        summary = "가게의 띱박스 목록 조회",
        description = "해당 가게의 모든 띱박스 목록을 조회합니다. (비활성화된 것 포함)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "띱박스 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게"
        )
    })
    ApiResponse<List<DdipBoxManagementResponse>> getDdipBoxesByStore(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId
    );
}