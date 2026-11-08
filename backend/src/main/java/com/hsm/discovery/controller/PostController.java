
package com.hsm.discovery.controller;

import com.hsm.discovery.dto.PostDetailResponse;
import com.hsm.discovery.dto.PostSummaryResponse;
import com.hsm.discovery.security.CurrentUserResolver;
import com.hsm.discovery.service.HistoryService;
import com.hsm.discovery.service.PostService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final HistoryService historyService;
    private final CurrentUserResolver currentUser;

    public PostController(
            PostService postService,
            HistoryService historyService,
            CurrentUserResolver currentUser
    ) {
        this.postService = postService;
        this.historyService = historyService;
        this.currentUser = currentUser;
    }

    // UC14 + UC15: Tìm kiếm, lọc và sắp xếp
    @GetMapping
    public Page<PostSummaryResponse> searchPosts(
            HttpServletRequest request,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        if (keyword != null && keyword.length() > 200) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Keyword must not exceed 200 characters"
            );
        }

        Page<PostSummaryResponse> result =
                postService.searchPosts(
                        keyword,
                        categoryId,
                        areaId,
                        condition,
                        minPrice,
                        maxPrice,
                        sort,
                        page,
                        size
                );

        Long userId = currentUser.optionalUserId(request);

        if (keyword != null && !keyword.isBlank()) {
            historyService.saveSearch(userId, keyword);
        }

        return result;
    }

    // UC16: Xem chi tiết sản phẩm
    @GetMapping("/{postId}")
    public PostDetailResponse getPostDetail(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        PostDetailResponse result =
                postService.getPostDetail(postId);

        Long userId = currentUser.optionalUserId(request);

        historyService.saveView(userId, postId);

        return result;
    }
}
