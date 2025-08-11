package com.kkulddip.stream.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.store.entity.Store;
import com.kkulddip.store.exception.StoreNotFoundException;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.stream.dto.request.StreamCreateRequest;
import com.kkulddip.stream.dto.response.StreamResponse;
import com.kkulddip.stream.dto.response.StreamTokenResponse;
import com.kkulddip.stream.entity.Stream;
import com.kkulddip.stream.exception.OpenViduConnectionException;
import com.kkulddip.stream.exception.OpenViduSessionException;
import com.kkulddip.stream.exception.StreamNotFoundException;
import com.kkulddip.stream.exception.StreamOwnerMismatchException;
import com.kkulddip.stream.repository.StreamRepository;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 스트림 관리 서비스
 * 
 * 스트림의 생명주기 관리를 담당하는 서비스입니다.
 * 스트림 생성, 시작, 종료와 Owner의 스트림 목록 조회 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StreamManagementService {

    private final StreamRepository streamRepository;
    private final StoreRepository storeRepository;
    private final OpenVidu openVidu;

    /**
     * 새로운 스트림을 생성합니다.
     * 
     * 지정된 스토어에 대해 새로운 스트림을 생성하고, 
     * 스트림의 기본 상태를 READY로 설정합니다.
     * 
     * @param ownerId 스트림을 생성하는 Owner의 ID
     * @param request 스트림 생성 요청 정보 (storeId, title, description 포함)
     * @return 생성된 스트림 정보
     */
    @Transactional
    public StreamResponse createStream(Long ownerId, StreamCreateRequest request) {
        log.info("스트림 생성 요청 - OwnerId: {}, StoreId: {}, Title: {}", 
                ownerId, request.storeId(), request.title());

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new StoreNotFoundException(request.storeId()));
        
        if (!store.getOwnerId().equals(ownerId)) {
            log.warn("스트림 생성 권한 없음 - OwnerId: {}, StoreOwnerId: {}, StoreId: {}", 
                    ownerId, store.getOwnerId(), request.storeId());
            throw new BusinessException(ErrorCode.AUTH_ACCESS_DENIED);
        }

        // Stream 엔티티 생성
        Stream stream = Stream.builder()
                .store(store)
                .title(request.title())
                .description(request.description())
                .build();

        Stream savedStream = streamRepository.save(stream);
        log.info("스트림 생성 완료 - StoreId: {}, StreamId: {}", request.storeId(), savedStream.getId());

        return StreamResponse.from(savedStream);
    }

    /**
     * 스트림을 시작하고 Owner용 OpenVidu 토큰을 생성합니다.
     * 
     * READY 상태의 스트림을 LIVE 상태로 변경하고, Owner가 방송을 송출할 수 있는
     * PUBLISHER 권한의 토큰을 생성합니다. 새로운 OpenVidu 세션을 생성하고
     * 해당 세션 ID를 스트림에 저장합니다.
     * 
     * @param ownerId 스트림을 시작하는 Owner의 ID  
     * @param streamId 시작할 스트림의 ID
     * @return OpenVidu 연결을 위한 토큰과 세션 ID를 포함한 응답
     * 
     * @throws StreamNotFoundException 스트림을 찾을 수 없는 경우
     * @throws StreamOwnerMismatchException 스트림 소유자가 아닌 경우
     * 
     * @since 1.0
     */
    @Transactional
    public StreamTokenResponse startStream(Long ownerId, Long streamId) {
        log.info("스트림 시작 및 Owner 토큰 생성 요청 - OwnerId: {}, StreamId: {}", ownerId, streamId);

        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new StreamNotFoundException(streamId));

        if (!stream.isOwner(ownerId)) {
            throw new StreamOwnerMismatchException(ownerId, streamId);
        }

        String sessionId = generateSessionId();
        
        Session session = createOpenViduSession(sessionId);
        Connection connection = createOwnerConnection(session);
        
        // 스트림 시작 (상태를 LIVE로 변경하고 세션 ID 저장)
        stream.start(sessionId);
        streamRepository.save(stream);
        
        log.info("스트림 시작 및 Owner 토큰 생성 완료 - StreamId: {}, SessionId: {}", streamId, sessionId);
        
        return StreamTokenResponse.builder()
                .token(connection.getToken())
                .sessionId(stream.getSessionId())
                .build();
    }

    /**
     * 진행 중인 스트림을 종료합니다.
     * 
     * 스트림의 소유자만 해당 스트림을 종료할 수 있으며,
     * OpenVidu 세션도 함께 종료됩니다.
     * 
     * @param ownerId 스트림 종료를 요청하는 Owner의 ID
     * @param streamId 종료할 스트림의 ID
     */
    @Transactional
    public void endStream(Long ownerId, Long streamId) {
        log.info("스트림 종료 요청 - OwnerId: {}, StreamId: {}", ownerId, streamId);

        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new StreamNotFoundException(streamId));

        if (!stream.isOwner(ownerId)) {
            throw new StreamOwnerMismatchException(ownerId, streamId);
        }

        if (!stream.isLive()) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_REQUEST);
        }

        closeOpenViduSession(stream.getSessionId(), streamId);

        // OpenVidu 세션 종료가 실패해도 스트림 상태는 종료로 변경
        stream.end();
        streamRepository.save(stream);

        log.info("스트림 종료 완료 - StreamId: {}, SessionId: {}", streamId, stream.getSessionId());
    }

    /**
     * 특정 Owner가 생성한 모든 스트림 목록을 조회합니다.
     * 
     * 지정된 Owner가 생성한 모든 스트림을 생성 시간 내림차순으로 정렬하여 반환합니다.
     * 스트림의 상태(READY, LIVE, ENDED)와 관계없이 모든 스트림을 포함합니다.
     * 
     * @param ownerId 조회할 Owner의 ID
     * @return Owner가 생성한 스트림 목록 (최신 생성 순)
     */
    public List<StreamResponse> getOwnerStreams(Long ownerId) {
        log.debug("Owner 스트림 목록 조회 요청 - OwnerId: {}", ownerId);

        List<Stream> ownerStreams = streamRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
        List<StreamResponse> responses = ownerStreams.stream()
                .map(StreamResponse::from)
                .toList();

        log.info("Owner 스트림 목록 조회 완료 - OwnerId: {}, 개수: {}", ownerId, responses.size());
        return responses;
    }

    /**
     * 특정 Store의 모든 스트림 목록을 조회합니다.
     * 
     * 지정된 Store에서 진행된 모든 스트림을 생성 시간 내림차순으로 정렬하여 반환합니다.
     * 스트림의 상태(READY, LIVE, ENDED)와 관계없이 모든 스트림을 포함합니다.
     * 
     * @param storeId 조회할 Store의 ID
     * @return Store의 모든 스트림 목록 (최신 생성 순)
     */
    public List<StreamResponse> getStoreStreams(Long storeId) {
        log.debug("Store 스트림 목록 조회 요청 - StoreId: {}", storeId);

        storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException(storeId));

        List<Stream> storeStreams = streamRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
        List<StreamResponse> responses = storeStreams.stream()
                .map(StreamResponse::from)
                .toList();

        log.info("Store 스트림 목록 조회 완료 - StoreId: {}, 개수: {}", storeId, responses.size());
        return responses;
    }

    /**
     * 고유한 OpenVidu 세션 ID를 생성합니다.
     *
     * "ses_" 접두사와 32자리 랜덤 문자열을 조합하여
     * 고유한 세션 ID를 생성합니다.
     *
     * @return 생성된 세션 ID
     */
    private String generateSessionId() {
        String sessionId = "ses_" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        log.debug("새 세션 ID 생성: {}", sessionId);
        return sessionId;
    }

    /**
     * OpenVidu 세션을 생성합니다.
     *
     * @param sessionId 생성할 세션 ID
     * @return 생성된 세션
     * @throws OpenViduSessionException 세션 생성 실패 시
     */
    private Session createOpenViduSession(String sessionId) {
        try {
            SessionProperties sessionProperties = new SessionProperties.Builder()
                    .customSessionId(sessionId)
                    .build();
            Session session = openVidu.createSession(sessionProperties);
            log.debug("OpenVidu 세션 생성 성공 - SessionId: {}", sessionId);
            return session;
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("OpenVidu 세션 생성 실패 - SessionId: {}", sessionId, e);
            throw OpenViduSessionException.fromOpenViduException(sessionId, e);
        }
    }

    /**
     * Owner용 연결을 생성합니다.
     *
     * @param session OpenVidu 세션
     * @return 생성된 연결
     * @throws OpenViduConnectionException 연결 생성 실패 시
     */
    private Connection createOwnerConnection(Session session) {
        try {
            ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                    .role(OpenViduRole.PUBLISHER)
                    .data("owner")
                    .build();
            Connection connection = session.createConnection(connectionProperties);
            log.debug("Owner 연결 생성 성공 - SessionId: {}", session.getSessionId());
            return connection;
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("Owner 연결 생성 실패 - SessionId: {}", session.getSessionId(), e);
            throw OpenViduConnectionException.connectionCreationFailed(session.getSessionId(), "PUBLISHER", e);
        }
    }

    /**
     * OpenVidu 세션을 안전하게 종료합니다.
     * 세션 종료 오류는 로깅만 하고 예외를 던지지 않습니다.
     *
     * @param sessionId 종료할 세션 ID
     * @param streamId 스트림 ID (로깅용)
     */
    private void closeOpenViduSession(String sessionId, Long streamId) {
        try {
            Session session = openVidu.getActiveSession(sessionId);
            if (session != null) {
                session.close();
                log.info("OpenVidu 세션 종료 완료 - SessionId: {}", sessionId);
            } else {
                log.warn("세션이 이미 종료되었거나 존재하지 않음 - SessionId: {}", sessionId);
            }
        } catch (OpenViduHttpException e) {
            if (e.getStatus() == 404) {
                log.warn("세션이 이미 종료되었거나 존재하지 않음 - SessionId: {}", sessionId);
            } else {
                log.error("OpenVidu 세션 종료 실패 - StreamId: {}, Status: {}", streamId, e.getStatus(), e);
            }
        } catch (OpenViduJavaClientException e) {
            log.error("OpenVidu 클라이언트 오류 - StreamId: {}", streamId, e);
        }
    }
}