
package com.hsm.discovery.repository;

import com.hsm.discovery.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    // UC14 + UC15: tìm kiếm và lọc bài đăng công khai
    @Query("""
        SELECT p FROM Post p
        WHERE p.approvalStatus = 'APPROVED'
          AND p.saleStatus = 'FOR_SALE'
          AND p.displayStatus = 'VISIBLE'
          AND p.expiresAt > CURRENT_TIMESTAMP
          AND (
              :keyword IS NULL
              OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
          AND (:categoryId IS NULL OR p.categoryId = :categoryId)
          AND (:areaId IS NULL OR p.areaId = :areaId)
          AND (:condition IS NULL OR p.conditionStatus = :condition)
          AND (:minPrice IS NULL OR p.referencePrice >= :minPrice)
          AND (:maxPrice IS NULL OR p.referencePrice <= :maxPrice)
        """)
    Page<Post> searchPosts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("areaId") Long areaId,
            @Param("condition") String condition,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            Pageable pageable
    );

    // UC16: tăng lượt xem trực tiếp trong database
    @Modifying
    @Query("""
        UPDATE Post p
        SET p.viewCount = p.viewCount + 1
        WHERE p.id = :postId
        """)
    int increaseViewCount(@Param("postId") Long postId);

    // UC16: sản phẩm cùng danh mục
    @Query("""
        SELECT p FROM Post p
        WHERE p.categoryId = :categoryId
          AND p.id <> :postId
          AND p.approvalStatus = 'APPROVED'
          AND p.saleStatus = 'FOR_SALE'
          AND p.displayStatus = 'VISIBLE'
          AND p.expiresAt > CURRENT_TIMESTAMP
        ORDER BY p.createdAt DESC
        """)
    Page<Post> findSimilarPosts(
            @Param("categoryId") Long categoryId,
            @Param("postId") Long postId,
            Pageable pageable
    );
}
