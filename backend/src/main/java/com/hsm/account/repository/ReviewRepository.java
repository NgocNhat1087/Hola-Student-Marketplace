package com.hsm.account.repository;
import com.hsm.account.entity.Review; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.List;
public interface ReviewRepository extends JpaRepository<Review,Long>{ @Query("select r from Review r where r.sellerId=:seller") List<Review> findCompletedBySeller(@Param("seller") Long seller); }
