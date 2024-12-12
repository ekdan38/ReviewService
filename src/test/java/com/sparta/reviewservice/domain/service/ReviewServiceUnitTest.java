package com.sparta.reviewservice.domain.service;

import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.entity.Review;
import com.sparta.reviewservice.domain.repository.ProductRepository;
import com.sparta.reviewservice.domain.repository.ReviewRepository;
import com.sparta.reviewservice.domain.s3.S3Util;
import com.sparta.reviewservice.domain.service.ReviewService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceUnitTest {

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    S3Util s3Util;

    @Test
    @DisplayName("Review 생성 성공 이미지 포함")
    public void createReview_Success_withImage(){
        //given
        Long productId = 1L;
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");
        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "testImage.jpg",
                "image/jpg",
                new byte[]{0, 0, 0, 0});
        when(s3Util.uploadImage(any(MultipartFile.class))).thenReturn("ImageURL");
        Product mockProduct = Product.createProduct();
        Review mockReview = Review.createReview(
                mockProduct,
                reviewRequestDto,
                "ImageURL",
                "testImage.jpg",
                "S3FileName",
                "jpg");
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(mockProduct));
        when(reviewRepository.existsByUserIdAndProductId(any(Long.class), any(Long.class))).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(mockReview);

        //when
        Review review = reviewService.createReview(productId, reviewRequestDto, mockImage);

        //then
        Assertions.assertThat(review.getUserId()).isEqualTo(1L);
        Assertions.assertThat(review.getImageUrl()).isEqualTo("ImageURL");
        Assertions.assertThat(review.getContent()).isEqualTo("TEST");
        Assertions.assertThat(review.getScore()).isEqualTo(5);
    }

    @Test
    @DisplayName("Review 생성 성공 이미지 미포함")
    public void createReview_Success_withoutImage(){
        //given
        Long productId = 1L;
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");

        Product mockProduct = Product.createProduct();
        Review mockReview = Review.createReview(
                mockProduct,
                reviewRequestDto,
                "ImageURL",
                "testImage.jpg",
                "S3FileName",
                "jpg");
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(mockProduct));
        when(reviewRepository.existsByUserIdAndProductId(any(Long.class), any(Long.class))).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(mockReview);

        //when
        Review review = reviewService.createReview(productId, reviewRequestDto, null);

        //then
        Assertions.assertThat(review.getUserId()).isEqualTo(1L);
        Assertions.assertThat(review.getImageUrl()).isEqualTo("ImageURL");
        Assertions.assertThat(review.getContent()).isEqualTo("TEST");
        Assertions.assertThat(review.getScore()).isEqualTo(5);
    }


    @Test
    @DisplayName("Review 생성 실패_Product 존재하지 않음")
    public void createReview_Fail_NoProduct(){
        //given
        Long productId = 1L;
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");

        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "testImage.jpg",
                "image/jpg",
                new byte[]{0, 0, 0, 0});

        // when && then
        Assertions.assertThatThrownBy(() -> reviewService.createReview(productId, reviewRequestDto, mockImage))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("Review 생성 실패_이미 리뷰 작성함")
    public void createReview_Fail_AlreadyReviewed(){
        //given
        Long productId = 1L;
        Product mockProduct = Product.createProduct();
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");

        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "testImage.jpg",
                "image/jpg",
                new byte[]{0, 0, 0, 0});

        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(mockProduct));
        when(reviewRepository.existsByUserIdAndProductId(any(Long.class), any(Long.class))).thenReturn(true);


        // when && then
        Assertions.assertThatThrownBy(() -> reviewService.createReview(productId, reviewRequestDto, mockImage))
                .isInstanceOf(IllegalArgumentException.class);

    }

}