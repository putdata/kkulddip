package com.kkulddip.domain.customer.service;

import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 고객 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * 고객 프로필 업데이트
     */
    public Customer updateProfile(Customer customer, String name, String profileImageUrl) {

        customer.updateName(name);
        customer.updateProfileImageUrl(profileImageUrl);
        
        Customer updatedCustomer = customerRepository.save(customer);

        return updatedCustomer;
    }
}