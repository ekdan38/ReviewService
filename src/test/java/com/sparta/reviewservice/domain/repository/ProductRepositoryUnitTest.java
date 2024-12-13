package com.sparta.reviewservice.domain.repository;

import com.sparta.reviewservice.domain.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductRepositoryUnitTest {

    @Autowired
    ProductRepository productRepository;


    @Test
    @DisplayName("Id로 Product 조회(비관적 락)")
    public void findByIdWithLock(){
        //given
        Product savedProduct = productRepository.save(Product.createProduct());
        Long savedProductId = savedProduct.getId();

        //when
        Long foundProductId = productRepository.findByIdWithLock(savedProductId).get().getId();

        //then
        Assertions.assertThat(savedProductId).isEqualTo(foundProductId);
    }

}