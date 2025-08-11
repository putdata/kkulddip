package com.kkulddip.stream.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.stream.dto.request.StreamCreateRequest;
import com.kkulddip.stream.dto.request.StreamJoinRequest;
import com.kkulddip.stream.dto.response.StreamResponse;
import com.kkulddip.stream.dto.response.StreamTokenResponse;
import com.kkulddip.stream.service.StreamManagementService;
import com.kkulddip.stream.service.StreamAccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 스트리밍 관련 API를 처리하는 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/streams")
public class StreamController implements StreamApi {

    private final StreamManagementService streamManagementService;
    private final StreamAccessService streamAccessService;

    /**
     * 새로운 스트림을 생성합니다.
     * Owner 권한을 가진 사용자만 스트림을 생성할 수 있습니다.
     * 
     * @param userInfo JWT 토큰에서 추출된 사용자 정보
     * @param request 스트림 생성 요청 정보
     * @return 생성된 스트림 정보
     */
    @PostMapping
    public ApiResponse<StreamResponse> createStream(
            @AuthenticationPrincipal JwtUserInfo userInfo,
            @Valid @RequestBody StreamCreateRequest request
    ) {
        StreamResponse response = streamManagementService.createStream(Long.parseLong(userInfo.userId()), request);
        return ApiResponse.of(response);
    }

    /**
     * 생성된 스트림을 시작하고 Owner용 OpenVidu 토큰을 발급합니다.
     * 
     * @param userInfo JWT 토큰에서 추출된 사용자 정보
     * @param streamId 시작할 스트림의 ID
     * @return OpenVidu 세션 연결을 위한 토큰 정보
     */
    @PostMapping("/{streamId}/start")
    public ApiResponse<StreamTokenResponse> startStream(
            @AuthenticationPrincipal JwtUserInfo userInfo,
            @PathVariable Long streamId
    ) {
        StreamTokenResponse response = streamManagementService.startStream(Long.parseLong(userInfo.userId()), streamId);
        return ApiResponse.of(response);
    }

    /**
     * 진행 중인 스트림에 Customer로 참가합니다.
     * 스트림이 LIVE 상태일 때만 참가 가능합니다.
     * 
     * @param streamId 참가할 스트림의 ID
     * @param request 스트림 참가 요청 정보 (닉네임 등)
     * @return Customer용 OpenVidu 토큰 정보
     */
    @PostMapping("/{streamId}/join")
    public ApiResponse<StreamTokenResponse> joinStream(
            @PathVariable Long streamId,
            @Valid @RequestBody StreamJoinRequest request
    ) {
        StreamTokenResponse response = streamAccessService.createCustomerToken(streamId, request);
        return ApiResponse.of(response);
    }

    /**
     * 진행 중인 스트림을 종료합니다.
     * Owner만 자신의 스트림을 종료할 수 있습니다.
     * 
     * @param userInfo JWT 토큰에서 추출된 사용자 정보
     * @param streamId 종료할 스트림의 ID
     * @return 빈 응답
     */
    @DeleteMapping("/{streamId}")
    public ApiResponse<Void> endStream(
            @AuthenticationPrincipal JwtUserInfo userInfo,
            @PathVariable Long streamId
    ) {
        streamManagementService.endStream(Long.parseLong(userInfo.userId()), streamId);
        return ApiResponse.of();
    }

    /**
     * 현재 진행 중인 모든 라이브 스트림 목록을 조회합니다.
     * LIVE 상태인 스트림만 반환됩니다.
     * 
     * @return 라이브 스트림 목록
     */
    @GetMapping("/live")
    public ApiResponse<List<StreamResponse>> getLiveStreams() {
        List<StreamResponse> response = streamAccessService.getLiveStreams();
        return ApiResponse.of(response);
    }

    /**
     * 로그인한 Owner의 모든 스트림 목록을 조회합니다.
     * 생성한 모든 스트림(상태 무관)을 반환합니다.
     * 
     * @param userInfo JWT 토큰에서 추출된 사용자 정보
     * @return Owner가 생성한 스트림 목록
     */
    @GetMapping("/my")
    public ApiResponse<List<StreamResponse>> getMyStreams(
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        List<StreamResponse> response = streamManagementService.getOwnerStreams(Long.parseLong(userInfo.userId()));
        return ApiResponse.of(response);
    }

    /**
     * 특정 스트림의 상세 정보를 조회합니다.
     * 
     * @param streamId 조회할 스트림의 ID
     * @return 스트림 상세 정보
     */
    @GetMapping("/{streamId}")
    public ApiResponse<StreamResponse> getStream(@PathVariable Long streamId) {
        StreamResponse response = streamAccessService.getStream(streamId);
        return ApiResponse.of(response);
    }

    /**
     * 특정 스토어의 모든 스트림 목록을 조회합니다.
     * 
     * @param storeId 조회할 스토어의 ID
     * @return 스토어의 모든 스트림 목록 (최신 생성 순)
     */
    @GetMapping("/stores/{storeId}")
    public ApiResponse<List<StreamResponse>> getStoreStreams(@PathVariable Long storeId) {
        List<StreamResponse> response = streamManagementService.getStoreStreams(storeId);
        return ApiResponse.of(response);
    }
}