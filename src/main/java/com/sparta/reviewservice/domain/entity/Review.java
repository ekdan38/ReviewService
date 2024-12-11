package com.sparta.reviewservice.domain.entity;

import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String content;

    private String originalFileName;

    private String s3FileName;

    private String extension;

    private Review(Product product, ReviewRequestDto reviewRequestDto, String originalFileName, String s3FileName, String extension) {
        this.product = product;
        this.userId = reviewRequestDto.getUserId();
        this.score = reviewRequestDto.getScore();
        this.content = reviewRequestDto.getContent();
        this.originalFileName = originalFileName;
        this.s3FileName = s3FileName;
        this.extension = extension;
    }
    public static Review createReview(Product product, ReviewRequestDto reviewRequestDto, String originalFileName, String s3FileName, String extension){
        return new Review(product, reviewRequestDto, originalFileName, s3FileName, extension);
    }
}
