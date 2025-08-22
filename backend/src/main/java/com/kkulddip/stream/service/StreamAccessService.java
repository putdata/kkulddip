package com.kkulddip.stream.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.stream.dto.request.StreamJoinRequest;
import com.kkulddip.stream.dto.response.StreamResponse;
import com.kkulddip.stream.dto.response.StreamTokenResponse;
import com.kkulddip.stream.entity.Stream;
import com.kkulddip.stream.exception.OpenViduConnectionException;
import com.kkulddip.stream.exception.StreamNotFoundException;
import com.kkulddip.stream.repository.StreamRepository;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 스트림 접근 서비스
 * 
 * OpenVidu 토큰 생성과 스트림 접근 관련 작업을 담당하는 서비스입니다.
 * Owner 토큰 생성(스트림 시작), Customer 토큰 생성(스트림 참가) 기능을 제공합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StreamAccessService {

    private final StreamRepository streamRepository;
    private final OpenVidu openVidu;

    /**
     * 특정 스트림의 상세 정보를 조회합니다.
     *
     * 스트림의 모든 정보를 포함하여 반환합니다.
     *
     * @param streamId 조회할 스트림의 ID
     * @return 스트림 상세 정보
     */
    public StreamResponse getStream(Long streamId) {
        log.debug("스트림 상세 정보 조회 요청 - StreamId: {}", streamId);

        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new StreamNotFoundException(streamId));

        StreamResponse response = StreamResponse.from(stream);
        log.info("스트림 상세 정보 조회 완료 - StreamId: {}", streamId);

        return response;
    }

    /**
     * Customer용 OpenVidu 토큰을 생성합니다.
     * 
     * 진행 중인 라이브 스트림에 시청자로 참가할 수 있는 
     * SUBSCRIBER 권한의 토큰을 생성합니다. 스트림이 LIVE 상태일 때만
     * 토큰 생성이 가능합니다.
     * 
     * @param streamId 참가할 스트림의 ID
     * @param request 스트림 참가 요청 정보 (현재는 빈 객체)
     * @return OpenVidu 연결을 위한 토큰과 세션 ID를 포함한 응답
     */
    public StreamTokenResponse createCustomerToken(Long streamId, StreamJoinRequest request) {
        log.info("Customer 토큰 생성 요청 - StreamId: {}", streamId);

        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new StreamNotFoundException(streamId));

        if (!stream.isLive()) {
            log.warn("스트림이 라이브 상태가 아님 - StreamId: {}, Status: {}", 
                    streamId, stream.getStatus());
            throw new BusinessException(ErrorCode.COMMON_INVALID_REQUEST);
        }

        Session session = getActiveOpenViduSession(stream.getSessionId());
        Connection connection = createCustomerConnection(session);
        
        log.info("Customer 토큰 생성 완료 - StreamId: {}, SessionId: {}", 
                streamId, stream.getSessionId());
        
        return StreamTokenResponse.builder()
                .token(connection.getToken())
                .sessionId(stream.getSessionId())
                .build();
    }

    /**
     * 현재 진행 중인 모든 라이브 스트림 목록을 조회합니다.
     * 
     * LIVE 상태인 모든 스트림을 시작 시간 내림차순으로 정렬하여 반환합니다.
     * 시청자들이 현재 시청 가능한 스트림을 확인할 때 사용됩니다.
     * 
     * @return 진행 중인 라이브 스트림 목록 (최근 시작 순)
     */
    public List<StreamResponse> getLiveStreams() {
        log.debug("진행 중인 라이브 스트림 목록 조회 요청");

        List<Stream> liveStreams = streamRepository.findLiveStreamsOrderByStartedAtDesc();
        List<StreamResponse> responses = liveStreams.stream()
                .map(StreamResponse::from)
                .toList();

        log.info("진행 중인 라이브 스트림 목록 조회 완료 - 개수: {}", responses.size());
        return responses;
    }

    /**
     * 활성 OpenVidu 세션을 조회합니다.
     *
     * @param sessionId 세션 ID
     * @return 활성 세션
     * @throws BusinessException 세션을 찾을 수 없는 경우
     */
    private Session getActiveOpenViduSession(String sessionId) {
        Session session = openVidu.getActiveSession(sessionId);
        if (session == null) {
            log.warn("OpenVidu 세션을 찾을 수 없음 - SessionId: {}", sessionId);
            throw new BusinessException(ErrorCode.COMMON_ENTITY_NOT_FOUND);
        }
        log.debug("OpenVidu 세션 조회 성공 - SessionId: {}", sessionId);
        return session;
    }

    /**
     * Customer용 연결을 생성합니다.
     *
     * @param session OpenVidu 세션
     * @return 생성된 연결
     * @throws OpenViduConnectionException 연결 생성 실패 시
     */
    private Connection createCustomerConnection(Session session) {
        try {
            ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                    .role(OpenViduRole.SUBSCRIBER)
                    .data("customer")
                    .build();
            Connection connection = session.createConnection(connectionProperties);
            log.debug("Customer 연결 생성 성공 - SessionId: {}", session.getSessionId());
            return connection;
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("Customer 연결 생성 실패 - SessionId: {}", session.getSessionId(), e);
            throw OpenViduConnectionException.connectionCreationFailed(session.getSessionId(), "SUBSCRIBER", e);
        }
    }
}