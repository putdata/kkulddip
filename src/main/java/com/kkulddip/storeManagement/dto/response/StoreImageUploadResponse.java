//package com.kkulddip.storeManagement.dto.response;
//
//import lombok.Builder;
//import java.util.List;
//
///**
// * 가게 이미지 업로드 완료 응답 DTO
// */
//@Builder
//public record StoreImageUploadResponse(
//
//    Long storeId,
//    String message,
//    int uploadedCount,
//    int failedCount,
//    List<StoreImageResponse> uploadedImages,
//    List<String> failedFiles
//
//) {
//
//    public static StoreImageUploadResponse success(Long storeId, List<StoreImageResponse> uploadedImages) {
//        return StoreImageUploadResponse.builder()
//            .storeId(storeId)
//            .message("이미지 업로드가 완료되었습니다.")
//            .uploadedCount(uploadedImages.size())
//            .failedCount(0)
//            .uploadedImages(uploadedImages)
//            .failedFiles(List.of())
//            .build();
//    }
//
//    public static StoreImageUploadResponse partialSuccess(Long storeId,
//                                                         List<StoreImageResponse> uploadedImages,
//                                                         List<String> failedFiles) {
//        return StoreImageUploadResponse.builder()
//            .storeId(storeId)
//            .message("일부 이미지 업로드가 실패했습니다.")
//            .uploadedCount(uploadedImages.size())
//            .failedCount(failedFiles.size())
//            .uploadedImages(uploadedImages)
//            .failedFiles(failedFiles)
//            .build();
//    }
//}