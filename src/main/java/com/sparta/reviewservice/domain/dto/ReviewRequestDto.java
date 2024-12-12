package com.sparta.reviewservice.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDto {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotNull(message = "socre은 필수입니다.")
    @Min(value = 1, message = "score은 최소 1입니다.")
    @Max(value = 5, message = "score은 최대 5입니다.")
    private Integer score;

    @NotNull
    private String content;

    public ReviewRequestDto(Long userId, Integer score, String content) {
        this.userId = userId;
        this.score = score;
        this.content = content;
    }
}
