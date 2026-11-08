
package com.hsm.discovery.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "search_histories")
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "keyword", nullable = false, length = 200)
    private String keyword;

    @Column(name = "searched_at")
    private LocalDateTime searchedAt;

    @PrePersist
    public void beforeSave() {
        if (searchedAt == null) {
            searchedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getKeyword() { return keyword; }
    public LocalDateTime getSearchedAt() { return searchedAt; }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
