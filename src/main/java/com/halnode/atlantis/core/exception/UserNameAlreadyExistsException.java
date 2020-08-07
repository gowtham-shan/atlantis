package com.halnode.atlantis.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNameAlreadyExistsException extends RuntimeException {

    private String message;

    public UserNameAlreadyExistsException() {
        super();
    }

    public UserNameAlreadyExistsException(String message) {
        this.message = message;
    }
}
