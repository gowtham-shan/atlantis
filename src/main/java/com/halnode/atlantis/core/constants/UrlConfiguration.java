package com.halnode.atlantis.core.constants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UrlConfiguration {

    private String url;
    private String method;
    private List<String> roles;

}
