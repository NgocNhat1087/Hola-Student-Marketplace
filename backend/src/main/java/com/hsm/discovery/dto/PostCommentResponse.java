
package com.hsm.discovery.dto;

import java.time.LocalDateTime;

public record PostCommentResponse(
        Long commentId,
        Long userId,
        String nickname,
        String avatarUrl,
        String content,
        LocalDateTime createdAt
) {
}
