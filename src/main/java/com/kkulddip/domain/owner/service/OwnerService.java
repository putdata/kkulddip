package com.kkulddip.domain.owner.service;

import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사장 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OwnerService {

    private final OwnerRepository ownerRepository;

    /**
     * 사장 프로필 업데이트
     */
    public Owner updateProfile(Owner owner, String name, String profileImageUrl) {
        User.validateProfileUpdate(name, profileImageUrl);

        owner.updateName(name);
        owner.updateProfileImageUrl(profileImageUrl);
        
        Owner updatedOwner = ownerRepository.save(owner);

        return updatedOwner;
    }
}