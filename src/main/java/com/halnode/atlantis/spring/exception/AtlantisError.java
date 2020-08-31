package com.halnode.atlantis.spring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AtlantisError {

    private String moduleName = "atlantis";

    private String message;

    private int code;

    private List<String> errors;
}
