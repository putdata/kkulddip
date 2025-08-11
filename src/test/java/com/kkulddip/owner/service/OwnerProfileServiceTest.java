package com.kkulddip.owner.service;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.owner.dto.request.UpdateOwnerProfileRequest;
import com.kkulddip.owner.dto.response.OwnerProfileResponse;
import com.kkulddip.owner.exception.OwnerNotFoundException;
import com.kkulddip.owner.mapper.OwnerMapper;
import com.kkulddip.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("OwnerProfileService 테스트")
@ActiveProfiles("citest")
class OwnerProfileServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private OwnerMapper ownerMapper;

    @InjectMocks
    private OwnerProfileService ownerProfileService;

    @Test
    @DisplayName("Owner 프로필 조회 성공")
    void getOwnerProfile_Success() {
        // given
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        
        Owner owner = Owner.builder()
            .ownerId(ownerId)
            .email("test@example.com")
            .name("테스트 사장")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId("google123")
            .createdAt(now.minusMonths(1))
            .updatedAt(now)
            .build();

        OwnerProfileResponse expectedResponse = new OwnerProfileResponse(
            ownerId, "test@example.com", "테스트 사장", null,
            "GOOGLE", null, now.minusMonths(1), now,
            null, null, 3, 2
        );

        given(ownerRepository.findById(ownerId)).willReturn(Optional.of(owner));
        given(storeRepository.countByOwnerId(ownerId)).willReturn(3L);
        given(storeRepository.countActiveByOwnerId(ownerId)).willReturn(2L);
        given(ownerMapper.toOwnerProfileResponse(owner, 3, 2)).willReturn(expectedResponse);

        // when
        OwnerProfileResponse result = ownerProfileService.getOwnerProfile(ownerId);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        then(ownerRepository).should().findById(ownerId);
        then(storeRepository).should().countByOwnerId(ownerId);
        then(storeRepository).should().countActiveByOwnerId(ownerId);
        then(ownerMapper).should().toOwnerProfileResponse(owner, 3, 2);
    }

    @Test
    @DisplayName("Owner 프로필 조회 실패 - Owner가 존재하지 않는 경우")
    void getOwnerProfile_OwnerNotFound() {
        // given
        Long ownerId = 1L;
        given(ownerRepository.findById(ownerId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ownerProfileService.getOwnerProfile(ownerId))
            .isInstanceOf(OwnerNotFoundException.class);

        then(ownerRepository).should().findById(ownerId);
        then(storeRepository).shouldHaveNoInteractions();
        then(ownerMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Owner 프로필 수정 성공")
    void updateOwnerProfile_Success() {
        // given
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        
        Owner existingOwner = Owner.builder()
            .ownerId(ownerId)
            .email("test@example.com")
            .name("기존 사장")
            .profileImageUrl("old-image.jpg")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId("google123")
            .createdAt(now.minusMonths(1))
            .updatedAt(now.minusDays(1))
            .build();

        UpdateOwnerProfileRequest request = new UpdateOwnerProfileRequest(
            "새로운 사장",
            "new-image.jpg",
            "123-45-67890",
            "대표자명"
        );

        Owner updatedOwner = Owner.builder()
            .ownerId(ownerId)
            .email("test@example.com")
            .name("새로운 사장")
            .profileImageUrl("new-image.jpg")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId("google123")
            .lastActiveAt(LocalDateTime.now())
            .createdAt(now.minusMonths(1))
            .updatedAt(LocalDateTime.now())
            .build();

        OwnerProfileResponse expectedResponse = new OwnerProfileResponse(
            ownerId, "test@example.com", "새로운 사장", "new-image.jpg",
            "GOOGLE", null, now.minusMonths(1), now,
            null, null, 3, 2
        );

        given(ownerRepository.findById(ownerId)).willReturn(Optional.of(existingOwner));
        given(ownerRepository.save(any(Owner.class))).willReturn(updatedOwner);
        given(storeRepository.countByOwnerId(ownerId)).willReturn(3L);
        given(storeRepository.countActiveByOwnerId(ownerId)).willReturn(2L);
        given(ownerMapper.toOwnerProfileResponse(any(Owner.class), eq(3), eq(2)))
            .willReturn(expectedResponse);

        // when
        OwnerProfileResponse result = ownerProfileService.updateOwnerProfile(ownerId, request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        
        ArgumentCaptor<Owner> ownerCaptor = ArgumentCaptor.forClass(Owner.class);
        then(ownerRepository).should().save(ownerCaptor.capture());
        
        Owner savedOwner = ownerCaptor.getValue();
        assertThat(savedOwner.getName()).isEqualTo("새로운 사장");
        assertThat(savedOwner.getProfileImageUrl()).isEqualTo("new-image.jpg");
        assertThat(savedOwner.getLastActiveAt()).isNotNull();
        assertThat(savedOwner.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Owner 프로필 수정 실패 - Owner가 존재하지 않는 경우")
    void updateOwnerProfile_OwnerNotFound() {
        // given
        Long ownerId = 1L;
        UpdateOwnerProfileRequest request = new UpdateOwnerProfileRequest(
            "새로운 사장", "new-image.jpg", null, null
        );
        
        given(ownerRepository.findById(ownerId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ownerProfileService.updateOwnerProfile(ownerId, request))
            .isInstanceOf(OwnerNotFoundException.class);

        then(ownerRepository).should().findById(ownerId);
        then(ownerRepository).should(times(0)).save(any(Owner.class));
    }

    @Test
    @DisplayName("마지막 활동 시간 업데이트 성공")
    void updateLastActiveAt_Success() {
        // given
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        
        Owner existingOwner = Owner.builder()
            .ownerId(ownerId)
            .email("test@example.com")
            .name("테스트 사장")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId("google123")
            .createdAt(now.minusMonths(1))
            .updatedAt(now.minusDays(1))
            .build();

        given(ownerRepository.findById(ownerId)).willReturn(Optional.of(existingOwner));
        given(ownerRepository.save(any(Owner.class))).willReturn(existingOwner);

        // when
        ownerProfileService.updateLastActiveAt(ownerId);

        // then
        ArgumentCaptor<Owner> ownerCaptor = ArgumentCaptor.forClass(Owner.class);
        then(ownerRepository).should().save(ownerCaptor.capture());
        
        Owner savedOwner = ownerCaptor.getValue();
        assertThat(savedOwner.getLastActiveAt()).isNotNull();
        assertThat(savedOwner.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("마지막 활동 시간 업데이트 실패 - Owner가 존재하지 않는 경우")
    void updateLastActiveAt_OwnerNotFound() {
        // given
        Long ownerId = 1L;
        given(ownerRepository.findById(ownerId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ownerProfileService.updateLastActiveAt(ownerId))
            .isInstanceOf(OwnerNotFoundException.class);

        then(ownerRepository).should().findById(ownerId);
        then(ownerRepository).should(times(0)).save(any(Owner.class));
    }
}