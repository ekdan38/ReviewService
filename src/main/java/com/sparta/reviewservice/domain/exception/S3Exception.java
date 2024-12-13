package com.sparta.reviewservice.domain.exception;

import com.sparta.reviewservice.domain.exception.errorcode.ErrorCode;

public class S3Exception extends BusinessBaseException{
    public S3Exception(ErrorCode errorCode) {
        super(errorCode.getErrorMessage(), errorCode);
    }

}
