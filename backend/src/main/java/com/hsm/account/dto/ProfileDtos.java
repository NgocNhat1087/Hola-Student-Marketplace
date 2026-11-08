package com.hsm.account.dto;
import jakarta.validation.constraints.*;
public final class ProfileDtos { private ProfileDtos(){} public record ProfileResponse(Long userId,String nickname,String avatarUrl,String coverPhotoUrl,String bio,Double averageRating,Integer ratingCount,Integer sellerRank){} public record UpdateProfileRequest(@NotBlank @Size(max=80) String nickname,@Size(max=1000) String bio){} }
