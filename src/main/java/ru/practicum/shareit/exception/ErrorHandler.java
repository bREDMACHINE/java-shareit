package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleError(final ErrorResponse e) {
        return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleBadRequestException(final BadRequestException e) {
        return new ResponseEntity<>(Map.of("Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFoundException(final NotFoundException e) {
        return new ResponseEntity<>(Map.of("NotFound Error", e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
