package com.sparta.reviewservice.web.exceptionHandler;

import com.sparta.reviewservice.domain.entity.Review;
import com.sparta.reviewservice.domain.exception.ProductException;
import com.sparta.reviewservice.domain.exception.ReviewException;
import com.sparta.reviewservice.domain.exception.dto.ExceptionResponseDto;
import com.sparta.reviewservice.domain.exception.errorcode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j(topic = "[GlobalExceptionHandler]")
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ExceptionResponseDto> productException (ProductException e){
        log.error("ProductException = {}", e.getMessage());
        return createExceptionResponseEntity(e.getErrorCode());
    }
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ExceptionResponseDto> reviewException (ReviewException e){
        log.error("ReviewException = {}", e.getMessage());
        return createExceptionResponseEntity(e.getErrorCode());
    }

    private ResponseEntity<ExceptionResponseDto> createExceptionResponseEntity(ErrorCode errorCode) {
        return new ResponseEntity<>(
                ExceptionResponseDto.of(errorCode),
                errorCode.getStatus());
    }
}
