package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getAllErrors().stream()
                .map(objectError -> objectError.getDefaultMessage())
                .collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError fieldError) {
            String filed = fieldError.getField();
            String massage = e.getDefaultMessage();
            return filed + " " + massage;
        }
        return e.getDefaultMessage();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>(
                "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerExceptions(NullPointerException ex) {
        return new ResponseEntity<>(
                "Null pointer exception occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
