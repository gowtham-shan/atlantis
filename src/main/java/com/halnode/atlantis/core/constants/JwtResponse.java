package com.halnode.atlantis.core.constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {

    private static final long serialVersionUID = -8091879091924046844L;

    private final String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}
