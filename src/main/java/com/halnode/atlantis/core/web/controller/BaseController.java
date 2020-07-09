package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.constants.JwtRequest;
import com.halnode.atlantis.core.constants.JwtResponse;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.spring.authentication.UserDetailsServiceImpl;
import com.halnode.atlantis.util.JwtUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

import javax.persistence.EntityManagerFactory;

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

    @PostMapping("/auth/obtain-token")
    public ResponseEntity<?> authentication(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUserName(), jwtRequest.getPassword()));
            final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUserName());
            final String jwtToken = jwtUtil.createToken(jwtRequest.getUserName());
            return ResponseEntity.ok(new JwtResponse(jwtToken));
        } catch (BadCredentialsException e) {
            //TODO: have to write our own exception handler
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad Credentials");
        } catch (Exception e) {
            return null;
        }
    }

    @NonNull
    private final EntityManagerFactory entityManagerFactory;

    @NonNull
    private final UserRepository userRepository;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        entityManager.getTransaction().begin();
//        Query query = entityManager.createNativeQuery("SET SCHEMA \'Halnode\'");
//        query.executeUpdate();
        User userList = userRepository.findByUserName("admin");

        //entityManager.close();
        return ResponseEntity.ok(userList);
    }
}
