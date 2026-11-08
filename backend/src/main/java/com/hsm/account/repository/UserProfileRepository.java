package com.hsm.account.repository;
import com.hsm.account.entity.UserProfile; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserProfileRepository extends JpaRepository<UserProfile,Long>{}
