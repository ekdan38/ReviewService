package com.sparta.reviewservice.domain.exception.errorcode;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_001", "존재하지 않는 상품입니다."),

    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "REVIEW_001", "이미 해당 상품에 리뷰를 작성했습니다."),

    S3_EMPTY_FILE(HttpStatus.BAD_REQUEST, "S3_001", "이미지 파일이 비어 있습니다."),
    S3_NO_EXTENSION(HttpStatus.BAD_REQUEST, "S3_001", "확장자가 없습니다."),
    S3_NOT_SUPPORT_EXTENSION(HttpStatus.BAD_REQUEST, "S3_001", "지원하지 않는 확장자입니다.")
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
