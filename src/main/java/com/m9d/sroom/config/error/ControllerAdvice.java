package com.m9d.sroom.config.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse notFoundException(NotFoundException e) {
        log.warn("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage());
        return ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ErrorResponse unauthorizedException(UnauthorizedException e) {
        log.warn("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage());
        return ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicationException.class)
    public ErrorResponse duplicationException(DuplicationException e) {
        log.warn("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage());
        return ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotMatchException.class)
    public ErrorResponse notMatchException(NotMatchException e) {
        log.warn("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage());
        return ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse customHandleMissingRequestValue(MissingServletRequestParameterException e) {
        String name = e.getParameterName();
        String type = e.getParameterType();
        log.info("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage());

        String error = String.format("필수 파라미터인 '%s'(%s)가 누락되었습니다.", name, type);
        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(error)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public ErrorResponse customHandleMissingPathVariable(MissingPathVariableException e) {
        String name = e.getVariableName();
        String error = String.format("필수 경로 변수인 '%s'가 누락되었습니다.", name);
        log.info("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage());

        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(error)
                .build();

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidParameterException.class)
    public ErrorResponse InvalidParameterException(InvalidParameterException e) {
        log.warn("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage());
        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse IllegalArgumentException(IllegalArgumentException e) {
        log.warn("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse argumentNotValidException(MethodArgumentNotValidException e){
        log.warn("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage(), e);

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> errorMessages = fieldErrors.stream()
                .map(FieldError::getField)
                .collect(Collectors.toList());

        String message = String.format("다음 필드에 대한 에러입니다 : %s", String.join(", ", errorMessages));
        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }
}
