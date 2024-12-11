package com.sparta.reviewservice.domain.entity;

import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.print.attribute.standard.MediaSize;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Long userId;

    private Integer score;

    private String content;

    private Review(Product product, ReviewRequestDto reviewRequestDto) {
        this.product = product;
        this.userId = reviewRequestDto.getUserId();
        this.score = reviewRequestDto.getScore();
        this.content = reviewRequestDto.getContent();
    }
    public static Review createReview(Product product, ReviewRequestDto reviewRequestDto){
        return new Review(product, reviewRequestDto);
    }
}
