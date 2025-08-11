package com.kkulddip.stream.repository;

import com.kkulddip.stream.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StreamRepository extends JpaRepository<Stream, Long> {

    @Query("SELECT s FROM Stream s WHERE s.store.ownerId = :ownerId ORDER BY s.createdAt DESC")
    List<Stream> findByOwnerIdOrderByCreatedAtDesc(@Param("ownerId") Long ownerId);

    @Query("SELECT s FROM Stream s WHERE s.store.storeId = :storeId ORDER BY s.createdAt DESC")
    List<Stream> findByStoreIdOrderByCreatedAtDesc(@Param("storeId") Long storeId);

    @Query("SELECT s FROM Stream s WHERE s.status = 'LIVE' ORDER BY s.startedAt DESC")
    List<Stream> findLiveStreamsOrderByStartedAtDesc();
}