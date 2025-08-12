package com.kkulddip.customerProfile.mapper;

import com.kkulddip.customerProfile.dto.response.CustomerProfileResponse;
import com.kkulddip.customerProfile.dto.response.CustomerStatsResponse;
import com.kkulddip.customerProfile.dto.response.UpdateLocationResponse;
import com.kkulddip.customerProfile.dto.response.UpdateProfileResponse;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.enums.CustomerLevel;
import org.springframework.stereotype.Component;

@Component
public class CustomerProfileMapper {
    
    public CustomerProfileResponse toProfileResponse(Customer customer) {
        return CustomerProfileResponse.builder()
            .customerId(customer.getCustomerId())
            .email(customer.getEmail())
            .name(customer.getName())
            .profileImageUrl(customer.getProfileImageUrl())
            .address(customer.getAddress())
            .latitude(customer.getLatitude())
            .longitude(customer.getLongitude())
            .level(customer.getLevel())
            .createdAt(customer.getCreatedAt())
            .lastActiveAt(customer.getLastActiveAt())
            .build();
    }
    
    public CustomerStatsResponse toStatsResponse(Customer customer) {
        CustomerLevel currentLevel = customer.getLevel();
        CustomerLevel nextLevel = getNextLevel(currentLevel);
        Integer ordersUntilNextLevel = calculateOrdersUntilNextLevel(
            customer.getTotalOrder(),
            currentLevel
        );
        
        return CustomerStatsResponse.builder()
            .customerId(customer.getCustomerId())
            .level(customer.getLevel())
            .totalOrder(customer.getTotalOrder())
            .totalMoneySaved(customer.getTotalMoneySaved())
            .totalCo2Saved(customer.getTotalCo2Saved())
            .ordersUntilNextLevel(ordersUntilNextLevel)
            .nextLevel(nextLevel)
            .build();
    }
    
    public UpdateProfileResponse toUpdateProfileResponse(Customer customer) {
        return UpdateProfileResponse.builder()
            .customerId(customer.getCustomerId())
            .name(customer.getName())
            .profileImageUrl(customer.getProfileImageUrl())
            .updatedAt(customer.getUpdatedAt())
            .build();
    }
    
    public UpdateLocationResponse toUpdateLocationResponse(Customer customer) {
        return UpdateLocationResponse.builder()
            .customerId(customer.getCustomerId())
            .address(customer.getAddress())
            .latitude(customer.getLatitude())
            .longitude(customer.getLongitude())
            .updatedAt(customer.getUpdatedAt())
            .build();
    }
    
    private CustomerLevel getNextLevel(CustomerLevel currentLevel) {
        return switch (currentLevel) {
            case SPROUT_BEE -> CustomerLevel.WORKER_BEE;
            case WORKER_BEE -> CustomerLevel.HONEY_BEE;
            case HONEY_BEE -> CustomerLevel.QUEEN_BEE;
            case QUEEN_BEE -> null;
        };
    }
    
    private Integer calculateOrdersUntilNextLevel(Integer totalOrder, CustomerLevel currentLevel) {
        Integer requiredOrdersForNextLevel = switch (currentLevel) {
            case SPROUT_BEE -> 10;
            case WORKER_BEE -> 30;
            case HONEY_BEE -> 50;
            case QUEEN_BEE -> null;
        };
        
        if (requiredOrdersForNextLevel == null) {
            return null;
        }
        
        return Math.max(0, requiredOrdersForNextLevel - totalOrder);
    }
}