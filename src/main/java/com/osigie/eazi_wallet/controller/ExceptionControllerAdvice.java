package com.osigie.eazi_wallet.controller;

import com.osigie.eazi_wallet.dto.response.ErrorResponseDto;
import com.osigie.eazi_wallet.exception.CurrencyMismatchException;
import com.osigie.eazi_wallet.exception.DuplicateTransactionException;
import com.osigie.eazi_wallet.exception.InsufficientFundsException;
import com.osigie.eazi_wallet.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<Map<String, String>>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<Map<String, String>> errorList = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            Map<String, String> errorDetails = new HashMap<>();
            errorDetails.put("field", fieldName);
            errorDetails.put("message", errorMessage);
            errorList.add(errorDetails);
        });

        Map<String, List<Map<String, String>>> errors = new HashMap<>();
        errors.put("errors", errorList);

        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.NOT_FOUND, ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateTransactionException(DuplicateTransactionException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.CONFLICT, ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficientFundsException(InsufficientFundsException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(CurrencyMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleCurrencyMismatchException(CurrencyMismatchException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoHandlerFoundException(final NoHandlerFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.NOT_FOUND, "Resource not found", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);

    }


    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidJson(HttpMessageNotReadableException ex) {
        ErrorResponseDto error =
                new ErrorResponseDto(HttpStatus.BAD_REQUEST, "Invalid request body", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch() {
        ErrorResponseDto error =
                new ErrorResponseDto(HttpStatus.BAD_REQUEST, "Invalid request parameter", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation() {
        ErrorResponseDto error =
                new ErrorResponseDto(HttpStatus.BAD_REQUEST, "Invalid request data", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpectedError(Throwable ex) {

        log.error("Unexpected error", ex);

        ErrorResponseDto error =
                new ErrorResponseDto(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
