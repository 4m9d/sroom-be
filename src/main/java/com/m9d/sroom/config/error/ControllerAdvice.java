package com.m9d.sroom.config.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(NotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorizedException(UnauthorizedException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<ErrorResponse> duplicationException(DuplicationException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NotMatchException.class)
    public ResponseEntity<ErrorResponse> notMatchException(NotMatchException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> iOException(IOException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> jsonProcessingException(JsonProcessingException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> customHandleMissingRequestValue(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        String type = ex.getParameterType();

        String error = String.format("필수 파라미터인 '%s'(%s)가 누락되었습니다.", name, type);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .message(error)
                .build();

        // 예외 메시지를 담은 새로운 예외 객체를 생성하여 반환
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<?> customHandleMissingPathVariable(MissingPathVariableException ex) {
        String name = ex.getVariableName();
        String error = String.format("필수 경로 변수인 '%s'가 누락되었습니다.", name);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .message(error)
                .build();

        // 예외 메시지를 담은 새로운 예외 객체를 생성하여 반환
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<?> InvalidParameterException(InvalidParameterException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> IllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }
}
