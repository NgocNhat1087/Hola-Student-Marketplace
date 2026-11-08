
package com.hsm.discovery.service;

import com.hsm.discovery.dto.PostDetailResponse;
import com.hsm.discovery.dto.PostSummaryResponse;
import com.hsm.discovery.entity.Post;
import com.hsm.discovery.repository.PostRepository;
import com.hsm.discovery.repository.PostImageRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hsm.discovery.repository.PostDetailQueryRepository;
import com.hsm.discovery.dto.SellerInfoResponse;
import com.hsm.discovery.dto.PostCommentResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
private final PostImageRepository postImageRepository;
private final PostDetailQueryRepository detailQueryRepository;

public PostService(
        PostRepository postRepository,
        PostImageRepository postImageRepository,
        PostDetailQueryRepository detailQueryRepository
) {
    this.postRepository = postRepository;
    this.postImageRepository = postImageRepository;
    this.detailQueryRepository = detailQueryRepository;
}

    // =====================================================
    // UC14 + UC15: SEARCH, FILTER AND SORT PRODUCTS
    // =====================================================

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

        // 1. Validate pagination
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination"
            );
        }

        // 2. Validate price range
        if ((minPrice != null && minPrice.signum() < 0)
                || (maxPrice != null && maxPrice.signum() < 0)
                || (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid price range"
            );
        }

        // 3. Validate product condition
        if (condition != null && !List.of(
                "EXCELLENT",
                "GOOD",
                "FAIR",
                "HEAVILY_USED",
                "NEEDS_REPAIR"
        ).contains(condition)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid condition"
            );
        }

        // 4. Normalize keyword
        String normalizedKeyword =
                keyword == null || keyword.isBlank()
                        ? null
                        : keyword.trim();

                        // UC14: Find posts matching tags
List<Long> tagPostIds;

if (normalizedKeyword == null) {
    tagPostIds = List.of(-1L);
} else {
    tagPostIds = postRepository.findPostIdsByTagKeyword(
            normalizedKeyword
    );

    // Avoid passing an empty collection to JPQL IN
    if (tagPostIds.isEmpty()) {
        tagPostIds = List.of(-1L);
    }
}

        // 5. Sorting options
        String sortOption =
                sort == null || sort.isBlank()
                        ? "newest"
                        : sort;

        Sort sorting = switch (sortOption) {

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

        // 6. Pagination
        Pageable pageable = PageRequest.of(
                page,
                size,
                sorting
        );

        // 7. Search database and return DTO
        return postRepository.searchPosts(
        normalizedKeyword,
        tagPostIds,
        categoryId,
        areaId,
        condition,
        minPrice,
        maxPrice,
        pageable
).map(PostSummaryResponse::from);
    }

    // =====================================================
    // UC16: VIEW PRODUCT DETAILS
    // =====================================================

    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {

        // 1. Validate post ID
        if (postId == null || postId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid post ID"
            );
        }

        // 2. Find product by ID
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post not found"
                ));

        // 3. Check approval and visibility
        if (!"APPROVED".equals(post.getApprovalStatus())
                || !"VISIBLE".equals(post.getDisplayStatus())
                || post.getExpiresAt() == null
                || !post.getExpiresAt().isAfter(LocalDateTime.now())) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Post is not publicly available"
            );
        }

        // 4. Increase view count
        postRepository.increaseViewCount(postId);

        // 5. Get similar products
        List<PostSummaryResponse> similarPosts =
                postRepository.findSimilarPosts(
                        post.getCategoryId(),
                        postId,
                        PageRequest.of(0, 5)
                )
                .getContent()
                .stream()
                .map(PostSummaryResponse::from)
                .toList();

        // 6. Get product images
        List<String> imageUrls =
                postImageRepository.findImageUrlsByPostId(postId);

        // Get seller public information
        SellerInfoResponse seller =
        detailQueryRepository.findSeller(post.getSellerId());

        // Get public comments
        List<PostCommentResponse> comments =
        detailQueryRepository.findComments(postId);

        // 7. Return product details
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
        imageUrls,
        seller,
        comments,
        similarPosts
);
    }
}
