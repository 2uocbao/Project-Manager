package com.quocbao.projectmanager.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.quocbao.projectmanager.common.ErrorResponse;
import com.quocbao.projectmanager.common.ExceptionResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Resouce Not Found",
				LocalDateTime.now(), ex.getMessage());
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(ConstraintViolationException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		ErrorResponse.Error error = errorResponse.new Error();
		ErrorResponse.Error.Detail detail = errorResponse.new Error().new Detail();
		error.setCode("INVALID_INPUT");
		error.setMessage("The provided input is not valid.");
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
		errorResponse.setError(error);
		ex.getConstraintViolations().forEach(e -> {
			detail.setField(e.getPropertyPath().toString());
			detail.setIssue(e.getMessage());
			error.setDetail(detail);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateException.class)
	public ResponseEntity<ExceptionResponse> handleDuplicateException(DuplicateException ex) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.CONFLICT.value(),
				"The provided input already exists", LocalDateTime.now(), ex.getMessage());
		return new ResponseEntity<>(exceptionResponse, HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
