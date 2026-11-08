
package com.hsm.discovery.service;

import com.hsm.discovery.entity.*;
import com.hsm.discovery.repository.*;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HistoryService {

    private final SearchHistoryRepository searchRepository;
    private final ViewingHistoryRepository viewingRepository;

    public HistoryService(
            SearchHistoryRepository searchRepository,
            ViewingHistoryRepository viewingRepository
    ) {
        this.searchRepository = searchRepository;
        this.viewingRepository = viewingRepository;
    }

    // UC14: lưu từ khóa tìm kiếm
    @Transactional
    public void saveSearch(Long userId, String keyword) {

        if (userId == null
                || keyword == null
                || keyword.isBlank()) {
            return;
        }

        String value = keyword.trim();

        if (value.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Keyword is too long"
            );
        }

        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(value);

        searchRepository.save(history);
    }

    // UC16: ghi lịch sử xem
    @Transactional
    public void saveView(Long userId, Long postId) {

        if (userId != null && postId != null) {
            viewingRepository.recordView(userId, postId);
        }
    }

    // UC13
    @Transactional(readOnly = true)
    public Page<ViewingHistory> getViewingHistory(
            Long userId,
            int page,
            int size
    ) {
        Pageable pageable = createPageable(page, size);

        return viewingRepository
                .findByUserIdOrderByViewedAtDesc(
                        userId, pageable
                );
    }

    // UC17: xem lịch sử tìm kiếm
    @Transactional(readOnly = true)
    public Page<SearchHistory> getSearchHistory(
            Long userId,
            int page,
            int size
    ) {
        Pageable pageable = createPageable(page, size);

        return searchRepository
                .findByUserIdOrderBySearchedAtDesc(
                        userId, pageable
                );
    }

    // UC17: xóa một từ khóa
    @Transactional
    public void deleteSearch(Long userId, Long searchId) {

        long deleted = searchRepository
                .deleteByIdAndUserId(searchId, userId);

        if (deleted == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Search history not found"
            );
        }
    }

    // UC17: xóa tất cả lịch sử
    @Transactional
    public void clearSearchHistory(Long userId) {
        searchRepository.deleteByUserId(userId);
    }

    private Pageable createPageable(int page, int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination"
            );
        }

        return PageRequest.of(page, size);
    }
}
