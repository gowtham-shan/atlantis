package com.halnode.atlantis.core.constants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtRequest {
    private static final long serialVersionUID = 5926468583005150707L;

    private String userName;

    private String password;
}
