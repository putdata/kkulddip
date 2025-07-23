package com.kkulddip.user.repository;

import com.kkulddip.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByIsVerified(Boolean isVerified);

    List<User> findByNotificationEnabled(Boolean notificationEnabled);

    boolean existsByPhoneNumber(String phoneNumber);
} 