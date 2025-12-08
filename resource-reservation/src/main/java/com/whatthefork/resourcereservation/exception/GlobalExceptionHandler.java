package com.whatthefork.resourcereservation.exception;

import com.whatthefork.resourcereservation.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {

        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Void> response =
                ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }
}
