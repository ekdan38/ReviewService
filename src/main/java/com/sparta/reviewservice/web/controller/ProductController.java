package com.sparta.reviewservice.web.controller;

import com.sparta.reviewservice.domain.dto.ProductResponseDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j(topic = "[ProductController]")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 상품 등록
    @PostMapping("/products")
    public ProductResponseDto createProducts() {
        return productService.createProducts();
    }


}
