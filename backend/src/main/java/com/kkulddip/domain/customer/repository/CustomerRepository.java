package com.kkulddip.domain.customer.repository;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.domain.customer.dto.CustomerNameAndImage;
import com.kkulddip.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByOauth2ProviderAndOauth2ProviderId(
        OAuth2Provider provider, String providerId);

    /**
     * customerId로 고객명을 조회합니다.
     */
    @Query("""
        SELECT c.name FROM Customer c 
        WHERE c.customerId = :customerId
        """)
    Optional<String> findCustomerNameByCustomerId(@Param("customerId") Long customerId);

    /**
     * customerId로 고객의 이름과 프로필 이미지를 조회합니다.
     */
    @Query("""
        SELECT new com.kkulddip.domain.customer.dto.CustomerNameAndImage(c.name, c.profileImageUrl) 
        FROM Customer c 
        WHERE c.customerId = :customerId
        """)
    Optional<CustomerNameAndImage> findCustomerNameAndImageByCustomerId(@Param("customerId") Long customerId);
}