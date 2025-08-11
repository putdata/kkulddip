package com.kkulddip.stream.entity;

import com.kkulddip.store.entity.Store;
import com.kkulddip.stream.entity.enums.StreamStatus;
import com.kkulddip.stream.exception.StreamCannotBeStartedException;
import com.kkulddip.stream.exception.StreamCannotBeEndedException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "streams")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stream_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StreamStatus status;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "viewer_count", nullable = false)
    private Integer viewerCount;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Stream(Store store, String title, String description) {
        this.store = store;
        this.title = title;
        this.description = description;
        this.status = StreamStatus.READY;
        this.viewerCount = 0;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 스트림 시작
     * @param sessionId OpenVidu 세션 ID
     */
    public void start(String sessionId) {
        if (!status.canStart()) {
            throw new StreamCannotBeStartedException(
                "현재 상태에서는 스트림을 시작할 수 없습니다. 현재 상태: " + status.getDescription());
        }
        
        this.sessionId = sessionId;
        this.status = StreamStatus.LIVE;
        this.startedAt = LocalDateTime.now();
    }
    
    /**
     * 스트림 종료
     */
    public void end() {
        if (!status.canEnd()) {
            throw new StreamCannotBeEndedException(
                "현재 상태에서는 스트림을 종료할 수 없습니다. 현재 상태: " + status.getDescription());
        }
        
        this.status = StreamStatus.ENDED;
        this.endedAt = LocalDateTime.now();
    }
    
    /**
     * 시청자 수 증가
     */
    public void incrementViewer() {
        this.viewerCount++;
    }
    
    /**
     * 시청자 수 감소
     */
    public void decrementViewer() {
        if (this.viewerCount > 0) {
            this.viewerCount--;
        }
    }
    
    /**
     * 스트림 정보 업데이트
     */
    public void updateInfo(String title, String description) {
        this.title = title;
        this.description = description;
    }
    
    /**
     * 스트림이 라이브 상태인지 확인
     */
    public boolean isLive() {
        return status.isLive();
    }
    
    /**
     * 스트림이 종료되었는지 확인
     */
    public boolean isEnded() {
        return status.isEnded();
    }
    
    /**
     * 스트림 소유자 ID 반환 (Store의 Owner ID)
     */
    public Long getOwnerId() {
        return store.getOwnerId();
    }
    
    /**
     * 스토어 ID 반환
     */
    public Long getStoreId() {
        return store.getStoreId();
    }
    
    /**
     * 소유자인지 확인
     */
    public boolean isOwner(Long userId) {
        return getOwnerId().equals(userId);
    }
}