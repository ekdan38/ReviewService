package com.sparta.reviewservice.domain.dto;

import lombok.Data;

@Data
public class ReviewRequestDto {

    private Long userId;
    private Integer score;
    private String content;

    public ReviewRequestDto(Long userId, Integer score, String content) {
        this.userId = userId;
        this.score = score;
        this.content = content;
    }
}
