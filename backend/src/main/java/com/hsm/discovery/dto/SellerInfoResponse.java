
package com.hsm.discovery.dto;

import java.math.BigDecimal;

public record SellerInfoResponse(
        Long userId,
        String nickname,
        String avatarUrl,
        BigDecimal averageRating,
        Integer sellerRank
) {
}
