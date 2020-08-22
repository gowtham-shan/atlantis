package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.exception.ResourceNotFoundException;
import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import com.halnode.atlantis.spring.authentication.jwt.JwtRequest;
import com.halnode.atlantis.spring.authentication.jwt.JwtResponse;
import com.halnode.atlantis.util.JwtUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class BaseController {

    @NonNull
    private final JwtUtil jwtUtil;

    @NonNull
    private final AuthenticationManager authenticationManager;

    @GetMapping
    public ResponseEntity<?> landingPage() {
        return ResponseEntity.ok("Hello World !!!");
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("api/auth/load-user/")
    public String test(){
        return "WELCOME";
    }

    @PostMapping("api/auth/obtain-token")
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

    @NonNull
    private OrganizationRepository organizationRepository;

    @PostMapping("exception")
    public ResponseEntity<?> excption(@Valid @RequestBody Organization organization){
        return ResponseEntity.ok(organizationRepository.save(organization));
    }
}
