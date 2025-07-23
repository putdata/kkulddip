package com.kkulddip.user.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.user.dto.request.UserCreateRequest;
import com.kkulddip.user.dto.request.UserUpdateRequest;
import com.kkulddip.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Tag(name = "사용자 관리", description = "사용자 관련 API")
public interface UserApi {

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @RequestBody(
        description = "사용자 생성 요청",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserCreateRequest.class),
            examples = {
                @ExampleObject(
                    name = "정상 요청 예시",
                    value = """
                        {
                          "phoneNumber": "01012345678",
                          "notificationEnabled": true
                        }"""
                ),
                @ExampleObject(
                    name = "잘못된 요청 예시",
                    value = """
                        {
                          "phoneNumber": "010-1234-5678",
                          "notificationEnabled": true
                        }"""
                )
            }
        )
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "사용자 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "사용자 생성 성공 응답",
                    value = """
                        {
                          "success": true,
                          "status": 201,
                          "body": {
                            "userId": 1,
                            "phoneNumber": "01012345678",
                            "isVerified": false,
                            "createdAt": "2025-01-20T10:30:00",
                            "updatedAt": "2025-01-20T10:30:00",
                            "notificationEnabled": true
                          }
                        }"""
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 - 유효성 검증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "전화번호 형식 오류",
                        value = """
                            {
                              "success": false,
                              "status": 400,
                              "code": "COMMON_INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다.",
                              "timestamp": "2025-01-20T10:30:00",
                              "body": [
                                {
                                  "field": "phoneNumber",
                                  "message": "올바른 전화번호 형식이 아닙니다",
                                  "rejectedValue": "010-1234-5678"
                                }
                              ]
                            }"""
                    ),
                    @ExampleObject(
                        name = "전화번호 길이 초과",
                        value = """
                            {
                              "success": false,
                              "status": 400,
                              "code": "COMMON_INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다.",
                              "timestamp": "2025-01-20T10:30:00",
                              "body": [
                                {
                                  "field": "phoneNumber",
                                  "message": "전화번호는 11자 이하여야 합니다",
                                  "rejectedValue": "010123456789"
                                },
                                {
                                  "field": "phoneNumber",
                                  "message": "올바른 전화번호 형식이 아닙니다",
                                  "rejectedValue": "010123456789"
                                }
                              ]
                            }"""
                    ),
                    @ExampleObject(
                        name = "필수값 누락",
                        value = """
                            {
                              "success": false,
                              "status": 400,
                              "code": "COMMON_INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다.",
                              "timestamp": "2025-01-20T10:30:00",
                              "body": [
                                {
                                  "field": "phoneNumber",
                                  "message": "전화번호는 필수입니다",
                                  "rejectedValue": null
                                }
                              ]
                            }"""
                    )
                }
            )
        )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<UserResponse> createUser(
        @Valid @org.springframework.web.bind.annotation.RequestBody UserCreateRequest request
    );

    @Operation(summary = "사용자 조회", description = "사용자 ID를 통해 특정 사용자 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "사용자 조회 성공 응답",
                    value = """
                        {
                          "success": true,
                          "status": 200,
                          "body": {
                            "userId": 1,
                            "phoneNumber": "01012345678",
                            "isVerified": true,
                            "createdAt": "2025-01-20T10:30:00",
                            "updatedAt": "2025-01-20T15:45:00",
                            "notificationEnabled": true
                          }
                        }"""
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "사용자를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "사용자 없음 에러",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "USER_NOT_FOUND",
                          "message": "사용자를 찾을 수 없습니다.",
                          "timestamp": "2025-01-20T10:30:00"
                        }"""
                )
            )
        )
    })
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(
        @Parameter(description = "사용자 ID", example = "1") 
        @PathVariable Long userId
    );



    @Operation(
        summary = "사용자 목록 조회", 
        description = """
            사용자 목록을 조회합니다. 다음 조합들이 지원됩니다:
            • 전체 조회: /api/v1/users
            • 페이징만: /api/v1/users?page=0&size=20&sort=userId,desc
            • 인증 상태 필터링: /api/v1/users?isVerified=true
            • 알림 설정 필터링: /api/v1/users?notificationEnabled=false
            • 전화번호로 조회: /api/v1/users?phoneNumber=01012345678
            
            참고:
            • 검색/필터링 결과가 없으면 빈 배열([])을 반환합니다. (404 에러 없음)
            • 복합 필터링(여러 필터 동시 사용)과 필터링+페이징 조합은 현재 지원되지 않습니다.
            """,
        parameters = {
            @Parameter(
                name = "isVerified", 
                description = "인증 여부로 필터링 (true: 인증됨, false: 미인증)", 
                example = "true"
            ),
            @Parameter(
                name = "notificationEnabled", 
                description = "알림 설정으로 필터링 (true: 활성화, false: 비활성화)", 
                example = "true"
            ),
            @Parameter(
                name = "phoneNumber", 
                description = "전화번호로 정확히 일치하는 사용자 조회", 
                example = "01012345678"
            ),
            @Parameter(
                name = "page", 
                description = "페이지 번호 (0부터 시작)", 
                example = "0"
            ),
            @Parameter(
                name = "size", 
                description = "페이지 크기", 
                example = "20"
            ),
            @Parameter(
                name = "sort", 
                description = "정렬 방식 (예: userId,desc 또는 createdAt,asc)", 
                example = "userId,desc"
            )
        }
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "전체 사용자 조회 (페이징 없음)",
                        description = "GET /api/v1/users",
                        value = """
                            {
                              "success": true,
                              "status": 200,
                              "body": [
                                {
                                  "userId": 1,
                                  "phoneNumber": "01012345678",
                                  "isVerified": true,
                                  "createdAt": "2025-01-20T10:30:00",
                                  "updatedAt": "2025-01-20T15:45:00",
                                  "notificationEnabled": true
                                },
                                {
                                  "userId": 2,
                                  "phoneNumber": "01098765432",
                                  "isVerified": false,
                                  "createdAt": "2025-01-20T11:00:00",
                                  "updatedAt": "2025-01-20T11:00:00",
                                  "notificationEnabled": false
                                }
                              ]
                            }"""
                    ),
                    @ExampleObject(
                        name = "인증된 사용자만 조회",
                        description = "GET /api/v1/users?isVerified=true",
                        value = """
                            {
                              "success": true,
                              "status": 200,
                              "body": [
                                {
                                  "userId": 1,
                                  "phoneNumber": "01012345678",
                                  "isVerified": true,
                                  "createdAt": "2025-01-20T10:30:00",
                                  "updatedAt": "2025-01-20T15:45:00",
                                  "notificationEnabled": true
                                }
                              ]
                            }"""
                    ),
                    @ExampleObject(
                        name = "알림 비활성화된 사용자 조회",
                        description = "GET /api/v1/users?notificationEnabled=false",
                        value = """
                            {
                              "success": true,
                              "status": 200,
                              "body": [
                                {
                                  "userId": 2,
                                  "phoneNumber": "01098765432",
                                  "isVerified": false,
                                  "createdAt": "2025-01-20T11:00:00",
                                  "updatedAt": "2025-01-20T11:00:00",
                                  "notificationEnabled": false
                                }
                              ]
                            }"""
                    ),
                    @ExampleObject(
                        name = "전화번호로 사용자 조회",
                        description = "GET /api/v1/users?phoneNumber=01012345678",
                        value = """
                            {
                              "success": true,
                              "status": 200,
                              "body": [
                                {
                                  "userId": 1,
                                  "phoneNumber": "01012345678",
                                  "isVerified": true,
                                  "createdAt": "2025-01-20T10:30:00",
                                  "updatedAt": "2025-01-20T15:45:00",
                                  "notificationEnabled": true
                                }
                              ]
                            }"""
                    ),
                    @ExampleObject(
                        name = "검색 결과 없음 (빈 배열)",
                        description = "GET /api/v1/users?phoneNumber=01099999999",
                        value = """
                            {
                              "success": true,
                              "status": 200,
                              "body": []
                            }"""
                    ),
                    @ExampleObject(
                        name = "필터링 결과 없음",
                        description = "GET /api/v1/users?isVerified=true (인증된 사용자가 없는 경우)",
                        value = """
                            {
                              "success": true,
                              "status": 200,
                              "body": []
                            }"""
                    ),

                    @ExampleObject(
                        name = "페이징만 적용",
                        description = "GET /api/v1/users?page=0&size=20&sort=userId,desc",
                        value = """
                            {
                              "success": true,
                              "status": 200,
                              "body": {
                                "content": [
                                  {
                                    "userId": 2,
                                    "phoneNumber": "01098765432",
                                    "isVerified": false,
                                    "createdAt": "2025-01-20T11:00:00",
                                    "updatedAt": "2025-01-20T11:00:00",
                                    "notificationEnabled": false
                                  },
                                  {
                                    "userId": 1,
                                    "phoneNumber": "01012345678",
                                    "isVerified": true,
                                    "createdAt": "2025-01-20T10:30:00",
                                    "updatedAt": "2025-01-20T15:45:00",
                                    "notificationEnabled": true
                                  }
                                ],
                                "pageable": {
                                  "pageNumber": 0,
                                  "pageSize": 20,
                                  "sort": {
                                    "empty": false,
                                    "sorted": true,
                                    "unsorted": false
                                  }
                                },
                                "totalElements": 2,
                                "totalPages": 1,
                                "last": true,
                                "size": 20,
                                "number": 0,
                                "sort": {
                                  "empty": false,
                                  "sorted": true,
                                  "unsorted": false
                                },
                                "first": true,
                                "numberOfElements": 2,
                                "empty": false
                              }
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 - 복합 필터링 시도",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "복합 필터링 에러 - 인증상태+알림설정",
                        description = "GET /api/v1/users?isVerified=true&notificationEnabled=false",
                        value = """
                            {
                              "success": false,
                              "status": 400,
                              "code": "COMMON_INVALID_INPUT",
                              "message": "복합 필터링은 현재 지원되지 않습니다. 하나의 필터만 사용해주세요.",
                              "timestamp": "2025-01-20T10:30:00"
                            }"""
                    ),
                    @ExampleObject(
                        name = "복합 필터링 에러 - 전화번호+인증상태",
                        description = "GET /api/v1/users?phoneNumber=01012345678&isVerified=true",
                        value = """
                            {
                              "success": false,
                              "status": 400,
                              "code": "COMMON_INVALID_INPUT",
                              "message": "복합 필터링은 현재 지원되지 않습니다. 하나의 필터만 사용해주세요.",
                              "timestamp": "2025-01-20T10:30:00"
                            }"""
                    )
                }
            )
        )
    })
    @GetMapping
    ApiResponse<?> getUsers(
        @Parameter(description = "인증 여부로 필터링", example = "true")
        @org.springframework.web.bind.annotation.RequestParam(required = false) Boolean isVerified,
        
        @Parameter(description = "알림 설정으로 필터링", example = "true")
        @org.springframework.web.bind.annotation.RequestParam(required = false) Boolean notificationEnabled,
        
        @Parameter(description = "전화번호로 정확히 일치하는 사용자 조회", example = "01012345678")
        @org.springframework.web.bind.annotation.RequestParam(required = false) String phoneNumber,
        
        @Parameter(description = "페이징 정보 (page, size, sort)")
        @PageableDefault(size = 20) Pageable pageable
    );

    @Operation(
        summary = "사용자 정보 수정", 
        description = """
            사용자 정보를 수정합니다. 
            
            주요 사용법:
            • 전화번호 변경: {"phoneNumber": "01098765432"}
            • 사용자 인증: {"isVerified": true}
            • 알림 설정 변경: {"notificationEnabled": false}
            • 여러 필드 동시 수정: {"phoneNumber": "01098765432", "isVerified": true, "notificationEnabled": false}
            
            참고: 기존 PATCH /users/{id}/verify API는 제거되었습니다. 
            사용자 인증은 이 API에서 isVerified를 true로 설정하여 수행하세요.
            """
    )
    @RequestBody(
        description = "사용자 수정 요청",
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserUpdateRequest.class),
            examples = {
                @ExampleObject(
                    name = "전화번호 변경 예시",
                    value = """
                        {
                          "phoneNumber": "01098765432",
                          "notificationEnabled": false
                        }"""
                ),
                @ExampleObject(
                    name = "사용자 인증 (기존 PATCH /verify 대체)",
                    value = """
                        {
                          "isVerified": true
                        }"""
                ),
                @ExampleObject(
                    name = "사용자 인증 해제",
                    value = """
                        {
                          "isVerified": false
                        }"""
                ),
                @ExampleObject(
                    name = "전체 정보 수정 예시",
                    value = """
                        {
                          "phoneNumber": "01087654321",
                          "isVerified": true,
                          "notificationEnabled": false
                        }"""
                )
            }
        )
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "수정 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "전화번호 변경 성공 응답",
                        value = """
                            {
                              "success": true,
                              "status": 200,
                              "body": {
                                "userId": 1,
                                "phoneNumber": "01098765432",
                                "isVerified": true,
                                "createdAt": "2025-01-20T10:30:00",
                                "updatedAt": "2025-01-20T16:30:00",
                                "notificationEnabled": false
                              }
                            }"""
                    ),
                    @ExampleObject(
                        name = "사용자 인증 성공 응답",
                        value = """
                            {
                              "success": true,
                              "status": 200,
                              "body": {
                                "userId": 1,
                                "phoneNumber": "01012345678",
                                "isVerified": true,
                                "createdAt": "2025-01-20T10:30:00",
                                "updatedAt": "2025-01-20T16:00:00",
                                "notificationEnabled": true
                              }
                            }"""
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "잘못된 요청 - 유효성 검증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "전화번호 형식 오류",
                    value = """
                        {
                          "success": false,
                          "status": 400,
                          "code": "COMMON_INVALID_INPUT",
                          "message": "입력값이 올바르지 않습니다.",
                          "timestamp": "2025-01-20T10:30:00",
                          "body": [
                            {
                              "field": "phoneNumber",
                              "message": "올바른 전화번호 형식이 아닙니다",
                              "rejectedValue": "010-9876-5432"
                            }
                          ]
                        }"""
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "사용자를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "사용자 없음 에러",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "USER_NOT_FOUND",
                          "message": "사용자를 찾을 수 없습니다.",
                          "timestamp": "2025-01-20T10:30:00"
                        }"""
                )
            )
        )
    })
    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(
        @Parameter(description = "사용자 ID", example = "1") 
        @PathVariable Long userId,
        @Valid @org.springframework.web.bind.annotation.RequestBody UserUpdateRequest request
    );



    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204", 
            description = "삭제 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "사용자 삭제 성공 응답",
                    value = """
                        {
                          "success": true,
                          "status": 204
                        }"""
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "사용자를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "사용자 없음 에러",
                    value = """
                        {
                          "success": false,
                          "status": 404,
                          "code": "USER_NOT_FOUND",
                          "message": "사용자를 찾을 수 없습니다.",
                          "timestamp": "2025-01-20T10:30:00"
                        }"""
                )
            )
        )
    })
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ApiResponse<Void> deleteUser(
        @Parameter(description = "사용자 ID", example = "1") 
        @PathVariable Long userId
    );
} 