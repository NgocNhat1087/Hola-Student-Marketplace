package com.hsm.account.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="user_profiles")
public class UserProfile {
  @Id private Long userId; @OneToOne(fetch=FetchType.LAZY) @MapsId @JoinColumn(name="user_id") private User user;
  @Column(nullable=false,length=80) private String nickname; @Column(length=500) private String avatarUrl; @Column(length=500) private String coverPhotoUrl; @Column(length=1000) private String bio; private Instant updatedAt=Instant.now();
  public UserProfile(){} public UserProfile(User u,String n){user=u;nickname=n;} public Long getUserId(){return userId;} public String getNickname(){return nickname;} public void setNickname(String v){nickname=v;} public String getAvatarUrl(){return avatarUrl;} public void setAvatarUrl(String v){avatarUrl=v;} public String getCoverPhotoUrl(){return coverPhotoUrl;} public void setCoverPhotoUrl(String v){coverPhotoUrl=v;} public String getBio(){return bio;} public void setBio(String v){bio=v;} public void touch(){updatedAt=Instant.now();}
}
