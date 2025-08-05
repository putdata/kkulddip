package com.kkulddip.domain.stream.repository;

import com.kkulddip.domain.stream.entity.Stream;
import com.kkulddip.domain.stream.entity.StreamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StreamRepository extends JpaRepository<Stream, Long> {

    Optional<Stream> findBySessionId(String sessionId);

    List<Stream> findByStatusOrderByCreatedAtDesc(StreamStatus status);

    @Query("SELECT s FROM Stream s WHERE s.owner.id = :ownerId ORDER BY s.createdAt DESC")
    List<Stream> findByOwnerIdOrderByCreatedAtDesc(@Param("ownerId") Long ownerId);

    @Query("SELECT s FROM Stream s WHERE s.status = 'LIVE' ORDER BY s.startedAt DESC")
    List<Stream> findLiveStreamsOrderByStartedAtDesc();
}