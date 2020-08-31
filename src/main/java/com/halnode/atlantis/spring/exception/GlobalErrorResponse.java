package com.halnode.atlantis.spring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Generic class for sending error messages when exception occurred
 *
 * @author gowtham
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GlobalErrorResponse {

    private LocalDateTime timestamp;

    private String url;

    private Object apiError;
}
