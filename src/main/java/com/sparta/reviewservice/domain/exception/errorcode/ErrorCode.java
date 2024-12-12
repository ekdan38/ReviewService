package com.sparta.reviewservice.domain.exception.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_001", "존재하지 않는 상품입니다."),

    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "REVIEW_001", "이미 해당 상품에 리뷰를 작성했습니다.")
    ;


    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;

    ErrorCode(HttpStatus status, String errorCode, String errorMessage) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
