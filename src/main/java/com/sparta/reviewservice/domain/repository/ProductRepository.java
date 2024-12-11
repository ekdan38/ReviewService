package com.sparta.reviewservice.domain.repository;


import com.sparta.reviewservice.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
