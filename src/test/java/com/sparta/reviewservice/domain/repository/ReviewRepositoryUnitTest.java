package com.sparta.reviewservice.domain.repository;

import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.entity.Review;
import com.sparta.reviewservice.domain.repository.ProductRepository;
import com.sparta.reviewservice.domain.repository.ReviewRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryUnitTest {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("UserId, ProductId로 Review 존재 True")
    @Transactional
    public void existsByUserIdAndProductId_True() {
        //given
        Long userId = 1L;
        Integer score = 5;
        String content = "TEST";

        Product product = productRepository.save(Product.createProduct());
        reviewRepository.save(Review.createReview(product,
                new ReviewRequestDto(
                        userId,
                        score,
                        content),
                null,
                null,
                null,
                null));
        //when
        boolean existsByUserIdAndProductId = reviewRepository.existsByUserIdAndProductId(userId, product.getId());

        //then
        assertThat(existsByUserIdAndProductId).isTrue();
    }

    @Test
    @DisplayName("UserId, ProductId로 Review 존재 False")
    @Transactional
    public void existsByUserIdAndProductId_False() {
        //given
        Long userId = 1L;
        Integer score = 5;
        String content = "TEST";

        Product product = productRepository.save(Product.createProduct());
        reviewRepository.save(Review.createReview(product,
                new ReviewRequestDto(
                        userId,
                        score,
                        content),
                null,
                null,
                null,
                null));
        //when
        boolean existsByUserIdAndProductId1 = reviewRepository.existsByUserIdAndProductId(2L, product.getId());
        boolean existsByUserIdAndProductId2 = reviewRepository.existsByUserIdAndProductId(2L, 2L);

        //then
        assertThat(existsByUserIdAndProductId1).isFalse();
        assertThat(existsByUserIdAndProductId2).isFalse();
    }

    @Test
    @DisplayName("ProductId, Cursor, Pageable 로 페이징")
    @Transactional
    public void findByProductAndCursor() {
        //given
        Product product = productRepository.save(Product.createProduct());
        generateReviews(product);
        PageRequest pageRequest = PageRequest.of(0, 5);

        //when
        List<Review> page1 = reviewRepository.findByProductAndCursor(product.getId(), 21L, pageRequest);
        List<Review> page2 = reviewRepository.findByProductAndCursor(product.getId(), 0L, pageRequest);

        //then
        assertThat(page1.size()).isEqualTo(5);
        assertThat(page1.get(page1.size() - 1).getId()).isEqualTo(16L);
        assertThat(page1.get(page1.size() - 1).getImageUrl()).isEqualTo("/imageUrl");
        assertThat(page1.get(page1.size() - 2).getImageUrl()).isNull();

        assertThat(page2.size()).isEqualTo(0);
    }


    private void generateReviews(Product product){
        for (long i = 1; i <= 20; i++) {
            if (i % 2 == 0) {
                reviewRepository.save(Review.createReview(product,
                        new ReviewRequestDto(
                                i,
                                new Random().nextInt(5) + 1,
                                "TEST"),
                        "/imageUrl",
                        "testImage.jpg",
                        "s3FileName.jpg",
                        "jpg"));
            } else {
                reviewRepository.save(Review.createReview(product,
                        new ReviewRequestDto(
                                i,
                                new Random().nextInt(5) + 1,
                                "TEST"),
                        null,
                        null,
                        null,
                        null));
            }
        }
    }
}