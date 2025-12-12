package com.whatthefork.resourcereservation.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    public final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
