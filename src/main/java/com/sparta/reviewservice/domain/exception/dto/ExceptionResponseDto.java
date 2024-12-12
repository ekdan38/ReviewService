package com.sparta.reviewservice.domain.exception.dto;

import com.sparta.reviewservice.domain.exception.errorcode.ErrorCode;
import lombok.Getter;

@Getter
public class ExceptionResponseDto {

    private String errorMessage;
    private String errorCode;

    private ExceptionResponseDto(final ErrorCode errorCode) {
        this.errorMessage = errorCode.getErrorMessage();
        this.errorCode = errorCode.getErrorCode();
    }

    public static ExceptionResponseDto of(final ErrorCode errorCode){
        return new ExceptionResponseDto(errorCode);
    }

}
