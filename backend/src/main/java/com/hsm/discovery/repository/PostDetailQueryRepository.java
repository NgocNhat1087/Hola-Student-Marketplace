
package com.hsm.discovery.repository;

import com.hsm.discovery.dto.SellerInfoResponse;
import com.hsm.discovery.dto.PostCommentResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostDetailQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostDetailQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // UC16: Get seller public profile
    public SellerInfoResponse findSeller(Long sellerId) {

        String sql = """
            SELECT
                user_id,
                nickname,
                avatar_url,
                average_rating,
                seller_rank
            FROM user_profiles
            WHERE user_id = ?
            """;

        List<SellerInfoResponse> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new SellerInfoResponse(
                        rs.getLong("user_id"),
                        rs.getString("nickname"),
                        rs.getString("avatar_url"),
                        rs.getBigDecimal("average_rating"),
                        rs.getObject("seller_rank", Integer.class)
                ),
                sellerId
        );

        return results.isEmpty() ? null : results.get(0);
    }

    // UC16: Get public comments
    public List<PostCommentResponse> findComments(Long postId) {

        String sql = """
            SELECT
                c.comment_id,
                c.user_id,
                p.nickname,
                p.avatar_url,
                c.comment_content,
                c.created_at
            FROM comments c
            JOIN user_profiles p ON c.user_id = p.user_id
            WHERE c.post_id = ?
              AND c.is_deleted = FALSE
            ORDER BY c.created_at DESC, c.comment_id DESC
            LIMIT 100
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new PostCommentResponse(
                        rs.getLong("comment_id"),
                        rs.getLong("user_id"),
                        rs.getString("nickname"),
                        rs.getString("avatar_url"),
                        rs.getString("comment_content"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ),
                postId
        );
    }
}
