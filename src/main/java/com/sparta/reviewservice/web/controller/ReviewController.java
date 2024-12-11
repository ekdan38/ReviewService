package com.sparta.reviewservice.web.controller;

import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j(topic = "[ReviewController]")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Review 생성 => userId, score, content, MultipalrtFile 단건 이미지
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<?> createReview(@RequestPart(value = "image", required = false) MultipartFile image,
                                       @RequestPart("data") @Validated ReviewRequestDto reviewRequestDto,
                                       BindingResult bindingResult,
                                       @PathVariable("productId") Long productId) {

        // Review 등록 요청 BeanValditon 검증, 응답
        if(bindingResult.hasErrors()){
            log.error("Validation Error ! = {}", bindingResult);
            return ResponseEntity.badRequest().body(bindingResult);
        }

        // 정상 로직 수행
        reviewService.createReview(productId, reviewRequestDto, image);
        return null;
    }

    // Review 조회
    @GetMapping("/products/{productId}/reviews")
    public void getReview(@PathVariable("productId") Long productId){


    }
}
