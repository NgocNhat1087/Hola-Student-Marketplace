
package com.hsm.discovery.repository;

import com.hsm.discovery.entity.ViewingHistory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ViewingHistoryRepository
        extends JpaRepository<ViewingHistory, Long> {

    Page<ViewingHistory> findByUserIdOrderByViewedAtDesc(
            Long userId,
            Pageable pageable
    );

    @Modifying
    @Query(value = """
        INSERT INTO viewing_histories
            (user_id, post_id, viewed_at)
        VALUES
            (:userId, :postId, CURRENT_TIMESTAMP)
        ON DUPLICATE KEY UPDATE
            viewed_at = CURRENT_TIMESTAMP
        """, nativeQuery = true)
    int recordView(
            @Param("userId") Long userId,
            @Param("postId") Long postId
    );
}
