package com.sparta.reviewservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.reviewservice.domain.dto.ProductResponseDto;
import com.sparta.reviewservice.domain.dto.ReviewRequestDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.entity.Review;
import com.sparta.reviewservice.domain.exception.errorcode.ErrorCode;
import com.sparta.reviewservice.domain.repository.ProductRepository;
import com.sparta.reviewservice.domain.repository.ReviewRepository;
import com.sparta.reviewservice.domain.service.ProductService;
import com.sparta.reviewservice.domain.service.ReviewService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Random;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProductService productService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductRepository productRepository;


    Long productId;

    @BeforeEach
    @Transactional
    void createProduct(){
        ProductResponseDto products = productService.createProducts();
        productId = products.getId();
    }

    @Test
    @DisplayName("BeforeEach 정상 작동 확인")
    public void ReviewIntegrationTest(){
        // when && then
        Long foundId = productRepository.findById(productId).get().getId();
        Assertions.assertThat(foundId).isNotNull();
    }


    @Test
    @DisplayName("Review 생성 성공_이미지 포함")
    @Transactional
    public void createReview_WithImage_Success() throws Exception {
        //given
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");

        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "testImage.jpg",
                "image/jpg",
                new byte[]{0, 0, 0, 0});

        MockMultipartFile mockData = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(reviewRequestDto)
        );

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
    @Transactional
    public void createReview_WithoutImage_Success() throws Exception {
        //given
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");

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
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Review 생성 실패_Data 필드 값 오류")
    @Transactional
    public void createReview_Fail_DataField() throws Exception {
        //given
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
    @DisplayName("Review 생성 실패_Review 이미 생성")
    @Transactional
    public void createReview_Fail_AlreadyReviewed() throws Exception {
        //given
        ReviewRequestDto reviewRequestDto1 = new ReviewRequestDto(1L, 5, "TEST");
        ReviewRequestDto reviewRequestDto2 = new ReviewRequestDto(1L, 5, "TEST");


        MockMultipartFile mockData1 = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(reviewRequestDto1)
        );

        MockMultipartFile mockData2 = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(reviewRequestDto2)
        );

        mockMvc.perform(multipart("/products/{productId}/reviews", productId)
                .file(mockData1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA));


        //when && then
        mockMvc.perform(multipart("/products/{productId}/reviews", productId)
                        .file(mockData2)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(ErrorCode.REVIEW_ALREADY_EXISTS.getErrorCode()))
                .andExpect(jsonPath("errorMessage").value(ErrorCode.REVIEW_ALREADY_EXISTS.getErrorMessage()));
    }

    @Test
    @DisplayName("Review 생성 실패_확장자 없음")
    @Transactional
    public void createReview_Fail_EmptyImage() throws Exception {
        //given
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "testImage",
                "image/jpg",
                new byte[]{0, 0, 0, 0});

        MockMultipartFile mockData = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(reviewRequestDto)
        );

        //when && then
        mockMvc.perform(multipart("/products/{productId}/reviews", productId)
                        .file(image)
                        .file(mockData)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(ErrorCode.S3_NO_EXTENSION.getErrorCode()))
                .andExpect(jsonPath("errorMessage").value(ErrorCode.S3_NO_EXTENSION.getErrorMessage()));
    }

    @Test
    @DisplayName("Review 생성 실패_지원 안하는 확장자")
    @Transactional
    public void createReview_Fail_NotSupportExtension() throws Exception {
        //given
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(1L, 5, "TEST");

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "testImage.gif",
                "image/gif",
                new byte[]{0, 0, 0, 0});

        MockMultipartFile mockData = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(reviewRequestDto)
        );

        //when && then
        mockMvc.perform(multipart("/products/{productId}/reviews", productId)
                        .file(image)
                        .file(mockData)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(ErrorCode.S3_NOT_SUPPORT_EXTENSION.getErrorCode()))
                .andExpect(jsonPath("errorMessage").value(ErrorCode.S3_NOT_SUPPORT_EXTENSION.getErrorMessage()));
    }


    @Test
    @DisplayName("Review 조회")
    @Transactional
    public void getReviews() throws Exception {
        //given
        int cursor = 12;
        int size = 5;
        int totalCount = 20;

        int sum = 0;
        for(long i = 1; i <= totalCount; i++){
            int score = new Random().nextInt(5) + 1;
            sum += score;

            ReviewRequestDto reviewRequestDto = new ReviewRequestDto(i, score, "TEST");

            if(i % 2 == 0){
                reviewService.createReview(productId, reviewRequestDto, null);
            }
            else {
                MockMultipartFile image = new MockMultipartFile(
                        "image",
                        "testImage.jpg",
                        "image/jpg",
                        new byte[]{0, 0, 0, 0});
                reviewService.createReview(productId, reviewRequestDto, image);
            }
        }

        double score = (double) sum / 20;

        //when && then
        mockMvc.perform(get("/products/{productId}/reviews", productId)
                        .queryParam("cursor", Integer.toString(cursor))
                        .queryParam("size", Integer.toString(size))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalCount").value(20))
                .andExpect(jsonPath("score").value(closeTo(score, 0.1)))
                .andExpect(jsonPath("cursor").value(7))
                .andExpect(jsonPath("$.reviews[0].id").value(11))
                .andExpect(jsonPath("$.reviews[0].imageUrl").doesNotExist())
                .andExpect(jsonPath("$.reviews[1].id").value(10))
                .andExpect(jsonPath("$.reviews[1].imageUrl").exists())
                .andExpect(jsonPath("$.reviews[2].id").value(9))
                .andExpect(jsonPath("$.reviews[2].imageUrl").doesNotExist())
                .andExpect(jsonPath("$.reviews[3].id").value(8))
                .andExpect(jsonPath("$.reviews[3].imageUrl").exists())
                .andExpect(jsonPath("$.reviews[4].id").value(7))
                .andExpect(jsonPath("$.reviews[4].imageUrl").doesNotExist());
    }

}
