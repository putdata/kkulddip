package com.kkulddip.customerProfile.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.customerProfile.dto.request.UpdateLocationRequest;
import com.kkulddip.customerProfile.dto.request.UpdateProfileRequest;
import com.kkulddip.customerProfile.dto.response.CustomerProfileResponse;
import com.kkulddip.customerProfile.dto.response.CustomerStatsResponse;
import com.kkulddip.customerProfile.dto.response.UpdateLocationResponse;
import com.kkulddip.customerProfile.dto.response.UpdateProfileResponse;
import com.kkulddip.customerProfile.location.dto.CustomerLocationDto;
import com.kkulddip.customerProfile.location.dto.LocationDistanceResponse;
import com.kkulddip.customerProfile.location.dto.UpdateRealtimeLocationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "고객 프로필", description = """
    고객 프로필 관리 API
    
    ## 주요 기능
    - 프로필 조회/수정
    - 위치 정보 관리 (주소 및 실시간 위치)
    - 고객 통계 및 레벨 시스템
    - Redis 기반 실시간 위치 추적
    
    ## 고객 레벨 시스템
    - SPROUT_BEE (새싹벌): 시작 레벨 (0-9 주문)
    - WORKER_BEE (일벌): 10-29 주문
    - HONEY_BEE (꿀벌): 30-49 주문  
    - QUEEN_BEE (여왕벌): 50+ 주문
    """)
@SecurityRequirement(name = "bearerAuth")
public interface CustomerProfileApi {
    
    @Operation(
        summary = "내 프로필 조회",
        description = "현재 로그인한 고객의 프로필 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "프로필 조회 성공",
            content = @Content(schema = @Schema(implementation = CustomerProfileResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "고객 정보를 찾을 수 없음"
        )
    })
    @GetMapping("/profile")
    ApiResponse<CustomerProfileResponse> getMyProfile();
    
    @Operation(
        summary = "프로필 수정",
        description = "현재 로그인한 고객의 프로필 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "프로필 수정 성공",
            content = @Content(schema = @Schema(implementation = UpdateProfileResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "고객 정보를 찾을 수 없음"
        )
    })
    @PutMapping("/profile")
    ApiResponse<UpdateProfileResponse> updateProfile(
        @Parameter(description = "프로필 수정 요청", required = true)
        @Valid @RequestBody UpdateProfileRequest request
    );
    
    @Operation(
        summary = "위치 정보 업데이트",
        description = "현재 로그인한 고객의 위치 정보를 업데이트합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "위치 정보 업데이트 성공",
            content = @Content(schema = @Schema(implementation = UpdateLocationResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 위치 정보"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "고객 정보를 찾을 수 없음"
        )
    })
    @PutMapping("/location")
    ApiResponse<UpdateLocationResponse> updateLocation(
        @Parameter(description = "위치 정보 수정 요청", required = true)
        @Valid @RequestBody UpdateLocationRequest request
    );
    
    @Operation(
        summary = "고객 통계 조회",
        description = "현재 로그인한 고객의 통계 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "통계 조회 성공",
            content = @Content(schema = @Schema(implementation = CustomerStatsResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "고객 정보를 찾을 수 없음"
        )
    })
    @GetMapping("/stats")
    ApiResponse<CustomerStatsResponse> getMyStats();
    
    @Operation(
        summary = "실시간 위치 업데이트",
        description = """
            현재 로그인한 고객의 실시간 위치를 업데이트합니다.
            
            ## 주요 특징
            - Redis GeoSpatial 데이터 구조 사용
            - 5분 TTL (Time To Live) 적용
            - 최소 30초 업데이트 간격 제한
            - 위치 공유 활성화 시에만 동작
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "위치 업데이트 성공",
            content = @Content(schema = @Schema(implementation = com.kkulddip.customerProfile.location.dto.CustomerLocationDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 위치 정보"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "실시간 위치 업데이트 요청",
        required = true,
        content = @Content(schema = @Schema(implementation = com.kkulddip.customerProfile.location.dto.UpdateRealtimeLocationRequest.class))
    )
    @PostMapping("/location/realtime")
    ApiResponse<CustomerLocationDto> updateRealtimeLocation(
        @Parameter(description = "실시간 위치 업데이트 요청", required = true)
        @Valid @RequestBody UpdateRealtimeLocationRequest request
    );
    
    @Operation(
        summary = "현재 위치 조회",
        description = "현재 로그인한 고객의 실시간 위치를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "위치 조회 성공",
            content = @Content(schema = @Schema(implementation = CustomerLocationDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "위치 정보를 찾을 수 없음"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        )
    })
    @GetMapping("/location/realtime")
    ApiResponse<CustomerLocationDto> getRealtimeLocation();
    
    @Operation(
        summary = "가게까지 거리 계산",
        description = """
            현재 위치에서 특정 가게까지의 거리를 계산합니다.
            
            ## 계산 방식
            - Redis GeoSpatial DISTANCE 연산 사용
            - 100m 이내 시 근처 도착으로 판정
            - 예상 도착 시간: 거리(km) × 3분 (도보 기준)
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "거리 계산 성공",
            content = @Content(schema = @Schema(implementation = LocationDistanceResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "위치 정보 또는 가게를 찾을 수 없음"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        )
    })
    @GetMapping("/location/distance/store/{storeId}")
    ApiResponse<LocationDistanceResponse> calculateDistanceToStore(
        @Parameter(description = "가게 ID", required = true, example = "1")
        @PathVariable Long storeId
    );
    
    @Operation(
        summary = "위치 공유 중지",
        description = "현재 로그인한 고객의 실시간 위치 공유를 중지하고 위치 정보를 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "위치 공유 중지 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        )
    })
    @DeleteMapping("/location/realtime")
    ApiResponse<Void> stopLocationSharing();
}