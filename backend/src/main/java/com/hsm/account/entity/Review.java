package com.hsm.account.entity;
import jakarta.persistence.*;
@Entity @Table(name="reviews", indexes=@Index(name="idx_reviews_seller", columnList="seller_id"))
public class Review { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="review_id") private Long id; @Column(name="seller_id",nullable=false) private Long sellerId; @Column(nullable=false) private byte rating; public Long getSellerId(){return sellerId;} public byte getRating(){return rating;} }
