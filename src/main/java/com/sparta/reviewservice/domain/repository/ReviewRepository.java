package com.sparta.reviewservice.domain.repository;

import com.sparta.reviewservice.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
