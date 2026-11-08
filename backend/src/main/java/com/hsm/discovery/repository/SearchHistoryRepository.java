
package com.hsm.discovery.repository;

import com.hsm.discovery.entity.SearchHistory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryRepository
        extends JpaRepository<SearchHistory, Long> {

    Page<SearchHistory> findByUserIdOrderBySearchedAtDesc(
            Long userId,
            Pageable pageable
    );

    long deleteByIdAndUserId(Long id, Long userId);

    long deleteByUserId(Long userId);
}
