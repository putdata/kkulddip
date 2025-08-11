package com.kkulddip.storeManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkulddip.storeManagement.dto.request.CreateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreRequest;
import com.kkulddip.storeManagement.dto.request.UpdateStoreStatusRequest;
import com.kkulddip.storeManagement.dto.response.StoreManagementResponse;
import com.kkulddip.storeManagement.service.StoreManagementService;
import com.kkulddip.storeManagement.util.OwnerExtractor;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
import org.mockito.MockedStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("citest")
@WebMvcTest(
    controllers = StoreManagementController.class,
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
class StoreManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StoreManagementService storeManagementService;

    // static 메서드를 모킹하기 위해 MockBean으로 등록
    @MockBean
    private OwnerExtractor ownerExtractor;


    private final Long ownerId = 1L;
    private final Long storeId = 10L;
    private MockedStatic<OwnerExtractor> mockedOwnerExtractor;

    @BeforeEach
    void setUp() {
        // OwnerExtractor의 static 메서드 모킹
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
    @DisplayName("가게 생성 성공")
    void createStore_success() throws Exception {
        // Given
        CreateStoreRequest request = CreateStoreRequest.builder()
            .storeName("새로운 가게")
            .storeAddress("서울시 강남구")
            .latitude(37.5)
            .longitude(127.0)
            .build();

        StoreManagementResponse mockResponse = StoreManagementResponse.builder()
            .storeId(storeId)
            .storeName("새로운 가게")
            .ownerId(ownerId)
            .build();

        when(storeManagementService.createStore(any(CreateStoreRequest.class), anyLong()))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/v1/store-management/stores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.body.storeId").value(storeId))
            .andExpect(jsonPath("$.body.storeName").value("새로운 가게"));
    }

    @Test
    @DisplayName("가게 정보 수정 성공")
    void updateStore_success() throws Exception {
        // Given
        UpdateStoreRequest request = UpdateStoreRequest.builder()
            .storeName("수정된 가게명")
            .description("수정된 설명")
            .build();

        StoreManagementResponse mockResponse = StoreManagementResponse.builder()
            .storeId(storeId)
            .storeName("수정된 가게명")
            .build();

        when(storeManagementService.updateStore(anyLong(), any(UpdateStoreRequest.class), anyLong()))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(put("/v1/store-management/stores/{storeId}", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.storeId").value(storeId))
            .andExpect(jsonPath("$.body.storeName").value("수정된 가게명"));
    }

    @Test
    @DisplayName("가게 상태 변경 성공")
    void updateStoreStatus_success() throws Exception {
        // Given
        UpdateStoreStatusRequest request = UpdateStoreStatusRequest.builder()
            .isActive(false)
            .reason("임시 휴업")
            .build();

        StoreManagementResponse mockResponse = StoreManagementResponse.builder()
            .storeId(storeId)
            .isActive(false)
            .build();

        when(storeManagementService.updateStoreStatus(anyLong(), any(UpdateStoreStatusRequest.class), anyLong()))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(patch("/v1/store-management/stores/{storeId}/status", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.isActive").value(false));
    }

    @Test
    @DisplayName("가게 삭제 성공")
    void deleteStore_success() throws Exception {
        // Given
        doNothing().when(storeManagementService).deleteStore(anyLong(), anyLong());

        // When & Then
        mockMvc.perform(delete("/v1/store-management/stores/{storeId}", storeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.status").value(200));
    }
    
    @Test
    @DisplayName("가게 생성 (이미지 포함) 성공")
    void createStoreWithImage_success() throws Exception {
        // Given
        CreateStoreRequest request = CreateStoreRequest.builder()
            .storeName("새로운 가게")
            .storeAddress("서울시 강남구")
            .latitude(37.5)
            .longitude(127.0)
            .build();
        
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "test.jpg",
            "image/jpeg",
            "test image".getBytes()
        );
        
        MockMultipartFile requestPart = new MockMultipartFile(
            "request",
            "request",
            "application/json",
            objectMapper.writeValueAsBytes(request)
        );
        
        StoreManagementResponse mockResponse = StoreManagementResponse.builder()
            .storeId(storeId)
            .storeName("새로운 가게")
            .ownerId(ownerId)
            .build();
        
        when(storeManagementService.createStoreWithImage(any(CreateStoreRequest.class), any(MultipartFile.class), anyLong()))
            .thenReturn(mockResponse);
        
        // When & Then
        mockMvc.perform(multipart("/v1/store-management/stores/with-image")
                .file(requestPart)
                .file(image)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.body.storeId").value(storeId))
            .andExpect(jsonPath("$.body.storeName").value("새로운 가게"));
    }
    
    @Test
    @DisplayName("가게 이미지 업데이트 성공")
    void updateStoreImage_success() throws Exception {
        // Given
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "updated.jpg",
            "image/jpeg",
            "updated image".getBytes()
        );
        
        StoreManagementResponse mockResponse = StoreManagementResponse.builder()
            .storeId(storeId)
            .storeName("테스트 가게")
            .ownerId(ownerId)
            .build();
        
        when(storeManagementService.updateStoreImage(anyLong(), any(MultipartFile.class), anyLong()))
            .thenReturn(mockResponse);
        
        // When & Then
        mockMvc.perform(multipart("/v1/store-management/stores/{storeId}/image", storeId)
                .file(image)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.storeId").value(storeId));
    }
}