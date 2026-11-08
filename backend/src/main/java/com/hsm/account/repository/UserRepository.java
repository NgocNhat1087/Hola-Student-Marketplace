package com.hsm.account.repository;
import com.hsm.account.entity.User; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Long>{ Optional<User> findByEmailIgnoreCase(String email); Optional<User> findByGoogleSubject(String subject); }
