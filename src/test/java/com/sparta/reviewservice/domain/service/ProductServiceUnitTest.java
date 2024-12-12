package com.sparta.reviewservice.domain.service;

import com.sparta.reviewservice.domain.dto.ProductResponseDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.repository.ProductRepository;
import com.sparta.reviewservice.domain.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("상품 생성")
    public void createProducts(){
        //given
        Product product = Product.createProduct();
        when(productRepository.save(any(Product.class))).thenReturn(product);

        //when
        ProductResponseDto products = productService.createProducts();

        //then
        Assertions.assertThat(products.getId()).isEqualTo(product.getId());
        Assertions.assertThat(products.getScore()).isEqualTo(product.getScore());
        Assertions.assertThat(products.getReviewCount()).isEqualTo(product.getReviewCount());
    }




}