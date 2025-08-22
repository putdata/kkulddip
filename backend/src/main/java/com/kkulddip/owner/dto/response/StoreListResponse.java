package com.kkulddip.owner.dto.response;

import java.util.List;

public record StoreListResponse(
    List<OwnerStoreResponse> stores,
    Integer totalCount,
    Integer activeCount,
    Integer inactiveCount,
    Boolean hasNext,
    String nextCursor
) {}