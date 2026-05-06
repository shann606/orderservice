package com.ecom.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecom.orderservice.dto.ExceptionDTO;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalException {

	@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDTO> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ExceptionDTO.builder()
                                               .status("Failed")
                                               .details(ex.getMessage())
                                               .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleAllExceptions(Exception ex) {
        log.error("Exception occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ExceptionDTO.builder()
                                               .status("Failed")
                                               .details(ex.getMessage())
                                               .build());
    }
}