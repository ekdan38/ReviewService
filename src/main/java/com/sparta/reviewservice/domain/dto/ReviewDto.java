package com.sparta.reviewservice.domain.dto;

import com.sparta.reviewservice.domain.entity.Review;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {

    private Long id;
    private Long userId;
    private Integer score;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    public ReviewDto(Review review) {
        this.id = review.getId();
        this.userId = review.getUserId();
        this.score = review.getScore();
        this.content = review.getContent();
        this.imageUrl = review.getImageUrl();
        this.createdAt = review.getCreatedAt();
    }
}
