package com.sparta.reviewservice.domain.repository;

import com.sparta.reviewservice.domain.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);


    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.id < :cursor ORDER BY r.id DESC")
    List<Review> findByProductAndCursor(@Param("productId") Long productId,
                                        @Param("cursor") Long cursor,
                                        Pageable pageable);
}
