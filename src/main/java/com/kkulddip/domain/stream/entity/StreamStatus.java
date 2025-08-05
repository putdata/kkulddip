package com.kkulddip.domain.stream.entity;

public enum StreamStatus {
    WAITING,  // 스트림 생성됨, 시작 대기 중
    LIVE,     // 스트림 진행 중
    ENDED     // 스트림 종료됨
}