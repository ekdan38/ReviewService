package com.sparta.reviewservice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.reviewservice.domain.dto.ReviewDto;
import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.dto.ReviewsResponseDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.entity.Review;
import com.sparta.reviewservice.domain.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReviewService reviewService;

    // JpaAudting 오류 해결
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("Review 생성 성공_이미지 포함")
    public void createReview_WithImage_Success() throws Exception {
        //given
        Long productId = 1L;

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");

        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "testImage.jpg",
                "image/jpg",
                new byte[]{0, 0, 0, 0}
        );

        MockMultipartFile mockData = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(reviewRequestDto)
        );

        Review mockReview = Review.createReview(
                Product.createProduct(),
                reviewRequestDto,
                "ImageUrl",
                "testImage.jpg",
                "S3FileName",
                "jpg");

        when(reviewService.createReview(productId, reviewRequestDto, mockImage)).thenReturn(mockReview);

        //when && then
        mockMvc.perform(multipart("/products/{productId}/reviews", productId)
                        .file(mockImage)
                        .file(mockData)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Review 생성 성공_이미지 미포함")
    public void createReview_WithoutImage_Success() throws Exception {
        //given
        Long productId = 1L;

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");

        MockMultipartFile mockData = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(reviewRequestDto)
        );

        Review mockReview = Review.createReview(
                Product.createProduct(),
                reviewRequestDto,
                null,
                null,
                null,
                null);

        when(reviewService.createReview(productId, reviewRequestDto, null)).thenReturn(mockReview);

        //when && then
        mockMvc.perform(multipart("/products/{productId}/reviews", productId)
                        .file(mockData)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Review 생성 실패_data필드 요청값 오류")
    public void createReview_Fail_DataField() throws Exception {
        //given
        Long productId = 1L;

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(null, 6, null);

        MockMultipartFile mockData = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(reviewRequestDto)
        );

        //when && then
        mockMvc.perform(multipart("/products/{productId}/reviews", productId)
                        .file(mockData)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'content')].defaultMessage").value("must not be null"))
                .andExpect(jsonPath("$.errors[?(@.field == 'score')].defaultMessage").value("score은 최대 5입니다."))
                .andExpect(jsonPath("$.errors[?(@.field == 'userId')].defaultMessage").value("userId는 필수입니다."));
    }


    @Test
    @DisplayName("Review 조회")
    public void getReview() throws Exception {
        //given
        Long productId = 1L;
        Long cursor = 5L;
        int size = 2;

        Product product = Product.createProduct();
        List<ReviewDto> reviews = new ArrayList<>();
        for(long i = 1; i <= 10; i++){
            reviews.add(new ReviewDto(Review.createReview(product,
                    new ReviewRequestDto(i, 5, "TEST"),
                    null, null, null, null)));
        }

        ReviewsResponseDto reviewsResponseDto = new ReviewsResponseDto(product, cursor, reviews);

        when(reviewService.getReviews(productId, cursor, size)).thenReturn(reviewsResponseDto);

        //when && then
        mockMvc.perform(get("/products/{productId}/reviews", productId)
                        .queryParam("cursor", Long.toString(cursor))
                        .queryParam("size",Integer.toString(size))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

}