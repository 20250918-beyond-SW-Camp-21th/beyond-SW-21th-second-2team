package com.whatthefork.attendancetracking.common.error;

import com.whatthefork.attendancetracking.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /* BusinessException 처리 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error("handleBusinessException 에러 발생", ex);
        log.error("BusinessException - Code: {}, Message: {}", ex.getErrorCode().getCode(), ex.getMessage());

        ErrorCode errorcode = ex.getErrorCode();

        ErrorResponse response = ErrorResponse.of(errorcode);

        return ResponseEntity
                .status(errorcode.getStatus())
                .body(response);
    }

    /* 그 외 에러 전역 처리 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("handleException 에러 발생", ex);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = ErrorResponse.of(errorCode);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
}