package com.sparta.reviewservice.web.controller;

import com.sparta.reviewservice.domain.dto.ProductResponseDto;
import com.sparta.reviewservice.domain.entity.Product;
import com.sparta.reviewservice.domain.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    // @MockBean deprecated 됐다.
    @MockitoBean
    ProductService productService;

    // JpaAudting 오류 해결
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("Product 생성")
    public void createProducts() throws Exception {
        //given
        ProductResponseDto mockProductResponseDto = new ProductResponseDto(Product.createProduct());
        when(productService.createProducts()).thenReturn(mockProductResponseDto);

        //when && then
        mockMvc.perform(post("/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("reviewCount").value(0))
                .andExpect(jsonPath("score").value(0.0));
    }


}