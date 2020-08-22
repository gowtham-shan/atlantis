package com.halnode.atlantis.spring.authorization;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AuthorizationConfig {

    private String url;
    private List<String> methods;
    private List<String> roles;

}
