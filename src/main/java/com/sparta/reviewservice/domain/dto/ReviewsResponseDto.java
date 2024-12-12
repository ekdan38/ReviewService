package com.sparta.reviewservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sparta.reviewservice.domain.entity.Product;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({"totalCount", "score", "cursor", "reviews"})
public class ReviewsResponseDto {
    private Long totalCount;
    private Double score;
    private Long cursor;
    private List<ReviewDto> reviews;

    public ReviewsResponseDto(Product product, Long cursor, List<ReviewDto> reviews) {
        this.totalCount = product.getReviewCount();
        this.score = product.getScore();
        this.cursor = cursor;
        this.reviews = reviews;
    }
}
