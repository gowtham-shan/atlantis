package com.halnode.atlantis.core.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BaseApiController {

    @GetMapping
    public String welcome() {
        return "API CONTROLLER FOR JWT AUTHENTICATION";
    }

    @GetMapping("/get")
    public String securityCheck() {
        return "Checked";
    }
}
