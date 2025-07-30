package com.kkulddip.domain.customer.repository;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByOauth2ProviderAndOauth2ProviderId(
        OAuth2Provider provider, String providerId);

    boolean existsByEmail(String email);
}