package com.kkulddip.storeManagement.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.storeManagement.service.impl.StoreImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("citest")
@ExtendWith(MockitoExtension.class)
class StoreImageServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private StoreImageServiceImpl storeImageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storeImageService, "bucket", "test-bucket");
    }
    
    @Test
    @DisplayName("단일 이미지 S3 업로드 성공")
    void uploadSingleImageToS3_success() throws IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "image.jpg", 
            "image.jpg", 
            "image/jpeg", 
            "test".getBytes()
        );
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/store/uuid.jpg";
        
        when(amazonS3.putObject(any())).thenReturn(new PutObjectResult());
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(new java.net.URL(expectedUrl));
        
        // When
        String result = storeImageService.uploadSingleImageToS3(file);
        
        // Then
        assertThat(result).isEqualTo(expectedUrl);
        verify(amazonS3, times(1)).putObject(any());
    }

    @Test
    @DisplayName("단일 이미지 S3 업로드 실패 - 빈 파일")
    void uploadSingleImageToS3_fail_emptyFile() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "empty.jpg",
            "empty.jpg",
            "image/jpeg",
            new byte[0]
        );
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeImageService.uploadSingleImageToS3(file));
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STORE_IMAGE_EMPTY_FILE);
        verify(amazonS3, never()).putObject(any());
    }

    @Test
    @DisplayName("단일 이미지 S3 업로드 실패 - 파일 크기 초과")
    void uploadSingleImageToS3_fail_fileTooLarge() {
        // Given
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile file = new MockMultipartFile(
            "large.jpg",
            "large.jpg",
            "image/jpeg",
            largeContent
        );
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeImageService.uploadSingleImageToS3(file));
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STORE_IMAGE_TOO_LARGE);
        verify(amazonS3, never()).putObject(any());
    }

    @Test
    @DisplayName("단일 이미지 S3 업로드 실패 - 지원하지 않는 파일 형식")
    void uploadSingleImageToS3_fail_unsupportedFormat() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "document.pdf",
            "document.pdf",
            "application/pdf",
            "test".getBytes()
        );
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeImageService.uploadSingleImageToS3(file));
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STORE_IMAGE_INVALID_FORMAT);
        verify(amazonS3, never()).putObject(any());
    }
    
    @Test
    @DisplayName("S3에서 이미지 삭제 성공")
    void deleteImageFromS3_success() {
        // Given
        String imageUrl = "https://test-bucket.s3.amazonaws.com/store/test-image.jpg";
        
        doNothing().when(amazonS3).deleteObject(anyString(), anyString());
        
        // When
        storeImageService.deleteImageFromS3(imageUrl);
        
        // Then
        verify(amazonS3, times(1)).deleteObject(eq("test-bucket"), anyString());
    }
    
    @Test
    @DisplayName("S3에서 이미지 삭제 - URL이 null인 경우")
    void deleteImageFromS3_nullUrl() {
        // When
        storeImageService.deleteImageFromS3(null);
        
        // Then
        verify(amazonS3, never()).deleteObject(anyString(), anyString());
    }
    
    @Test
    @DisplayName("S3에서 이미지 삭제 - URL이 빈 문자열인 경우")
    void deleteImageFromS3_emptyUrl() {
        // When
        storeImageService.deleteImageFromS3("");
        
        // Then
        verify(amazonS3, never()).deleteObject(anyString(), anyString());
    }
    
    @Test
    @DisplayName("S3에서 이미지 삭제 실패 - 예외 발생해도 무시")
    void deleteImageFromS3_failureSilentlyIgnored() {
        // Given
        String imageUrl = "https://test-bucket.s3.amazonaws.com/store/test-image.jpg";
        
        doThrow(new RuntimeException("S3 Error")).when(amazonS3).deleteObject(anyString(), anyString());
        
        // When & Then - 예외가 발생하지 않아야 함
        storeImageService.deleteImageFromS3(imageUrl);
        
        verify(amazonS3, times(1)).deleteObject(eq("test-bucket"), anyString());
    }
}