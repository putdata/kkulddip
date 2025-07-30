package com.kkulddip.domain.owner.service;

import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사장 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OwnerService {

    private final OwnerRepository ownerRepository;

    /**
     * 사장 프로필 업데이트
     */
    public Owner updateProfile(Owner owner, String name, String profileImageUrl) {
        owner.updateProfile(name, profileImageUrl);
        
        Owner updatedOwner = ownerRepository.save(owner);
        
        return updatedOwner;
    }
}