package com.kkulddip.domain.stream.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.stream.dto.request.StreamCreateRequest;
import com.kkulddip.domain.stream.dto.request.StreamJoinRequest;
import com.kkulddip.domain.stream.dto.response.StreamResponse;
import com.kkulddip.domain.stream.dto.response.StreamTokenResponse;
import com.kkulddip.domain.stream.entity.Stream;
import com.kkulddip.domain.stream.repository.StreamRepository;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StreamService {

    private final StreamRepository streamRepository;
    private final OwnerRepository ownerRepository;
    private final OpenVidu openVidu;

    @Transactional
    public StreamResponse createStream(Long ownerId, StreamCreateRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String sessionId = generateSessionId();

        try {
            // OpenVidu 세션 생성
            SessionProperties sessionProperties = new SessionProperties.Builder()
                    .customSessionId(sessionId)
                    .build();
            Session session = openVidu.createSession(sessionProperties);

            // Stream 엔티티 생성
            Stream stream = Stream.builder()
                    .sessionId(sessionId)
                    .title(request.title())
                    .description(request.description())
                    .owner(owner)
                    .build();

            Stream savedStream = streamRepository.save(stream);
            log.info("스트림 생성 완료 - SessionId: {}, StreamId: {}", sessionId, savedStream.getId());

            return StreamResponse.from(savedStream);

        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("OpenVidu 세션 생성 실패 - SessionId: {}", sessionId, e);
            throw new BusinessException(ErrorCode.COMMON_INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public StreamTokenResponse createOwnerToken(Long ownerId, Long streamId) {
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_ENTITY_NOT_FOUND));

        if (!stream.getOwner().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.COMMON_ACCESS_DENIED);
        }

        if (!stream.canStart()) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_REQUEST);
        }

        try {
            Session session = openVidu.getActiveSession(stream.getSessionId());
            if (session == null) {
                session = openVidu.createSession(
                        new SessionProperties.Builder()
                                .customSessionId(stream.getSessionId())
                                .build()
                );
            }

            ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                    .role(OpenViduRole.PUBLISHER)
                    .data("owner")
                    .build();

            Connection connection = session.createConnection(connectionProperties);
            
            // 스트림 시작
            stream.start();
            streamRepository.save(stream);

            log.info("Owner 토큰 생성 완료 - StreamId: {}, SessionId: {}", streamId, stream.getSessionId());

            return new StreamTokenResponse(connection.getToken(), stream.getSessionId());

        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("Owner 토큰 생성 실패 - StreamId: {}", streamId, e);
            throw new BusinessException(ErrorCode.COMMON_INTERNAL_SERVER_ERROR);
        }
    }

    public StreamTokenResponse createCustomerToken(StreamJoinRequest request) {
        Stream stream = streamRepository.findBySessionId(request.sessionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_ENTITY_NOT_FOUND));

        if (!stream.isLive()) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_REQUEST);
        }

        try {
            Session session = openVidu.getActiveSession(stream.getSessionId());
            if (session == null) {
                throw new BusinessException(ErrorCode.COMMON_ENTITY_NOT_FOUND);
            }

            ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                    .role(OpenViduRole.SUBSCRIBER)
                    .data("customer")
                    .build();

            Connection connection = session.createConnection(connectionProperties);

            log.info("Customer 토큰 생성 완료 - SessionId: {}", stream.getSessionId());

            return new StreamTokenResponse(connection.getToken(), stream.getSessionId());

        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("Customer 토큰 생성 실패 - SessionId: {}", request.sessionId(), e);
            throw new BusinessException(ErrorCode.COMMON_INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void endStream(Long ownerId, Long streamId) {
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_ENTITY_NOT_FOUND));

        if (!stream.getOwner().getId().equals(ownerId)) {
            throw new BusinessException(ErrorCode.COMMON_ACCESS_DENIED);
        }

        if (!stream.isLive()) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_REQUEST);
        }

        try {
            Session session = openVidu.getActiveSession(stream.getSessionId());
            if (session != null) {
                session.close();
            }
        } catch (OpenViduHttpException e) {
            if (e.getStatus() == 404) {
                log.warn("세션이 이미 종료되었거나 존재하지 않음 - SessionId: {}", stream.getSessionId());
            } else {
                log.error("OpenVidu 세션 종료 실패 - StreamId: {}, Status: {}", streamId, e.getStatus(), e);
                throw new BusinessException(ErrorCode.COMMON_INTERNAL_SERVER_ERROR);
            }
        } catch (OpenViduJavaClientException e) {
            log.error("OpenVidu 클라이언트 오류 - StreamId: {}", streamId, e);
            throw new BusinessException(ErrorCode.COMMON_INTERNAL_SERVER_ERROR);
        }

        // OpenVidu 세션 종료가 실패해도 스트림 상태는 종료로 변경
        stream.end();
        streamRepository.save(stream);

        log.info("스트림 종료 완료 - StreamId: {}, SessionId: {}", streamId, stream.getSessionId());
    }

    public List<StreamResponse> getLiveStreams() {
        List<Stream> liveStreams = streamRepository.findLiveStreamsOrderByStartedAtDesc();
        return liveStreams.stream()
                .map(StreamResponse::from)
                .toList();
    }

    public List<StreamResponse> getOwnerStreams(Long ownerId) {
        List<Stream> ownerStreams = streamRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
        return ownerStreams.stream()
                .map(StreamResponse::from)
                .toList();
    }

    public StreamResponse getStream(Long streamId) {
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMON_ENTITY_NOT_FOUND));
        return StreamResponse.from(stream);
    }

    private String generateSessionId() {
        return "ses_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}