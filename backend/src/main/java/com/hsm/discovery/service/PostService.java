
package com.hsm.discovery.service;

import com.hsm.discovery.dto.PostDetailResponse;
import com.hsm.discovery.dto.PostSummaryResponse;
import com.hsm.discovery.entity.Post;
import com.hsm.discovery.repository.PostRepository;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // UC14 + UC15
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> searchPosts(
            String keyword,
            Long categoryId,
            Long areaId,
            String condition,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sort,
            int page,
            int size
    ) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination"
            );
        }

        if (minPrice != null && minPrice.signum() < 0
                || maxPrice != null && maxPrice.signum() < 0
                || minPrice != null && maxPrice != null
                   && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid price range"
            );
        }

        if (condition != null && !List.of(
                "EXCELLENT", "GOOD", "FAIR",
                "HEAVILY_USED", "NEEDS_REPAIR"
        ).contains(condition)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid condition"
            );
        }

        String normalizedKeyword = keyword == null
                || keyword.isBlank()
                ? null
                : keyword.trim();

        Sort sorting = switch (sort) {
            case "price_asc" ->
                    Sort.by("referencePrice").ascending()
                        .and(Sort.by("id").descending());
            case "price_desc" ->
                    Sort.by("referencePrice").descending()
                        .and(Sort.by("id").descending());
            case "views" ->
                    Sort.by("viewCount").descending()
                        .and(Sort.by("id").descending());
            case "newest" ->
                    Sort.by("createdAt").descending()
                        .and(Sort.by("id").descending());
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort option"
            );
        };

        Pageable pageable = PageRequest.of(
                page, size, sorting
        );

        return postRepository.searchPosts(
                normalizedKeyword,
                categoryId,
                areaId,
                condition,
                minPrice,
                maxPrice,
                pageable
        ).map(PostSummaryResponse::from);
    }

    // UC16
    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {

        if (postId == null || postId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid post ID"
            );
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post not found"
                ));

        if (!"APPROVED".equals(post.getApprovalStatus())
                || !"VISIBLE".equals(post.getDisplayStatus())
                || post.getExpiresAt() == null
                || !post.getExpiresAt().isAfter(LocalDateTime.now())) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Post is not publicly available"
            );
        }

        // Tang luot xem
        postRepository.increaseViewCount(postId);

        // Lay danh sach san pham cung danh muc
        List<PostSummaryResponse> similarPosts =
                postRepository.findSimilarPosts(
                        post.getCategoryId(),
                        postId,
                        PageRequest.of(0, 5)
                ).getContent().stream()
                        .map(PostSummaryResponse::from)
                        .toList();

        return new PostDetailResponse(
                post.getId(),
                post.getSellerId(),
                post.getCategoryId(),
                post.getAreaId(),
                post.getTitle(),
                post.getDescription(),
                post.getReferencePrice(),
                post.getConditionStatus(),
                post.getSaleStatus(),
                post.getViewCount() + 1,
                post.getCreatedAt(),
                similarPosts
        );
    }
}
