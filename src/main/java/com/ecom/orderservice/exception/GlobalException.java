package com.ecom.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecom.orderservice.dto.ExceptionDTO;

@RestControllerAdvice
public class GlobalException {

	@ExceptionHandler(exception = Exception.class)
	private ResponseEntity<ExceptionDTO> handleException(Exception ex) {

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ExceptionDTO.builder().status("Failed").details(ex.getMessage()).build());

	}

}