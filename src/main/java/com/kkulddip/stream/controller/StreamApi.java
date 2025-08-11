package com.kkulddip.stream.controller;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.stream.dto.request.StreamCreateRequest;
import com.kkulddip.stream.dto.request.StreamJoinRequest;
import com.kkulddip.stream.dto.response.StreamResponse;
import com.kkulddip.stream.dto.response.StreamTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Stream", description = "스트리밍 관련 API")
public interface StreamApi {

    @Operation(summary = "스트림 생성", description = "특정 스토어에 새로운 스트림을 생성합니다. 스토어별로 하나의 스트림만 생성 가능합니다. (Owner 전용)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스트림 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (스토어 ID 누락, 이미 존재하는 스트림 등)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "스토어 접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스토어를 찾을 수 없음")
    })
    ApiResponse<StreamResponse> createStream(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
            @Valid @RequestBody StreamCreateRequest request
    );

    @Operation(summary = "스트림 시작", description = "생성된 스트림을 시작하고 Owner용 토큰을 발급합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스트림을 찾을 수 없음")
    })
    ApiResponse<StreamTokenResponse> startStream(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
            @PathVariable Long streamId
    );

    @Operation(summary = "스트림 참가", description = "진행 중인 스트림에 Customer로 참가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "참가 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "스트림이 진행 중이지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스트림을 찾을 수 없음")
    })
    ApiResponse<StreamTokenResponse> joinStream(
            @PathVariable Long streamId,
            @Valid @RequestBody StreamJoinRequest request
    );

    @Operation(summary = "스트림 종료", description = "진행 중인 스트림을 종료합니다. (Owner 전용)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "스트림 종료 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스트림을 찾을 수 없음")
    })
    ApiResponse<Void> endStream(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo,
            @PathVariable Long streamId
    );

    @Operation(summary = "라이브 스트림 목록 조회", description = "현재 진행 중인 모든 스트림 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ApiResponse<List<StreamResponse>> getLiveStreams();

    @Operation(summary = "내 스트림 목록 조회", description = "로그인한 Owner의 모든 스트림 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ApiResponse<List<StreamResponse>> getMyStreams(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserInfo userInfo
    );

    @Operation(summary = "스트림 상세 정보 조회", description = "특정 스트림의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스트림을 찾을 수 없음")
    })
    ApiResponse<StreamResponse> getStream(@PathVariable Long streamId);

    @Operation(summary = "스토어별 스트림 목록 조회", description = "특정 스토어의 모든 스트림 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "스토어를 찾을 수 없음")
    })
    ApiResponse<List<StreamResponse>> getStoreStreams(@PathVariable Long storeId);
}