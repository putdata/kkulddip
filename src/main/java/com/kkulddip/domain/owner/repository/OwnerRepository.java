package com.kkulddip.domain.owner.repository;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.domain.owner.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByOauth2ProviderAndOauth2ProviderId(
        OAuth2Provider provider, String providerId);
}