
package com.hsm.discovery.controller;

import com.hsm.discovery.entity.SearchHistory;
import com.hsm.discovery.entity.ViewingHistory;
import com.hsm.discovery.security.CurrentUserResolver;
import com.hsm.discovery.service.HistoryService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class HistoryController {

    private final HistoryService historyService;
    private final CurrentUserResolver currentUser;

    public HistoryController(
            HistoryService historyService,
            CurrentUserResolver currentUser
    ) {
        this.historyService = historyService;
        this.currentUser = currentUser;
    }

    // UC13: Xem lịch sử sản phẩm đã xem
    @GetMapping("/viewing-history")
    public Page<ViewingHistory> viewingHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = currentUser.requiredUserId(request);

        return historyService.getViewingHistory(
                userId, page, size
        );
    }

    // UC17: Xem lịch sử tìm kiếm
    @GetMapping("/search-history")
    public Page<SearchHistory> searchHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = currentUser.requiredUserId(request);

        return historyService.getSearchHistory(
                userId, page, size
        );
    }

    // UC17: Xóa một từ khóa đã tìm kiếm
    @DeleteMapping("/search-history/{searchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSearch(
            HttpServletRequest request,
            @PathVariable Long searchId
    ) {
        Long userId = currentUser.requiredUserId(request);

        historyService.deleteSearch(userId, searchId);
    }

    // UC17: Xóa toàn bộ lịch sử tìm kiếm
    @DeleteMapping("/search-history")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearSearch(
            HttpServletRequest request
    ) {
        Long userId = currentUser.requiredUserId(request);

        historyService.clearSearchHistory(userId);
    }
}
