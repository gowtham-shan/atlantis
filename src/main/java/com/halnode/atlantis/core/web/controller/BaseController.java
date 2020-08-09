package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.constants.JwtRequest;
import com.halnode.atlantis.core.constants.JwtResponse;
import com.halnode.atlantis.spring.authentication.UserDetailsServiceImpl;
import com.halnode.atlantis.util.JwtUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class BaseController {

    @NonNull
    private final JwtUtil jwtUtil;

    @NonNull
    private final AuthenticationManager authenticationManager;

    @NonNull
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping
    public String landingPage() {
        return "landing";
    }

    @PostMapping("/auth/obtain-token")
    public ResponseEntity<?> authentication(@Valid @RequestBody JwtRequest jwtRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUserName(), jwtRequest.getPassword()));
            final String jwtToken = jwtUtil.createToken(jwtRequest.getUserName());
            return ResponseEntity.ok(new JwtResponse(jwtToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }
}
