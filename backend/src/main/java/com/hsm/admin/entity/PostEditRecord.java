package com.hsm.admin.entity;
import jakarta.persistence.*;
/** Read model mapped to the existing MySQL table; multi-table writes use native JPA queries within transactional services. */
@Entity
@org.hibernate.annotations.Immutable
@Table(name="post_edits")
public class PostEditRecord {
  public enum EditStatus { PENDING, APPROVED, REJECTED }
  @Id @Column(name="edit_id") private Long id;
  @Column(name="post_id") private Long postId;
  @Enumerated(EnumType.STRING)
  @Column(name="edit_status", nullable=false) private EditStatus editStatus;
  protected PostEditRecord() {}
  public Long getId(){return id;}
  public Long getPostId(){return postId;}
  public String getEditStatus(){return editStatus == null ? null : editStatus.name();}
}
