package com.sparta.reviewservice.domain.service;

import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.entity.Review;
import com.sparta.reviewservice.domain.repository.ProductRepository;
import com.sparta.reviewservice.domain.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReviewConcurrentTest {

    @Autowired
    ReviewService reviewService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("Review 생성시에 동시성 확인")
    public void ReviewConcurrent() throws InterruptedException {
        //given
        Product product = productRepository.save(Product.createProduct());

        Long productId = product.getId();

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        for (long i = 1; i <= threadCount; i++) {
            Long userId = i;
            executorService.submit(() -> {
                try {
                    System.out.println("스레드 실행 중: " + Thread.currentThread().getName());
                    System.out.println("productId = " + productId);
                    ReviewRequestDto reviewRequestDto = new ReviewRequestDto(userId, 5, "TEST");
                    Review review = reviewService.createReview(productId, reviewRequestDto, null);
                    System.out.println("review.getUserId() = " + review.getUserId());

                } catch (Exception e) {
                    System.out.println("실패: " + Thread.currentThread().getName() + " : " + e.getMessage());
                } finally {
                    System.out.println("스레드 종료 = " + Thread.currentThread().getName());
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        Long reviewCount = productRepository.findById(productId).get().getReviewCount();
        List<Review> reviews = reviewRepository.findAll();

        assertThat(reviewCount).isEqualTo(threadCount);
        assertThat(reviews.size()).isEqualTo(threadCount);
    }


    @Test
    @DisplayName("Review 1개 스레드에서 정상 작동")
    @Transactional
    public void ReviewConcurrent_test2(){
        //given
        Product product = productRepository.save(Product.createProduct());
        Long productId = product.getId();

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(200L, 5, "TEST");
        reviewService.createReview(productId, reviewRequestDto, null);

        Long reviewCount = productRepository.findById(productId).get().getReviewCount();
        assertThat(reviewCount).isEqualTo(1);
    }
}
