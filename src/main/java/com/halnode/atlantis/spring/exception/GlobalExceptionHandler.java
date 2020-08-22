package com.halnode.atlantis.spring.exception;

import com.halnode.atlantis.core.constants.ErrorResponse;
import com.halnode.atlantis.core.exception.ResourceNotFoundException;
import com.halnode.atlantis.core.exception.UserNameAlreadyExistsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

/**
 * This class acts as an Exception Handler to the entire application.
 * We can catch any exception and send our custom response {@link ErrorResponse}.
 * We can also handle our Custom Exception via this class.
 *
 * @author gowtham
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), LocalDateTime.now(),request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException exception,HttpServletRequest request) {
        exception.getConstraintViolations().forEach(error->{
            System.out.println(error.getInvalidValue());
        });
        ErrorResponse errorResponse = new ErrorResponse(exception.getLocalizedMessage(), LocalDateTime.now(),request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ErrorResponse errorResponse = new ErrorResponse(message, LocalDateTime.now(),request.getContextPath());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNameAlreadyExistsException.class)
    public final ResponseEntity<?> handleUserNameAlreadyExistsException(UserNameAlreadyExistsException exception,WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getLocalizedMessage(), LocalDateTime.now(),request.getContextPath());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}