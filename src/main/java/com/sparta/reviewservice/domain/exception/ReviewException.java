package com.sparta.reviewservice.domain.exception;

import com.sparta.reviewservice.domain.exception.errorcode.ErrorCode;

public class ReviewException extends BusinessBaseException{

    public ReviewException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage(), errorCode);
    }
}
