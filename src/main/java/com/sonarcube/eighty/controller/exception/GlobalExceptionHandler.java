package com.sonarcube.eighty.controller.exception;

import com.sonarcube.eighty.dto.ErrorDetails;
import com.sonarcube.eighty.exception.ResourceConversionException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceConversionException.class)
    public ResponseEntity<ErrorDetails> handleResourceConversionException(ResourceConversionException ex, WebRequest webRequest) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .status("500")
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Resource conversion error")
                .details(ex.getMessage())
                .path(webRequest.getDescription(false))
                .exception(ex.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException notFoundException, WebRequest webRequest) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .status("404")
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message("Resource not found")
                .details(notFoundException.getMessage())
                .path(webRequest.getDescription(false))
                .exception(notFoundException.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e, WebRequest webRequest) {
        String requiredType = Optional.ofNullable(e.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("Unknown Type");

        String errorMessage = String.format("Failed to convert value : '%s' to required type : '%s'", e.getValue(), requiredType);
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .status("400")
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getErrorCode())
                .details(errorMessage)
                .path(webRequest.getDescription(false))
                .exception(e.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
