package com.sparta.reviewservice.domain.exception;

import com.sparta.reviewservice.domain.exception.errorcode.ErrorCode;

public class ProductException extends BusinessBaseException{
    public ProductException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage(), errorCode);
    }
}
