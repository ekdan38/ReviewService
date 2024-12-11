package com.sparta.reviewservice.domain.dto;

import com.sparta.reviewservice.domain.entity.Product;
import lombok.Data;

@Data
public class ProductResponseDto {

    private Long id;
    private Long reviewCount;
    private Double score;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.reviewCount = product.getReviewCount();
        this.score = product.getScore();
    }
}
