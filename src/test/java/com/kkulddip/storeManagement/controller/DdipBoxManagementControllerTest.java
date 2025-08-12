package com.kkulddip.storeManagement.controller;

import com.kkulddip.storeManagement.dto.request.CreateDdipBoxRequest;
import com.kkulddip.storeManagement.dto.response.DdipBoxManagementResponse;
import com.kkulddip.storeManagement.service.DdipBoxManagementService;
import com.kkulddip.storeManagement.util.OwnerExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("citest")
@WebMvcTest(
    controllers = DdipBoxManagementController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.kkulddip\\.common\\.security\\..*"
    )
)
class DdipBoxManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DdipBoxManagementService ddipBoxManagementService;

    // OwnerExtractor가 static 메소드를 가지고 있으므로 Mockito를 사용하기 위해 클래스 자체를 Mock 처리
    @MockBean
    private OwnerExtractor ownerExtractor;


    private Long ownerId;
    private Long storeId;
    private MockedStatic<OwnerExtractor> mockedOwnerExtractor;

    @BeforeEach
    void setUp() {
        ownerId = 1L;
        storeId = 1L;
        mockedOwnerExtractor = mockStatic(OwnerExtractor.class);
        mockedOwnerExtractor.when(OwnerExtractor::getCurrentOwnerId).thenReturn(ownerId);
    }

    @AfterEach
    void tearDown() {
        if (mockedOwnerExtractor != null) {
            mockedOwnerExtractor.close();
        }
    }

    @Test
    @DisplayName("띱박스 생성 요청 성공")
    void createDdipBox_success() throws Exception {
        // Given
        CreateDdipBoxRequest request = CreateDdipBoxRequest.builder()
            .ddipboxName("테스트 띱박스")
            .category("FOOD")
            .originalPrice(10000L)
            .salePrice(5000L)
            .dailyQuantity(10L)
            .maxPerCustomer(5L)
            .build();

        DdipBoxManagementResponse response = DdipBoxManagementResponse.builder()
            .ddipboxId(1L)
            .ddipboxName("테스트 띱박스")
            .build();

        when(ddipBoxManagementService.createDdipBox(anyLong(), any(CreateDdipBoxRequest.class), anyLong()))
            .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/v1/store-management/stores/{storeId}/ddipboxes", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.body.ddipboxId").value(1L))
            .andExpect(jsonPath("$.body.ddipboxName").value("테스트 띱박스"));

        verify(ddipBoxManagementService, times(1)).createDdipBox(anyLong(), any(CreateDdipBoxRequest.class), anyLong());
    }

    @Test
    @DisplayName("띱박스 생성 요청 시 유효성 검사 실패")
    void createDdipBox_invalidRequest_fail() throws Exception {
        // Given - ddipboxName이 비어 있음
        CreateDdipBoxRequest request = CreateDdipBoxRequest.builder()
            .ddipboxName(" ")
            .category("FOOD")
            .originalPrice(10000L)
            .salePrice(5000L)
            .dailyQuantity(10L)
            .maxPerCustomer(5L)
            .build();

        // When & Then
        mockMvc.perform(post("/v1/store-management/stores/{storeId}/ddipboxes", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("가게 띱박스 목록 조회 성공")
    void getDdipBoxesByStore_success() throws Exception {
        // Given
        List<DdipBoxManagementResponse> responseList = Collections.singletonList(
            DdipBoxManagementResponse.builder().ddipboxId(1L).ddipboxName("Test Box").build()
        );
        when(ddipBoxManagementService.getDdipBoxesByStore(anyLong(), anyLong())).thenReturn(responseList);

        // When & Then
        mockMvc.perform(get("/v1/store-management/stores/{storeId}/ddipboxes", storeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body[0].ddipboxId").value(1L));

        verify(ddipBoxManagementService, times(1)).getDdipBoxesByStore(anyLong(), anyLong());
    }
}