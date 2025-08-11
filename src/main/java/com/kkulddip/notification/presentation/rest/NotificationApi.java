package com.kkulddip.notification.presentation.rest;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 알림 관리 API 인터페이스
 * Swagger 문서화를 위한 API 명세
 *
 * @author Claude
 * @since 1.0
 */
@Tag(name = "Notification API", description = "알림 관리 API")
public interface NotificationApi {

    @Operation(
        summary = "알림 생성",
        description = """
            새로운 알림을 생성하여 Redis 큐에 발행합니다.
            
            **알림 타입별 동작:**
            - `ALL`: 모든 사용자(고객+사장)에게 브로드캐스트
            - `CUSTOMER`: subscriberId가 있으면 특정 고객, 없으면 모든 고객
            - `OWNER`: subscriberId가 있으면 특정 가게 사장, 없으면 모든 사장
            - `SPECIFIC`: subscriberId로 지정된 특정 사용자
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "알림 요청 정보",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationRequest.class),
                examples = {
                    @ExampleObject(
                        name = "특정 고객 알림",
                        description = "특정 고객에게 주문 완료 알림",
                        value = """
                            {
                              "title": "주문이 완료되었습니다",
                              "content": "주문번호 #12345 결제가 성공적으로 처리되었습니다.",
                              "publisherId": 1001,
                              "publisherType": "SYSTEM",
                              "subscriberId": 2001,
                              "subscriberType": "CUSTOMER",
                              "notificationType": "ORDER_COMPLETE",
                              "actionUrl": "/orders/12345"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "특정 가게 사장 알림",
                        description = "특정 가게에 새 주문 알림",
                        value = """
                            {
                              "title": "새로운 주문이 들어왔습니다",
                              "content": "고객님이 치킨 2마리를 주문하셨습니다.",
                              "publisherId": 1001,
                              "publisherType": "SYSTEM",
                              "subscriberId": 3001,
                              "subscriberType": "OWNER",
                              "notificationType": "NEW_ORDER",
                              "actionUrl": "/orders/manage"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "전체 고객 브로드캐스트",
                        description = "모든 고객에게 시스템 공지",
                        value = """
                            {
                              "title": "시스템 점검 안내",
                              "content": "오늘 밤 2시-4시 시스템 점검이 예정되어 있습니다.",
                              "publisherId": 1001,
                              "publisherType": "SYSTEM",
                              "subscriberType": "CUSTOMER",
                              "notificationType": "SYSTEM_NOTICE"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "알림 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "success": true,
                          "status": 201,
                          "body": {
                            "title": "주문이 완료되었습니다",
                            "content": "주문번호 #12345 결제가 성공적으로 처리되었습니다.",
                            "publisherId": 1001,
                            "publisherType": "SYSTEM",
                            "subscriberId": 2001,
                            "subscriberType": "CUSTOMER",
                            "notificationType": "ORDER_COMPLETE",
                            "actionUrl": "/orders/12345",
                            "isSent": false
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Redis 발행 실패",
            content = @Content(mediaType = "application/json")
        )
    })
    ApiResponse<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request);

    @Operation(
        summary = "사용자별 알림 목록 조회",
        description = """
            JWT 토큰으로 인증된 사용자의 알림 목록을 조회합니다.
            
            **권한 제어:**
            - **Customer**: 자신의 user_id와 일치하는 CUSTOMER 알림만 조회 가능
            - **Owner**: 자신이 관리하는 가게의 OWNER 알림만 조회 가능
            
            **조회 예시:**
            - Customer (user_id=123): `subscriberId=123&subscriberType=CUSTOMER`
            - Owner (관리하는 storeId=456): `subscriberId=456&subscriberType=OWNER`
            """,
        security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "알림 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "success": true,
                          "status": 200,
                          "body": [
                            {
                              "notificationId": 1,
                              "title": "주문이 완료되었습니다",
                              "content": "주문번호 #12345 결제가 성공적으로 처리되었습니다.",
                              "publisherId": 1001,
                              "publisherType": "SYSTEM",
                              "subscriberId": 2001,
                              "subscriberType": "CUSTOMER",
                              "notificationType": "ORDER_COMPLETE",
                              "actionUrl": "/orders/12345",
                              "createdAt": "2024-08-11T10:30:00",
                              "sentAt": "2024-08-11T10:30:05",
                              "isSent": true
                            },
                            {
                              "notificationId": 2,
                              "title": "배송이 시작되었습니다",
                              "content": "주문하신 상품이 배송 준비 중입니다.",
                              "publisherId": 1001,
                              "publisherType": "SYSTEM",
                              "subscriberId": 2001,
                              "subscriberType": "CUSTOMER",
                              "notificationType": "DELIVERY_START",
                              "createdAt": "2024-08-11T11:00:00",
                              "sentAt": "2024-08-11T11:00:03",
                              "isSent": true
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패 - JWT 토큰이 유효하지 않음",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 - 요청한 알림에 접근할 권한이 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "success": false,
                          "status": 403,
                          "message": "알림을 조회할 권한이 없습니다."
                        }
                        """
                )
            )
        )
    })
    ApiResponse<List<NotificationResponse>> getNotificationsBySubscriber(
        @Parameter(
            description = "구독자 ID - Customer의 경우 user_id, Owner의 경우 store_id",
            example = "2001"
        ) @RequestParam Long subscriberId,
        
        @Parameter(
            description = "구독자 타입",
            example = "CUSTOMER"
        ) @RequestParam SubscriberType subscriberType,
        
        @AuthenticationPrincipal JwtUserInfo userInfo
    );
}