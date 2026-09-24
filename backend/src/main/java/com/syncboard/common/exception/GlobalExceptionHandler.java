package com.syncboard.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFound(ResourceNotFoundException e) {
        ErrorResponse response = new ErrorResponse(
                ErrorCode.RESOURCE_NOT_FOUND,
                e.getMessage(),
                Instant.now(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> resourceAlreadyExists(ResourceAlreadyExistsException e) {
        ErrorResponse response = new ErrorResponse(
                ErrorCode.RESOURCE_ALREADY_EXIST,
                e.getMessage(),
                Instant.now(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentials(InvalidCredentialsException e) {
        ErrorResponse response = new ErrorResponse(
                ErrorCode.INVALID_CREDENTIALS,
                e.getMessage(),
                Instant.now(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationError(
            MethodArgumentNotValidException e
    ) {

        // get input field + corresponding error message as map
        Map<String, String> errors =
                e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(Collectors.toMap(
                                FieldError::getField,
                                error -> Objects.requireNonNullElse(
                                        error.getDefaultMessage(),
                                        "Invalid value"
                                ),
                                (first, second) -> first
                        ));

        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_FAILED,
                "Validation failed",
                Instant.now(),
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException e) {
        ErrorResponse response = new ErrorResponse(
                ErrorCode.ACCESS_DENIED,
                e.getMessage(),
                Instant.now(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorResponse> unauthorizedOperation(UnauthorizedOperationException e) {
        ErrorResponse response = new ErrorResponse(
                ErrorCode.UNAUTHORIZED,
                e.getMessage(),
                Instant.now(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> businessRuleViolation(BusinessRuleViolationException e) {
        ErrorResponse response = new ErrorResponse(
                ErrorCode.BUSINESS_RULE_VIOLATION,
                e.getMessage(),
                Instant.now(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpectedException(Exception e) {

        //TODO: add logging
        e.printStackTrace();

        ErrorResponse response = new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Oops, something went wrong.",
                Instant.now(),
                Map.of()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
