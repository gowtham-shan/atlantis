package com.halnode.atlantis.spring.exception;

import com.halnode.atlantis.core.exception.TestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * This class acts as an Exception Handler to the entire application.
 * We can catch any exception and send our custom response {@link GlobalErrorResponse}.
 * We can also handle our Custom Exception via this class.
 *
 * @author gowtham
 */
@ControllerAdvice
public class AtlantisExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TestException.class)
    public final ResponseEntity<?> handleUserNameAlreadyExistsException(TestException exception, HttpServletRequest request) {
        AtlantisError error = new AtlantisError();
        error.setCode(AtlantisExceptionCode.TEST_EXCEPTION.getStatus());
        error.setMessage(AtlantisExceptionCode.TEST_EXCEPTION.getMessage());
        error.setErrors(Arrays.asList("Testing the exception controls"));
        GlobalErrorResponse globalErrorResponse = new GlobalErrorResponse();
        globalErrorResponse.setTimestamp(LocalDateTime.now());
        globalErrorResponse.setUrl(request.getRequestURI());
        globalErrorResponse.setApiError(error);
        return new ResponseEntity<>(globalErrorResponse, HttpStatus.BAD_REQUEST);
    }
}