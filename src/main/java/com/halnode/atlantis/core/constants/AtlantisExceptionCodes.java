package com.halnode.atlantis.core.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum AtlantisExceptionCodes {
    INVALID_PARAMETER(1000, "The parameter is invalid");

    private final int status;
    private final String message;

}
