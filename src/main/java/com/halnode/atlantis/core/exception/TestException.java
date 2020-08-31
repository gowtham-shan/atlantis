package com.halnode.atlantis.core.exception;

import com.halnode.atlantis.spring.exception.AtlantisError;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestException extends RuntimeException {

    private AtlantisError error;

    public TestException() {

    }

    public TestException(AtlantisError error) {
        this.error = error;
    }
}
