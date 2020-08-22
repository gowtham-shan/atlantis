package com.halnode.atlantis.core.constants;

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
public class ErrorResponse {

    private String message;

    private LocalDateTime timestamp;

    private String url;

    //package error class

}
