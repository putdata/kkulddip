package com.kkulddip.favorite.service;

import com.kkulddip.favorite.dto.request.AddFavoriteRequest;
import com.kkulddip.favorite.dto.request.GetFavoritesRequest;
import com.kkulddip.favorite.dto.response.AddFavoriteResponse;
import com.kkulddip.favorite.dto.response.DeleteFavoriteResponse;
import com.kkulddip.favorite.dto.response.GetFavoritesResponse;
import com.kkulddip.favorite.entity.Favorite;
import com.kkulddip.favorite.enums.FavoriteSortType;
import com.kkulddip.favorite.exception.FavoriteAlreadyExistsException;
import com.kkulddip.favorite.exception.FavoriteNotFoundException;
import com.kkulddip.favorite.mapper.FavoriteMapper;
import com.kkulddip.favorite.repository.FavoriteRepository;
import com.kkulddip.store.common.CursorInfo;
import com.kkulddip.store.common.Page;
import com.kkulddip.store.repository.StoreRepository;
import com.kkulddip.store.exception.StoreNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 즐겨찾기 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final StoreRepository storeRepository;
    private final FavoriteMapper favoriteMapper;

    /**
     * 즐겨찾기 추가
     */
    @Transactional
    public AddFavoriteResponse addFavorite(AddFavoriteRequest request) {
        log.debug("즐겨찾기 추가 요청: customerId={}, storeId={}", request.customerId(), request.storeId());

        // 가게 존재 여부 확인
        if (!storeRepository.existsActiveStore(request.storeId())) {
            throw new StoreNotFoundException(request.storeId());
        }

        // 중복 즐겨찾기 확인
        if (favoriteRepository.existsByCustomerIdAndStoreId(request.customerId(), request.storeId())) {
            throw new FavoriteAlreadyExistsException(request.customerId(), request.storeId());
        }

        // 즐겨찾기 생성 및 저장
        Favorite favorite = Favorite.builder()
            .customerId(request.customerId())
            .storeId(request.storeId())
            .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);
        log.info("즐겨찾기 추가 완료: favoriteId={}", savedFavorite.getFavoriteId());

        return favoriteMapper.toAddFavoriteResponse(savedFavorite);
    }

    /**
     * 즐겨찾기 목록 조회 (커서 기반 페이지네이션)
     */
    public Page<GetFavoritesResponse> getFavorites(GetFavoritesRequest request, Double userLat, Double userLng) {
        log.debug("즐겨찾기 목록 조회 요청: customerId={}, sortBy={}, size={}", 
            request.customerId(), request.sortBy(), request.size());

        CursorInfo cursorInfo = CursorInfo.decode(request.cursor());
        Pageable pageable = PageRequest.of(0, request.size() + 1); // +1 for hasNext check

        List<Favorite> favorites = fetchFavoritesBySortType(
            request.customerId(), 
            request.sortBy(), 
            cursorInfo, 
            pageable,
            userLat,
            userLng
        );

        return buildFavoritePage(favorites, request.size(), cursorInfo, userLat, userLng, request.sortBy());
    }

    /**
     * 즐겨찾기 삭제
     */
    @Transactional
    public DeleteFavoriteResponse deleteFavorite(Long favoriteId) {
        log.debug("즐겨찾기 삭제 요청: favoriteId={}", favoriteId);

        Favorite favorite = favoriteRepository.findById(favoriteId)
            .orElseThrow(() -> new FavoriteNotFoundException(favoriteId));

        favoriteRepository.delete(favorite);
        log.info("즐겨찾기 삭제 완료: favoriteId={}", favoriteId);

        return DeleteFavoriteResponse.builder()
            .message("즐겨찾기가 성공적으로 삭제되었습니다.")
            .favoriteId(favoriteId)
            .build();
    }

    /**
     * 고객의 특정 가게 즐겨찾기 삭제
     */
    @Transactional
    public DeleteFavoriteResponse deleteFavoriteByCustomerAndStore(Long customerId, Long storeId) {
        log.debug("고객의 가게 즐겨찾기 삭제 요청: customerId={}, storeId={}", customerId, storeId);

        Favorite favorite = favoriteRepository.findByCustomerIdAndStoreId(customerId, storeId)
            .orElseThrow(() -> new FavoriteNotFoundException());

        favoriteRepository.delete(favorite);
        log.info("고객의 가게 즐겨찾기 삭제 완료: favoriteId={}", favorite.getFavoriteId());

        return DeleteFavoriteResponse.builder()
            .message("즐겨찾기가 성공적으로 삭제되었습니다.")
            .favoriteId(favorite.getFavoriteId())
            .build();
    }

    /**
     * 즐겨찾기 존재 여부 확인
     */
    public boolean isFavorite(Long customerId, Long storeId) {
        return favoriteRepository.existsByCustomerIdAndStoreId(customerId, storeId);
    }

    /**
     * 정렬 타입에 따른 즐겨찾기 조회
     */
    private List<Favorite> fetchFavoritesBySortType(
            Long customerId, 
            FavoriteSortType sortType, 
            CursorInfo cursorInfo, 
            Pageable pageable,
            Double userLat,
            Double userLng) {
        
        return switch (sortType) {
            case CREATED_DESC -> favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
                customerId,
                cursorInfo != null ? cursorInfo.getCreatedAt() : null,
                cursorInfo != null ? cursorInfo.getId() : null,
                pageable
            );
            case NAME_ASC -> favoriteRepository.findFavoritesWithCursorByStoreNameAsc(
                customerId,
                null, // Store name은 CursorInfo에 저장되지 않으므로 null
                cursorInfo != null ? cursorInfo.getId() : null,
                pageable.getPageSize()
            );
            case DISTANCE_ASC -> {
                if (userLat == null || userLng == null) {
                    log.warn("거리순 정렬을 위해서는 사용자 위치가 필요합니다. 기본 정렬로 대체합니다.");
                    yield favoriteRepository.findFavoritesWithCursorByCreatedAtDesc(
                        customerId, 
                        cursorInfo != null ? cursorInfo.getCreatedAt() : null, 
                        cursorInfo != null ? cursorInfo.getId() : null, 
                        pageable);
                }
                yield favoriteRepository.findFavoritesWithCursorByDistance(
                    customerId, userLat, userLng, 
                    cursorInfo != null ? cursorInfo.getDistance() : null, 
                    cursorInfo != null ? cursorInfo.getId() : null, 
                    pageable.getPageSize());
            }
            case RATING_DESC -> favoriteRepository.findFavoritesWithCursorByRatingDesc(
                customerId,
                cursorInfo != null ? cursorInfo.getRating() : null,
                cursorInfo != null ? cursorInfo.getId() : null,
                pageable.getPageSize()
            );
            case CATEGORY -> favoriteRepository.findFavoritesWithCursorByCategory(
                customerId,
                null, // Category는 CursorInfo에 저장되지 않으므로 null
                cursorInfo != null ? cursorInfo.getId() : null,
                pageable.getPageSize()
            );
        };
    }

    /**
     * 즐겨찾기 페이지 빌드
     */
    private Page<GetFavoritesResponse> buildFavoritePage(
            List<Favorite> favorites, 
            int requestedSize, 
            CursorInfo cursorInfo,
            Double userLat,
            Double userLng,
            FavoriteSortType sortType) {
        
        boolean hasNext = favorites.size() > requestedSize;
        List<Favorite> content = hasNext ? favorites.subList(0, requestedSize) : favorites;
        
        List<GetFavoritesResponse> responseList = content.stream()
            .map(favorite -> favoriteMapper.toGetFavoritesResponse(favorite, userLat, userLng))
            .filter(Objects::nonNull) // 비활성화된 가게 제외
            .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext && !content.isEmpty() && !responseList.isEmpty()) {
            Favorite lastFavorite = content.get(content.size() - 1);
            GetFavoritesResponse lastResponse = responseList.get(responseList.size() - 1);
            
            // 정렬 타입에 따른 커서 생성
            nextCursor = switch (sortType) {
                case CREATED_DESC -> CursorInfo.ofCreatedAt(lastFavorite.getFavoriteId(), lastFavorite.getCreatedAt()).encode();
                case DISTANCE_ASC -> {
                    if (lastResponse != null && lastResponse.distanceFromCustomer() != null) {
                        yield CursorInfo.ofDistance(lastFavorite.getFavoriteId(), lastResponse.distanceFromCustomer()).encode();
                    } else {
                        yield CursorInfo.ofId(lastFavorite.getFavoriteId()).encode();
                    }
                }
                case RATING_DESC -> {
                    if (lastResponse != null && lastResponse.reviewRating() != null) {
                        yield CursorInfo.ofRating(lastFavorite.getFavoriteId(), lastResponse.reviewRating()).encode();
                    } else {
                        yield CursorInfo.ofId(lastFavorite.getFavoriteId()).encode();
                    }
                }
                default -> CursorInfo.ofId(lastFavorite.getFavoriteId()).encode();
            };
        }

        return Page.of(responseList, requestedSize, hasNext, nextCursor, cursorInfo == null);
    }
}