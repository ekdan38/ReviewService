package com.sparta.reviewservice.domain.exception;

import com.sparta.reviewservice.domain.exception.errorcode.ErrorCode;

public class BusinessBaseException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessBaseException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public BusinessBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}