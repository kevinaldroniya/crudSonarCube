package com.sonarcube.eighty.controller.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.sonarcube.eighty.dto.ErrorDetails;
import com.sonarcube.eighty.exception.InvalidRequestException;
import com.sonarcube.eighty.exception.ResourceAlreadyExistsException;
import com.sonarcube.eighty.exception.ResourceConversionException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorDetails> handleInvalidRequestException(InvalidRequestException e, WebRequest webRequest){
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .status("400")
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Field Validation Error")
                .details(e.getMessage())
                .path(webRequest.getDescription(false))
                .exception(e.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleResourceAlreadyExistException(ResourceAlreadyExistsException e, WebRequest webRequest){
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .status("400")
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Resource already exists")
                .details(e.getMessage())
                .path(webRequest.getDescription(false))
                .exception(e.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String errorMessage = getString(ex);
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .status(String.valueOf(status.value()))
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Malformed JSON request")
                .details(errorMessage)
                .path(request.getDescription(false).replace("uri=", ""))
                .exception(ex.getClass().getName())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("'%s' %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(new Date())
                .status("400")
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Field Validation Error")
                .details(message)
                .path(request.getDescription(false))
                .exception(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    private static String getString(HttpMessageNotReadableException ex) {
        String message = "Your request could not be processed due to invalid input.";

        // Try to extract the specific field name and invalid value from the exception message
        if (ex.getCause() instanceof JsonMappingException jsonEx) {
            List<JsonMappingException.Reference> references = jsonEx.getPath();

            String fieldName = references.get(0).getFieldName();
            message = String.format("Invalid value provided for field '%s'. Please ensure the value is correct and of the right type.", fieldName);
        }
        return message;
    }
}
