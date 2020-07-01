package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.constants.JwtRequest;
import com.halnode.atlantis.core.constants.JwtResponse;
import com.halnode.atlantis.spring.UserDetailsServiceImpl;
import com.halnode.atlantis.util.JwtUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class BaseController {

    @NonNull
    private final JwtUtil jwtUtil;

    @NonNull
    private AuthenticationManager authenticationManager;

    @NonNull
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping
    public String landingPage() {
        return "landing";
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authentication(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUserName(), jwtRequest.getPassword()));
        } catch (BadCredentialsException e) {
            //TODO: have to write our own exception handler
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUserName());
        System.out.println(userDetails.getUsername());
        final String jwtToken = jwtUtil.createToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(jwtToken));
    }
}
