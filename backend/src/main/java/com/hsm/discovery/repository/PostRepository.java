
package com.hsm.discovery.repository;

import com.hsm.discovery.entity.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // =====================================================
    // UC14: FIND POST IDS MATCHING TAG KEYWORD
    // =====================================================

    @Query(value = """
        SELECT DISTINCT pt.post_id
        FROM post_tags pt
        JOIN tags t ON pt.tag_id = t.tag_id
        WHERE LOWER(t.tag_name) LIKE
              LOWER(CONCAT('%', :keyword, '%'))
        """, nativeQuery = true)
    List<Long> findPostIdsByTagKeyword(
            @Param("keyword") String keyword
    );

    // =====================================================
    // UC14 + UC15: SEARCH AND REFINE RESULTS
    // =====================================================

    @Query("""
        SELECT p FROM Post p
        WHERE p.approvalStatus = 'APPROVED'
          AND p.saleStatus = 'FOR_SALE'
          AND p.displayStatus = 'VISIBLE'
          AND p.expiresAt > CURRENT_TIMESTAMP

          AND (
              :keyword IS NULL
              OR LOWER(p.title) LIKE
                 LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(p.description) LIKE
                 LOWER(CONCAT('%', :keyword, '%'))
              OR p.id IN :tagPostIds
          )

          AND (:categoryId IS NULL
               OR p.categoryId = :categoryId)

          AND (:areaId IS NULL
               OR p.areaId = :areaId)

          AND (:condition IS NULL
               OR p.conditionStatus = :condition)

          AND (:minPrice IS NULL
               OR p.referencePrice >= :minPrice)

          AND (:maxPrice IS NULL
               OR p.referencePrice <= :maxPrice)
        """)
    Page<Post> searchPosts(
            @Param("keyword") String keyword,
            @Param("tagPostIds") List<Long> tagPostIds,
            @Param("categoryId") Long categoryId,
            @Param("areaId") Long areaId,
            @Param("condition") String condition,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    // =====================================================
    // UC16: INCREASE POST VIEW COUNT
    // =====================================================

    @Modifying
    @Query("""
        UPDATE Post p
        SET p.viewCount = p.viewCount + 1
        WHERE p.id = :postId
        """)
    int increaseViewCount(
            @Param("postId") Long postId
    );

    // =====================================================
    // UC16: FIND SIMILAR POSTS
    // =====================================================

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
