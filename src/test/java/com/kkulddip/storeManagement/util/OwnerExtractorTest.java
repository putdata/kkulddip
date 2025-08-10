package com.kkulddip.storeManagement.util;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("citest")
@ExtendWith(MockitoExtension.class)
public class OwnerExtractorTest {

    @Mock
    private OwnerRepository ownerRepository;
    
    @BeforeEach
    void setUp() {
        // OwnerExtractor의 static ownerRepository 필드에 mock 설정
        ReflectionTestUtils.setField(OwnerExtractor.class, "ownerRepository", ownerRepository);
    }
    
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증된 사장님 ID 추출 성공")
    void getOwnerId_success() {
        // Given
        Long ownerId = 1L;
        String oauth2ProviderId = "google-user-123";
        
        JwtUserInfo jwtUserInfo = new JwtUserInfo("owner@example.com", "OWNER", "GOOGLE", oauth2ProviderId);
        
        Owner mockOwner = Owner.builder()
            .ownerId(ownerId)
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .oauth2ProviderId(oauth2ProviderId)
            .build();
        
        when(ownerRepository.findByOauth2ProviderAndOauth2ProviderId(OAuth2Provider.GOOGLE, oauth2ProviderId))
            .thenReturn(Optional.of(mockOwner));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUserInfo, null, null);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When
        Long extractedId = OwnerExtractor.getCurrentOwnerId();

        // Then
        assertThat(extractedId).isEqualTo(ownerId);
    }
}