package com.sparta.reviewservice.domain.service;

import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.entity.Review;
import com.sparta.reviewservice.domain.repository.ProductRepository;
import com.sparta.reviewservice.domain.repository.ReviewRepository;
import com.sparta.reviewservice.domain.s3.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j(topic = "[ReviewService]")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final S3Util s3Util;

    // S3 적재 부분 dummy 구현체 생성 (실제 연동 xx)
    // 유저는 하나의 상품에 대해 하나의 리뷰만 작성 가능
    @Transactional
    public void createReview(Long productId, ReviewRequestDto reviewRequestDto, MultipartFile image) {
        // DB에서 Product 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 유저가 해당 상품에 리뷰를 작성했었는지 확인
        if (reviewRepository.existsByUserIdAndProductId(reviewRequestDto.getUserId(), productId)) {
            throw new IllegalArgumentException("해당 상품에 이미 리뷰를 작성 했습니다.");
        }

        // 정상 로직 수행

        Review review;

        // image null 확인
        if (image == null) {
            review = Review.createReview(product, reviewRequestDto, null, null, null, null);
        }
        // image 값 안들어 있으면
        else if (image.isEmpty() || image.getOriginalFilename().isBlank()) {
            review = Review.createReview(product, reviewRequestDto, null, null, null, null);
        }

        // image 받았으면
        else {
            String imageUrl = s3Util.uploadImage(image);
            String s3FileName = s3Util.getFileNameForUrl(imageUrl);
            String extension = s3Util.getExtension(s3FileName);
            review = Review.createReview(product, reviewRequestDto, imageUrl, image.getOriginalFilename(), s3FileName, extension);
        }

        reviewRepository.save(review);
    }
}
