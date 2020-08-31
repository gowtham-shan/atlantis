package com.halnode.atlantis.spring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AtlantisExceptionCode {
    TEST_EXCEPTION(1000, "Sample Exception For Testing");

    private final int status;
    private final String message;

}
