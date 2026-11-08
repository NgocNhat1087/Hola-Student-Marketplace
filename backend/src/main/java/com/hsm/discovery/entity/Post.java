
package com.hsm.discovery.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.hsm.discovery.enums.*;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "area_id", nullable = false)
    private Long areaId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "reference_price", precision = 12, scale = 2)
    private BigDecimal referencePrice;

    @Enumerated(EnumType.STRING)
@Column(name = "condition_status", nullable = false)
private ConditionStatus conditionStatus;

   @Enumerated(EnumType.STRING)
@Column(name = "approval_status", nullable = false)
private ApprovalStatus approvalStatus;

    @Enumerated(EnumType.STRING)
@Column(name = "sale_status", nullable = false)
private SaleStatus saleStatus;

    @Enumerated(EnumType.STRING)
@Column(name = "display_status", nullable = false)
private DisplayStatus displayStatus;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getReferencePrice() {
        return referencePrice;
    }

    public ConditionStatus getConditionStatus() {
    return conditionStatus;
}

public ApprovalStatus getApprovalStatus() {
    return approvalStatus;
}

public SaleStatus getSaleStatus() {
    return saleStatus;
}

public DisplayStatus getDisplayStatus() {
    return displayStatus;
}

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
}
