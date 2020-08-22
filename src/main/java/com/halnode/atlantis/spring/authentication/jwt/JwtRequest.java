package com.halnode.atlantis.spring.authentication.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class JwtRequest implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;

    @NotEmpty(message = "Username should not be null or empty")
    private String userName;

    @NotEmpty(message = "Password should not be null or empty")
    private String password;
}
