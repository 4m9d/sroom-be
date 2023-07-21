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

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse notFoundException(NotFoundException e) {
        return ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ErrorResponse unauthorizedException(UnauthorizedException e) {
        return ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DuplicationException.class)
    public ErrorResponse duplicationException(DuplicationException e) {
        return ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(NotMatchException.class)
    public ErrorResponse notMatchException(NotMatchException e) {
        return ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IOException.class)
    public ErrorResponse iOException(IOException e) {
        return ErrorResponse.builder()
                .statusCode(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(JsonProcessingException.class)
    public ErrorResponse jsonProcessingException(JsonProcessingException e) {
        return ErrorResponse.builder()
                .statusCode(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse customHandleMissingRequestValue(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        String type = ex.getParameterType();

        String error = String.format("필수 파라미터인 '%s'(%s)가 누락되었습니다.", name, type);
        return ErrorResponse.builder()
                .statusCode(BAD_REQUEST.value())
                .message(error)
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public ErrorResponse customHandleMissingPathVariable(MissingPathVariableException ex) {
        String name = ex.getVariableName();
        String error = String.format("필수 경로 변수인 '%s'가 누락되었습니다.", name);

        return ErrorResponse.builder()
                .statusCode(BAD_REQUEST.value())
                .message(error)
                .build();

    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(InvalidParameterException.class)
    public ErrorResponse InvalidParameterException(InvalidParameterException e) {
        return ErrorResponse.builder()
                .statusCode(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse IllegalArgumentException(IllegalArgumentException e) {
        return ErrorResponse.builder()
                .statusCode(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse handleInvalidParameters(BindException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        String message = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.info("Exception: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage());
        return ErrorResponse.builder()
                .statusCode(BAD_REQUEST.value())
                .message(message)
                .build();
    }
}
