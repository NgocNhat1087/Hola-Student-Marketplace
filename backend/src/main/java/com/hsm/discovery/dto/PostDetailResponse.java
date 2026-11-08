
package com.hsm.discovery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long id,
        Long sellerId,
        Long categoryId,
        Long areaId,
        String title,
        String description,
        BigDecimal referencePrice,
        String conditionStatus,
        String saleStatus,
        Long viewCount,
        LocalDateTime createdAt,
        List<String> imageUrls,
        List<PostSummaryResponse> similarPosts
) {
}
