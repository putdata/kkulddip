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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 가게 관리 API 인터페이스
 * Swagger 문서화를 위한 인터페이스
 */
@Tag(name = "Store Management", description = "가게 관리 API (사장님 전용) - JWT 토큰 인증 필수")
@SecurityRequirement(name = "bearerAuth")
public interface StoreManagementApi {

    @Operation(
        summary = "가게 등록",
        description = """
        새로운 가게를 등록합니다. 사장님만 사용할 수 있습니다.
        
        **요청 구조:**
        ```json
        {
          "storeName": "친환경 농장 가게",
          "storeAddress": "서울시 강남구 테헤란로 123",
          "description": "신선한 유기농 채소를 판매합니다",
          "operatingHours": "09:00-18:00",
          "phone": "02-1234-5678",
          "businessNumber": "123-45-67890",
          "latitude": 37.5665,
          "longitude": 126.9780
        }
        ```
        
        **응답 구조:**
        ```json
        {
          "success": true,
          "status": 201,
          "body": {
            "storeId": 1,
            "ownerId": 1,
            "storeName": "친환경 농장 가게",
            "phone": "02-1234-5678",
            "description": "신선한 유기농 채소를 판매합니다",
            "operatingHours": "09:00-18:00",
            "isActive": true,
            "ratingAverage": 0.0,
            "reviewCount": 0,
            "businessNumber": "123-45-67890",
            "storeAddress": "서울시 강남구 테헤란로 123",
            "storeProfileImage": null,
            "latitude": 37.5665,
            "longitude": 126.9780,
            "createdAt": "2025-01-10T15:30:00",
            "updatedAt": "2025-01-10T15:30:00"
          }
        }
        ```
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "가게 등록 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":201,\"body\":{\"storeId\":1,\"ownerId\":1,\"storeName\":\"친환경 농장 가게\",\"phone\":\"02-1234-5678\",\"isActive\":true,\"ratingAverage\":0.0,\"reviewCount\":0,\"storeAddress\":\"서울시 강남구\",\"latitude\":37.5665,\"longitude\":126.9780}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터 - 필수 필드 누락, 유효성 검증 실패",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":400,\"message\":\"가게명은 필수입니다\",\"errors\":[{\"field\":\"storeName\",\"message\":\"가게명은 필수입니다\"}]}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패 - JWT 토큰 없음 또는 만료",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":401,\"message\":\"인증되지 않은 사용자입니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 - 사장님 권한 필요",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":403,\"message\":\"사장님만 접근할 수 있습니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "중복된 가게명 또는 사업자번호",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":409,\"message\":\"이미 등록된 가게명입니다\"}"
                )
            )
        )
    })
    ApiResponse<StoreManagementResponse> createStore(
        @Parameter(
            description = "가게 등록 정보", 
            required = true,
            schema = @Schema(
                example = "{\"storeName\":\"친환경 농장 가게\",\"storeAddress\":\"서울시 강남구 테헤란로 123\",\"description\":\"신선한 유기농 채소를 판매합니다\",\"operatingHours\":\"09:00-18:00\",\"phone\":\"02-1234-5678\",\"businessNumber\":\"123-45-67890\",\"latitude\":37.5665,\"longitude\":126.9780}"
            )
        )
        @Valid @RequestBody CreateStoreRequest request
    );

    @Operation(
        summary = "가게 등록 (이미지 포함)",
        description = """
        이미지와 함께 새로운 가게를 등록합니다.
        
        **요청:** Multipart Form Data
        - `request` (application/json): 가게 정보 JSON
        - `image` (image/*): 가게 프로필 이미지 (선택사항)
        
        **지원 이미지 형식:** JPG, PNG, GIF, WEBP
        **최대 파일 크기:** 10MB
        
        **응답:** 가게 등록과 동일하며, `storeProfileImage` 필드에 S3 URL 포함
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "가게 등록 성공 (이미지 포함)",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":201,\"body\":{\"storeId\":1,\"storeName\":\"친환경 농장 가게\",\"storeProfileImage\":\"https://s3.amazonaws.com/bucket/store/profile/1.jpg\",\"isActive\":true}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 이미지 파일 또는 요청 데이터",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":400,\"message\":\"지원하지 않는 이미지 형식입니다\"}"
                )
            )
        )
    })
    ApiResponse<StoreManagementResponse> createStoreWithImage(
        @Parameter(
            description = "가게 등록 정보 (JSON)",
            required = true
        )
        @RequestPart("request") @Valid CreateStoreRequest request,
        
        @Parameter(
            description = "가게 프로필 이미지 (선택사항)",
            content = @Content(mediaType = "image/*")
        )
        @RequestPart(value = "image", required = false) MultipartFile image
    );

    @Operation(
        summary = "가게 정보 수정",
        description = """
        기존 가게의 정보를 수정합니다. 해당 가게의 사장님만 수정할 수 있습니다.
        
        **수정 가능한 필드:**
        - storeName: 가게명
        - description: 가게 설명
        - operatingHours: 운영시간
        - phone: 전화번호
        - storeAddress: 주소
        - latitude, longitude: 좌표
        
        **수정 불가능한 필드:**
        - businessNumber: 사업자번호 (등록 후 변경 불가)
        - ownerId: 사장님 ID
        - ratingAverage, reviewCount: 시스템 관리 필드
        
        **요청 구조:**
        ```json
        {
          "storeName": "수정된 가게명",
          "description": "수정된 가게 설명",
          "operatingHours": "08:00-20:00",
          "phone": "02-9876-5432"
        }
        ```
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 정보 수정 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"storeId\":1,\"storeName\":\"수정된 가게명\",\"description\":\"수정된 가게 설명\",\"operatingHours\":\"08:00-20:00\",\"phone\":\"02-9876-5432\",\"updatedAt\":\"2025-01-10T16:00:00\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":400,\"message\":\"전화번호 형식이 올바르지 않습니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":401,\"message\":\"인증되지 않은 사용자입니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 - 해당 가게의 사장님이 아님",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":403,\"message\":\"해당 가게를 수정할 권한이 없습니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":404,\"message\":\"존재하지 않는 가게입니다\"}"
                )
            )
        )
    })
    ApiResponse<StoreManagementResponse> updateStore(
        @Parameter(
            description = "가게 ID", 
            required = true, 
            example = "1",
            schema = @Schema(minimum = "1")
        )
        @PathVariable Long storeId,
        
        @Parameter(
            description = "수정할 가게 정보",
            schema = @Schema(
                example = "{\"storeName\":\"수정된 가게명\",\"description\":\"수정된 가게 설명\",\"operatingHours\":\"08:00-20:00\",\"phone\":\"02-9876-5432\"}"
            )
        )
        @Valid @RequestBody UpdateStoreRequest request
    );

    @Operation(
        summary = "가게 활성화/비활성화",
        description = """
        가게의 영업 상태를 변경합니다.
        
        **비활성화 시 제한사항:**
        - 활성 주문이 있으면 비활성화할 수 없습니다
        - 비활성화된 가게는 고객에게 노출되지 않습니다
        - 기존 주문 처리는 계속 가능합니다
        
        **요청 구조:**
        ```json
        {
          "isActive": false,
          "reason": "임시 휴업"
        }
        ```
        
        **응답:** 변경된 가게 정보 전체 반환
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 상태 변경 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"storeId\":1,\"storeName\":\"친환경 농장 가게\",\"isActive\":false,\"updatedAt\":\"2025-01-10T16:30:00\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":400,\"message\":\"상태 변경 사유는 필수입니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 - 해당 가게의 사장님이 아님"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "활성 주문이 있어 비활성화할 수 없음",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":409,\"message\":\"처리 중인 주문이 있어 가게를 비활성화할 수 없습니다\"}"
                )
            )
        )
    })
    ApiResponse<StoreManagementResponse> updateStoreStatus(
        @Parameter(
            description = "가게 ID", 
            required = true, 
            example = "1"
        )
        @PathVariable Long storeId,
        
        @Parameter(
            description = "변경할 상태 정보", 
            required = true,
            schema = @Schema(
                example = "{\"isActive\":false,\"reason\":\"임시 휴업\"}"
            )
        )
        @Valid @RequestBody UpdateStoreStatusRequest request
    );

    @Operation(
        summary = "가게 이미지 수정",
        description = """
        가게 프로필 이미지를 수정합니다.
        
        **기능:**
        - 기존 이미지를 새 이미지로 교체
        - 기존 S3 이미지는 자동으로 삭제
        - 이미지가 없으면 기존 이미지 유지
        
        **지원 형식:** JPG, PNG, GIF, WEBP
        **최대 크기:** 10MB
        
        **응답:** 업데이트된 가게 정보 (storeProfileImage 포함)
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "이미지 수정 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200,\"body\":{\"storeId\":1,\"storeProfileImage\":\"https://s3.../updated-image.jpg\",\"updatedAt\":\"2025-01-10T17:00:00\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 이미지 파일",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":400,\"message\":\"파일 크기가 너무 큽니다 (최대 10MB)\"}"
                )
            )
        )
    })
    ApiResponse<StoreManagementResponse> updateStoreImage(
        @Parameter(
            description = "가게 ID", 
            required = true, 
            example = "1"
        )
        @PathVariable Long storeId,
        
        @Parameter(
            description = "새 프로필 이미지 (선택사항)",
            content = @Content(mediaType = "image/*")
        )
        @RequestPart(value = "image", required = false) MultipartFile image
    );

    @Operation(
        summary = "가게 삭제",
        description = """
        가게를 논리적으로 삭제(비활성화)합니다.
        
        **삭제 제한사항:**
        - 활성 주문이 있으면 삭제할 수 없습니다
        - 삭제된 가게는 복구할 수 없습니다
        - 기존 주문 데이터는 보존됩니다
        
        **삭제 과정:**
        1. 활성 주문 확인
        2. 가게 상태를 비활성화
        3. S3 프로필 이미지 삭제
        4. 관련 띱박스도 비활성화
        
        **응답:** 성공 시 200 상태코드와 기본 성공 메시지
        ```json
        {
          "success": true,
          "status": 200
        }
        ```
        """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "가게 삭제 성공",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":true,\"status\":200}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":401,\"message\":\"인증되지 않은 사용자입니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 - 해당 가게의 사장님이 아님",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":403,\"message\":\"해당 가게를 삭제할 권한이 없습니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가게",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":404,\"message\":\"존재하지 않는 가게입니다\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "활성 주문이 있어 삭제할 수 없음",
            content = @Content(
                schema = @Schema(
                    example = "{\"success\":false,\"status\":409,\"message\":\"처리 중인 주문이 있어 가게를 삭제할 수 없습니다\"}"
                )
            )
        )
    })
    ApiResponse<Void> deleteStore(
        @Parameter(
            description = "가게 ID", 
            required = true, 
            example = "1",
            schema = @Schema(minimum = "1")
        )
        @PathVariable Long storeId
    );
}