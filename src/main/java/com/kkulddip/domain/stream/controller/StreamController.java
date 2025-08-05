package com.kkulddip.domain.stream.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.domain.stream.dto.request.StreamCreateRequest;
import com.kkulddip.domain.stream.dto.request.StreamJoinRequest;
import com.kkulddip.domain.stream.dto.response.StreamResponse;
import com.kkulddip.domain.stream.dto.response.StreamTokenResponse;
import com.kkulddip.domain.stream.service.StreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Stream", description = "스트리밍 관련 API")
@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    @Operation(summary = "스트림 생성", description = "새로운 스트림을 생성합니다. (Owner 전용)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "스트림 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StreamResponse> createStream(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
            @Valid @RequestBody StreamCreateRequest request
    ) {
        if (userInfo == null) {
            throw new IllegalArgumentException("JWT 인증 정보가 없습니다.");
        }
        StreamResponse response = streamService.createStream(userInfo.userId(), request);
        return ApiResponse.of(HttpStatus.CREATED.value(), response);
    }

    @Operation(summary = "스트림 시작", description = "생성된 스트림을 시작하고 Owner용 토큰을 발급합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스트림을 찾을 수 없음")
    })
    @PostMapping("/{streamId}/start")
    public ApiResponse<StreamTokenResponse> startStream(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
            @PathVariable Long streamId
    ) {
        if (userInfo == null) {
            throw new IllegalArgumentException("JWT 인증 정보가 없습니다.");
        }
        StreamTokenResponse response = streamService.createOwnerToken(userInfo.userId(), streamId);
        return ApiResponse.of(response);
    }

    @Operation(summary = "스트림 참가", description = "진행 중인 스트림에 Customer로 참가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "참가 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "스트림이 진행 중이지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스트림을 찾을 수 없음")
    })
    @PostMapping("/join")
    public ApiResponse<StreamTokenResponse> joinStream(
            @Valid @RequestBody StreamJoinRequest request
    ) {
        StreamTokenResponse response = streamService.createCustomerToken(request);
        return ApiResponse.of(response);
    }

    @Operation(summary = "스트림 종료", description = "진행 중인 스트림을 종료합니다. (Owner 전용)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스트림 종료 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스트림을 찾을 수 없음")
    })
    @DeleteMapping("/{streamId}")
    public ApiResponse<Void> endStream(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
            @PathVariable Long streamId
    ) {
        if (userInfo == null) {
            throw new IllegalArgumentException("JWT 인증 정보가 없습니다.");
        }
        streamService.endStream(userInfo.userId(), streamId);
        return ApiResponse.of();
    }

    @Operation(summary = "라이브 스트림 목록 조회", description = "현재 진행 중인 모든 스트림 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/live")
    public ApiResponse<List<StreamResponse>> getLiveStreams() {
        List<StreamResponse> response = streamService.getLiveStreams();
        return ApiResponse.of(response);
    }

    @Operation(summary = "내 스트림 목록 조회", description = "로그인한 Owner의 모든 스트림 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/my")
    public ApiResponse<List<StreamResponse>> getMyStreams(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        if (userInfo == null) {
            throw new IllegalArgumentException("JWT 인증 정보가 없습니다.");
        }
        List<StreamResponse> response = streamService.getOwnerStreams(userInfo.userId());
        return ApiResponse.of(response);
    }

    @Operation(summary = "스트림 상세 정보 조회", description = "특정 스트림의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스트림을 찾을 수 없음")
    })
    @GetMapping("/{streamId}")
    public ApiResponse<StreamResponse> getStream(@PathVariable Long streamId) {
        StreamResponse response = streamService.getStream(streamId);
        return ApiResponse.of(response);
    }
}