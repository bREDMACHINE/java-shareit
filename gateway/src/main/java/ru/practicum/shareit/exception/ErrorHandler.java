package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleError(final ErrorResponse e) {
        return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
    }
}
