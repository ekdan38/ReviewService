package com.sparta.reviewservice.domain.service;

import com.sparta.reviewservice.domain.dto.ProductResponseDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "[ProductService]")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto createProducts(){
        Product product = Product.createProduct();
        productRepository.save(product);
        return new ProductResponseDto(product);
    }

}
