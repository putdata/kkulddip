package com.kkulddip.example;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/example")
// @Tag(name = "예제 API", description = "API 응답 형식과 에러 처리 예제")
public class ExampleController {
    
    /**
     * 성공 응답 예시 - 데이터 포함
     * 응답: {"success": true, "status": 200, "body": {"name": "홍길동", "email": "hong@example.com"}}
     */
    @GetMapping("/success-with-data")
    // @Operation(summary = "성공 응답 예시 (데이터 포함)", description = "성공적인 응답에 데이터가 포함된 경우의 예시입니다.")
    public ApiResponse<ExampleData> successWithData() {
        ExampleData data = new ExampleData("홍길동", "hong@example.com");
        return ApiResponse.of(data);
    }
    
    /**
     * 성공 응답 예시 - 데이터 없음, 상태 코드 201
     * 응답: {"success": true, "status": 201, "body": null}
     */
    @PostMapping("/success-no-data")
    public ApiResponse<Void> successNoData() {
        // 데이터 저장 로직...
        return ApiResponse.of(201);
    }
    
    /**
     * 사용자 등록 타입 에러 예시
     * POST /api/example/register 
     * Body: {"name": "test", "type": "INVALID"}
     * 
     * 응답:
     * {
     *   "success": false,
     *   "status": "400",
     *   "code": "USER_REGISTER_TYPE_ERROR",
     *   "message": "사용자 피드백",
     *   "timestamp": "2025-01-20T10:30:00",
     *   "body": null
     * }
     */
    @PostMapping("/register")
    public ApiResponse<Void> registerUser(@RequestBody RegisterRequest request) {
        if ("INVALID".equals(request.type())) {
            throw new BusinessException(ErrorCode.USER_REGISTER_TYPE_ERROR, "사용자 피드백");
        }
        
        // 등록 로직...
        return ApiResponse.of(201);
    }
    
    /**
     * 사용자 조회 예시 - 사용자 없음 에러
     * GET /api/example/user/999
     * 
     * 응답:
     * {
     *   "success": false,
     *   "status": "404",
     *   "code": "USER_NOT_FOUND",
     *   "message": "사용자를 찾을 수 없습니다.",
     *   "timestamp": "2025-01-20T10:30:00",
     *   "body": null
     * }
     */
    @GetMapping("/user/{id}")
    public ApiResponse<ExampleData> getUser(@PathVariable Long id) {
        if (id == 999L) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        ExampleData data = new ExampleData("사용자" + id, "user" + id + "@example.com");
        return ApiResponse.of(data);
    }
    
    /**
     * 유효성 검증 실패 예시 (리스트 형식)
     * POST /api/example/validate
     * Body: {"name": "a", "email": "invalid-email", "age": 200, "phoneNumber": "123-456-789"}
     * 
     * 응답:
     * {
     *   "success": false,
     *   "status": "400",
     *   "code": "COMMON_INVALID_INPUT",
     *   "message": "입력값이 올바르지 않습니다.",
     *   "timestamp": "2025-01-20T10:30:00",
     *   "body": [
     *     {
     *       "field": "name",
     *       "message": "이름은 2자 이상 20자 이하여야 합니다.",
     *       "rejectedValue": "a"
     *     },
     *     {
     *       "field": "email",
     *       "message": "올바른 이메일 형식이 아닙니다.",
     *       "rejectedValue": "invalid-email"
     *     },
     *     {
     *       "field": "age",
     *       "message": "나이는 150 이하여야 합니다.",
     *       "rejectedValue": 200
     *     },
     *     {
     *       "field": "phoneNumber",
     *       "message": "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)",
     *       "rejectedValue": "123-456-789"
     *     }
     *   ]
     * }
     */
    @PostMapping("/validate")
    public ApiResponse<Void> validateData(@Valid @RequestBody ValidationRequest request) {
        return ApiResponse.of();
    }
    
    /**
     * 타입 변환 에러 예시
     * GET /api/example/type-error/abc
     * 
     * 응답:
     * {
     *   "success": false,
     *   "status": "400",
     *   "code": "COMMON_INVALID_TYPE",
     *   "message": "abc의 타입이 올바르지 않습니다.",
     *   "timestamp": "2025-01-20T10:30:00",
     *   "body": null
     * }
     */
    @GetMapping("/type-error/{id}")
    public ApiResponse<String> typeError(@PathVariable Long id) {
        return ApiResponse.of("ID: " + id);
    }
    
    // DTO 클래스들
    public record ExampleData(String name, String email) {}
    
    public record RegisterRequest(String name, String type) {}
    
    public record ValidationRequest(
            @NotBlank(message = "이름은 필수입니다.")
            @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다.")
            String name,
            
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email,
            
            @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
            @Max(value = 150, message = "나이는 150 이하여야 합니다.")
            Integer age,
            
            @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
            String phoneNumber
    ) {}
} 