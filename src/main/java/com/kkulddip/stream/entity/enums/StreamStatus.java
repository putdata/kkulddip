package com.kkulddip.stream.entity.enums;

import lombok.Getter;

/**
 * 스트림 상태를 나타내는 열거형
 */
@Getter
public enum StreamStatus {

    READY("준비됨"),
    LIVE("라이브"),
    ENDED("종료됨");
    
    private final String description;
    
    StreamStatus(String description) {
        this.description = description;
    }
    
    /**
     * 스트림을 시작할 수 있는 상태인지 확인
     */
    public boolean canStart() {
        return this == READY;
    }
    
    /**
     * 스트림을 종료할 수 있는 상태인지 확인
     */
    public boolean canEnd() {
        return this == LIVE;
    }
    
    /**
     * 라이브 상태인지 확인
     */
    public boolean isLive() {
        return this == LIVE;
    }
    
    /**
     * 종료된 상태인지 확인
     */
    public boolean isEnded() {
        return this == ENDED;
    }
}