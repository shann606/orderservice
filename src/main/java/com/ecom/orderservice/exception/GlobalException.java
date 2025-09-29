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

	@ExceptionHandler(exception = Exception.class)
	private ResponseEntity<ExceptionDTO> handleException(Exception ex) {

		log.info("============Exception Occured=====================");
		ex.printStackTrace();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ExceptionDTO.builder().status("Failed").details(ex.getMessage()).build());

	}

}