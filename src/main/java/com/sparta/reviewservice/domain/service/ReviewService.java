package com.sparta.reviewservice.domain.service;

import com.sparta.reviewservice.domain.dto.ReviewDto;
import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.dto.ReviewsResponseDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.entity.Review;
import com.sparta.reviewservice.domain.exception.errorcode.ErrorCode;
import com.sparta.reviewservice.domain.exception.ProductException;
import com.sparta.reviewservice.domain.exception.ReviewException;
import com.sparta.reviewservice.domain.repository.ProductRepository;
import com.sparta.reviewservice.domain.repository.ReviewRepository;
import com.sparta.reviewservice.domain.s3.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "[ReviewService]")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final S3Util s3Util;

    // 리뷰 생성
    // 유저는 하나의 상품에 대해 하나의 리뷰만 작성 가능
    // 여러 유저가 하나의 상품에 리뷰를 작성할때 동시성 고려해야함.
    @Transactional
    public Review createReview(Long productId, ReviewRequestDto reviewRequestDto, MultipartFile image) {
        // DB에서 Product 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));

        // 유저가 해당 상품에 리뷰를 작성했었는지 확인
        if (reviewRepository.existsByUserIdAndProductId(reviewRequestDto.getUserId(), productId)) {
            throw new ReviewException(ErrorCode.REVIEW_ALREADY_EXISTS);
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

        return reviewRepository.save(review);
    }

    // 리뷰 조회 (Cursor 기반 페이징)
    // 가장 최근에 작성된 리뷰 순 => jpaAudting사용해서 createdAt 저장하고, updateable = false로 업데이트 불가
    // 즉, Pk id 순서가 작성된 리뷰 순서
    public ReviewsResponseDto getReviews(Long productId, Long cursor, int size){
        // 해당 Product랑 페이징한 Review가 필요...
        // => Product 따로 불러오고 페이징 따로 하자.
        // 1. Product 찾기
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));


        // 2. 가장 최근에 작성된 리뷰 순으로 가져와야한다.
        // 최초에 무조건 맨뒤부터
        if(cursor == 0){
            cursor = Long.MAX_VALUE;
        }

        Pageable pageable = PageRequest.of(0, size);
        List<ReviewDto> pagingList = reviewRepository.findByProductAndCursor(productId, cursor, pageable)
                .stream().map(ReviewDto::new).collect(Collectors.toList());

        // cursor 반환
        Long nextCursor;
        if(pagingList.isEmpty()) nextCursor = null;
        else nextCursor = pagingList.get(pagingList.size() - 1).getId();

        return new ReviewsResponseDto(product, nextCursor, pagingList);
    }
}
