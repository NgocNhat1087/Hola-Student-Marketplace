
package com.hsm.discovery.dto;

import com.hsm.discovery.entity.Post;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PostSummaryResponse(
        Long id,
        String title,
        BigDecimal referencePrice,
        String conditionStatus,
        Long categoryId,
        Long areaId,
        Long viewCount,
        LocalDateTime createdAt
) {
    public static PostSummaryResponse from(Post post) {
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getReferencePrice(),
                post.getConditionStatus().name(),
                post.getCategoryId(),
                post.getAreaId(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}
