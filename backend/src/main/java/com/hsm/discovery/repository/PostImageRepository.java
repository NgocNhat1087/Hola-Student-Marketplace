
package com.hsm.discovery.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository
        extends Repository<com.hsm.discovery.entity.Post, Long> {

    @Query(value = """
        SELECT image_url
        FROM post_images
        WHERE post_id = :postId
        ORDER BY display_order ASC
        """, nativeQuery = true)
    List<String> findImageUrlsByPostId(
            @Param("postId") Long postId
    );
}
