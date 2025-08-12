package com.kkulddip.owner.service;

import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.owner.dto.request.UpdateOwnerProfileRequest;
import com.kkulddip.owner.dto.response.OwnerProfileResponse;
import com.kkulddip.owner.exception.OwnerNotFoundException;
import com.kkulddip.owner.mapper.OwnerMapper;
import com.kkulddip.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OwnerProfileService {

    private final OwnerRepository ownerRepository;
    private final StoreRepository storeRepository;
    private final OwnerMapper ownerMapper;

    public OwnerProfileResponse getOwnerProfile(Long ownerId) {
        log.info("Owner 프로필 조회 요청 - ownerId: {}", ownerId);

        Owner owner = findOwnerById(ownerId);
        
        Long totalStoreCount = storeRepository.countByOwnerId(ownerId);
        Long activeStoreCount = storeRepository.countActiveByOwnerId(ownerId);

        OwnerProfileResponse response = ownerMapper.toOwnerProfileResponse(
            owner, 
            totalStoreCount.intValue(),
            activeStoreCount.intValue()
        );

        log.info("Owner 프로필 조회 완료 - ownerId: {}, 총 가게 수: {}, 활성 가게 수: {}", 
            ownerId, totalStoreCount, activeStoreCount);

        return response;
    }

    @Transactional
    public OwnerProfileResponse updateOwnerProfile(Long ownerId, UpdateOwnerProfileRequest request) {
        log.info("Owner 프로필 수정 요청 - ownerId: {}, name: {}", ownerId, request.name());

        Owner owner = findOwnerById(ownerId);

        Owner updatedOwner = Owner.builder()
            .ownerId(owner.getOwnerId())
            .email(owner.getEmail())
            .name(request.name())
            .profileImageUrl(request.profileImageUrl())
            .oauth2Provider(owner.getOauth2Provider())
            .oauth2ProviderId(owner.getOauth2ProviderId())
            .lastActiveAt(LocalDateTime.now())
            .createdAt(owner.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        Owner savedOwner = ownerRepository.save(updatedOwner);
        
        Long totalStoreCount = storeRepository.countByOwnerId(ownerId);
        Long activeStoreCount = storeRepository.countActiveByOwnerId(ownerId);

        OwnerProfileResponse response = ownerMapper.toOwnerProfileResponse(
            savedOwner,
            totalStoreCount.intValue(),
            activeStoreCount.intValue()
        );

        log.info("Owner 프로필 수정 완료 - ownerId: {}", ownerId);

        return response;
    }

    @Transactional
    public void updateLastActiveAt(Long ownerId) {
        log.debug("Owner 마지막 활동 시간 업데이트 - ownerId: {}", ownerId);

        Owner owner = findOwnerById(ownerId);
        
        Owner updatedOwner = Owner.builder()
            .ownerId(owner.getOwnerId())
            .email(owner.getEmail())
            .name(owner.getName())
            .profileImageUrl(owner.getProfileImageUrl())
            .oauth2Provider(owner.getOauth2Provider())
            .oauth2ProviderId(owner.getOauth2ProviderId())
            .lastActiveAt(LocalDateTime.now())
            .createdAt(owner.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        ownerRepository.save(updatedOwner);
    }

    private Owner findOwnerById(Long ownerId) {
        return ownerRepository.findById(ownerId)
            .orElseThrow(() -> new OwnerNotFoundException(ownerId));
    }
}